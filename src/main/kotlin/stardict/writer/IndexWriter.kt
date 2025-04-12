package com.tristanphan.stardict.writer

import com.tristanphan.longToBigEndianByteArray
import com.tristanphan.stardict.StarDictVersion
import java.io.File
import java.io.FileOutputStream

private const val DEFAULT_SIZE_BITS = 32

class IndexWriter(val file: File, val info: InfoWriter) {

    val words = HashMap<String, Pair<Long, Int>>()

    fun addWord(word: String, offset: Long, size: Int) {
        words[word] = Pair(offset, size)
    }

    fun write(): Int{
        assert(info.version == StarDictVersion.V3_0_0 && info.idxoffsetbits == 64)

        val offsetBytes = (info.idxoffsetbits ?: 32) / 8
        val sizeBytes = DEFAULT_SIZE_BITS / 8

        file.delete()
        file.createNewFile()
        var filesize = 0
        for ((word, pair) in words.toSortedMap(caselessComparator)) {
            val (offset, size) = pair
            val wordByteArray = word.toByteArray()
            val offsetByteArray = longToBigEndianByteArray(offset, offsetBytes)
            val sizeByteArray = longToBigEndianByteArray(size.toLong(), sizeBytes)

            FileOutputStream(file, true).use { writer ->
                writer.write(wordByteArray)
                writer.write(byteArrayOf(0x0.toByte()))
                writer.write(offsetByteArray)
                writer.write(sizeByteArray)
            }
            filesize += wordByteArray.size + 1 + offsetByteArray.size + sizeByteArray.size
        }
        return filesize
    }
}

val caselessComparator = java.util.Comparator { s1: String, s2: String ->
    val s1lowercase = s1.lowercase()
    val s2lowercase = s1.lowercase()
    if (s1lowercase > s2lowercase) return@Comparator 1
    else if (s1lowercase < s2lowercase) return@Comparator -1
    else {
        if (s1 > s2) return@Comparator 1
        else if (s1 < s2) return@Comparator -1
        else return@Comparator 0
    }
}
