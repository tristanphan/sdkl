package com.tristanphan.stardict.writer

import com.tristanphan.longToBigEndianByteArray
import com.tristanphan.stardict.TypeIdentifier
import java.io.File
import java.io.RandomAccessFile

private const val SIZE_BITS = 32

class DictWriter(file: File, info: InfoWriter) {
    private val dictFile: File = file
    private val info: InfoWriter = info
    private var filePosition: Long = 0

    init {
        file.delete()
    }

    fun addWord(definitions: HashMap<TypeIdentifier, String>): Pair<Long, Int> {
        assert(info.sametypesequence != null) { "Writer without sametypesequence not implemented yet!" }
        val offset = filePosition
        var size = 0
        useFile { file ->
            file.seek(offset)
            for ((index, typeSequence) in info.sametypesequence!!.withIndex()) {
                val isLast = index == info.sametypesequence.size - 1
                val definition = definitions[typeSequence]!!.toByteArray()
                if (typeSequence.fieldIsNullTerminated()) {
                    file.write(definition)
                    if (!isLast) {
                        file.write(byteArrayOf(0x0.toByte()))
                    }
                    size += definition.size + (if (isLast) 0 else 1)
                } else {
                    val sizeBytes = SIZE_BITS / 8
                    file.write(longToBigEndianByteArray(definition.size.toLong(), sizeBytes))
                    file.write(definition)
                    size += sizeBytes + definition.size
                }
            }
        }
        filePosition += size
        return Pair(offset, size)
    }

    private inline fun <R> useFile(block: (RandomAccessFile) -> R): R {
        val file = RandomAccessFile(dictFile, "rw")
        file.use { f ->
            return block(f)
        }
    }

}


