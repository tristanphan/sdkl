package com.tristanphan.sdkl.writer

import com.tristanphan.sdkl.StarDictVersion
import com.tristanphan.sdkl.utilities.caselessComparator
import com.tristanphan.sdkl.utilities.combineNavigableSets
import com.tristanphan.sdkl.utilities.longToBigEndianByteArray
import java.io.File
import java.io.FileOutputStream
import java.util.*

private const val DEFAULT_SIZE_BITS = 32

internal class IndexWriter(val file: File, val info: InfoWriter) {

    val words = TreeMap<String, Pair<Long, Int>>(caselessComparator)
    val aliases = TreeMap<String, String>(caselessComparator)

    fun addWord(word: String, offset: Long, size: Int) {
        val previousValue = words.put(word, Pair(offset, size))
        assert(previousValue == null)
        // TODO: [Reader AND Writer] It should be possible for 2 entries to have the same word string
    }

    fun addAlias(originalWord: String, newWord: String) {
        val previousValue = aliases.put(newWord, originalWord)
        assert(previousValue == null)
    }

    /**
     * Writes and finalizes the index file
     * @return Pair(index file size, word count)
     */
    fun write(): Pair<Int, Int> {
        assert(info.version == StarDictVersion.V3_0_0 && (info.idxoffsetbits ?: 32) == 32)

        val offsetBytes = (info.idxoffsetbits ?: 32) / 8
        val sizeBytes = DEFAULT_SIZE_BITS / 8

        file.delete()
        file.parentFile?.mkdirs()
        file.createNewFile()
        var fileSize = 0
        var wordCount = 0
        val allWords = combineNavigableSets(words.navigableKeySet(), aliases.navigableKeySet())
        FileOutputStream(file, true).buffered(bufferSize = 2_097_152).use { writer ->
            for (word in allWords) {
                val actualWordToConsider = aliases[word] ?: word
                if (!words.contains(actualWordToConsider)) {
                    System.err.println(
                        "[Warning] Redirect target not found: \"$actualWordToConsider\"" +
                                " (from \"$word\"). This may occur if the page is" +
                                " outside the dump (e.g. different namespace)."
                    )
                    continue
                }
                val (offset, size) = words[actualWordToConsider]!!

                val wordByteArray = word.toByteArray()
                val offsetByteArray = longToBigEndianByteArray(offset, offsetBytes)
                val sizeByteArray = longToBigEndianByteArray(size.toLong(), sizeBytes)

                writer.write(wordByteArray)
                writer.write(byteArrayOf(0x0.toByte()))
                writer.write(offsetByteArray)
                writer.write(sizeByteArray)
                ++wordCount
                fileSize += wordByteArray.size + 1 + offsetByteArray.size + sizeByteArray.size
            }
        }
        return Pair(fileSize, wordCount)
    }
}
