package com.tristanphan.stardict.writer

import com.tristanphan.stardict.TypeIdentifier
import com.tristanphan.utilities.longToBigEndianByteArray
import java.io.BufferedOutputStream
import java.io.File

private const val SIZE_BITS = 32

class DictWriter(file: File, info: InfoWriter) {
    private val info: InfoWriter = info
    private var filePosition: Long = 0
    val openFile: BufferedOutputStream

    init {
        file.delete()
        openFile = file.outputStream().buffered(bufferSize = 2_097_152)
    }

    fun addWord(definitions: Map<TypeIdentifier, String>): Pair<Long, Int> {
        assert(info.sametypesequence != null) { "Writer without sametypesequence not implemented yet!" }
        val offset = filePosition
        var size = 0
        for ((index, typeSequence) in info.sametypesequence!!.withIndex()) {
            val isLast = index == info.sametypesequence.size - 1
            val definition = definitions[typeSequence]!!.toByteArray()
            if (typeSequence.fieldIsNullTerminated()) {
                openFile.write(definition)
                if (!isLast) {
                    openFile.write(byteArrayOf(0x0.toByte()))
                }
                size += definition.size + (if (isLast) 0 else 1)
            } else {
                val sizeBytes = SIZE_BITS / 8
                openFile.write(longToBigEndianByteArray(definition.size.toLong(), sizeBytes))
                openFile.write(definition)
                size += sizeBytes + definition.size
            }
        }
        filePosition += size
        return Pair(offset, size)
    }

    fun finish() {
        openFile.close()
    }
}


