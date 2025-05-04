package com.tristanphan.sdkl.reader

import com.tristanphan.sdkl.TypeIdentifier
import com.tristanphan.sdkl.utilities.BufferedRandomAccessFile
import com.tristanphan.sdkl.utilities.bigEndianByteArrayToLong
import java.io.File
import java.io.RandomAccessFile
import java.util.*

private const val SIZE_BITS = 32

internal class DictReader(dictFile: File, info: InfoReader) {
    private val info: InfoReader = info
    private val file: RandomAccessFile = BufferedRandomAccessFile(dictFile)

    fun lookup(offset: Long, size: Int): Map<TypeIdentifier, String> {
        return if (info.sametypesequence != null) readWithPredefinedTypeSequence(
            offset,
            offset + size,
            info.sametypesequence
        )
        else readWithInlineTypeSequence(
            offset,
            offset + size,
        )
    }

    private fun readWithPredefinedTypeSequence(
        startPosition: Long,
        endPosition: Long,
        sequence: List<TypeIdentifier>,
    ): Map<TypeIdentifier, String> {
        val definitions = EnumMap<TypeIdentifier, String>(TypeIdentifier::class.java)
        file.seek(startPosition)
        for (type in sequence) {
            assert(!definitions.containsKey(type)) {
                "ASSUMPTION: Any given word should NOT have more than 1 definition of the same type"
            }
            val remainingCharactersAllotted = (endPosition - file.filePointer).toInt()
            // TODO: Maybe this should be ByteArray instead of String to allow for other types?
            definitions[type] = String(readDataOfType(type, remainingCharactersAllotted))
        }
        return definitions
    }

    private fun readWithInlineTypeSequence(
        startPosition: Long,
        endPosition: Long,
    ): Map<TypeIdentifier, String> {
        file.seek(startPosition)
        // TODO: Implement the case where a sametypesequence is not provided
        // This should be calling [readDataOfType], just as [readWithPredefinedTypeSequence] does
        // We need to read until [endPosition] is reached, as there are varying number of sequences
        throw NotImplementedError()
    }

    private fun readDataOfType(
        type: TypeIdentifier,
        remainingCharactersAllotted: Int
    ): ByteArray {
        if (type.fieldIsNullTerminated()) {
            var charactersLeft = remainingCharactersAllotted
            val byteList = ArrayList<Byte>(256)
            while (charactersLeft > 0) {
                val byte = file.read()
                if (byte == 0x0) break
                --charactersLeft
                byteList.add(byte.toByte())
            }
            return byteList.toByteArray()
        } else {
            val sizeArray = ByteArray(SIZE_BITS / 8)
            file.read(sizeArray)
            val size = bigEndianByteArrayToLong(sizeArray).toInt()
            val dataArray = ByteArray(size)
            file.read(dataArray)
            return dataArray
        }
    }
}


