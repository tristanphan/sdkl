package com.tristanphan.stardict.reader

import com.tristanphan.bigEndianByteArrayToLong
import com.tristanphan.stardict.TypeIdentifier
import java.io.File
import java.io.RandomAccessFile

private const val SIZE_BITS = 32

class DictReader(file: File, info: InfoReader) {
    private val dictFile: File = file
    private val info: InfoReader = info

    fun lookup(offset: Long, size: Int): HashMap<TypeIdentifier, String> {
        useFile { file ->
            return if (info.sametypesequence != null) readWithPredefinedTypeSequence(
                file,
                offset,
                offset + size,
                info.sametypesequence
            )
            else readWithInlineTypeSequence(
                file,
                offset,
                offset + size,
            )
        }
    }

    private inline fun <R> useFile(block: (RandomAccessFile) -> R): R {
        val file = RandomAccessFile(dictFile, "r")
        file.use { f ->
            return block(f)
        }
    }

    private fun readWithPredefinedTypeSequence(
        file: RandomAccessFile,
        startPosition: Long,
        endPosition: Long,
        sequence: List<TypeIdentifier>,
    ): HashMap<TypeIdentifier, String> {
        val definitions = HashMap<TypeIdentifier, String>()
        file.seek(startPosition)
        for (type in sequence) {
            assert(!definitions.containsKey(type)) {
                "ASSUMPTION: Any given word should NOT have more than 1 definition of the same type"
            }
            val remainingCharactersAllotted = (endPosition - file.filePointer).toInt()
            // TODO: Maybe this should be ByteArray instead of String to allow for other types?
            definitions[type] = String(readDataOfType(file, type, remainingCharactersAllotted))
        }
        return definitions
    }

    private fun readWithInlineTypeSequence(
        file: RandomAccessFile,
        startPosition: Long,
        endPosition: Long,
    ): HashMap<TypeIdentifier, String> {
        file.seek(startPosition)
        // TODO: Implement the case where a sametypesequence is not provided
        // This should be calling [readDataOfType], just as [readWithPredefinedTypeSequence] does
        // We need to read until [endPosition] is reached, as there are varying number of sequences
        throw NotImplementedError()
    }

    private fun readDataOfType(
        file: RandomAccessFile,
        type: TypeIdentifier,
        remainingCharactersAllotted: Int
    ): ByteArray {
        if (type.fieldIsNullTerminated()) {
            var charactersLeft = remainingCharactersAllotted
            val character = ByteArray(1)
            val byteList = ArrayList<Byte>()
            while (charactersLeft > 0) {
                file.read(character)
                if (character[0] == 0x0.toByte()) break
                --charactersLeft
                byteList.add(character[0])
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


