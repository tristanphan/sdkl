package com.tristanphan.stardict.reader

import com.tristanphan.bigEndianByteArrayToLong
import com.tristanphan.stardict.StarDictVersion
import java.io.EOFException
import java.io.File
import java.io.FileInputStream

private const val DEFAULT_OFFSET_BITS = 32
private const val DEFAULT_SIZE_BITS = 32

class IndexReader(file: File, info: InfoReader) {

    // TODO: Change this into a list (assume sorted) and use binary search
    val words = HashMap<String, Pair<Long, Int>>()

    init {
        val offsetBits =
            if (info.version == StarDictVersion.V3_0_0 && info.idxoffsetbits == 64) info.idxoffsetbits
            else DEFAULT_OFFSET_BITS
        val offsetBytes = offsetBits / 8
        val sizeBytes = DEFAULT_SIZE_BITS / 8

        file.inputStream().use { inputStream ->
            while (true) {
                try {
                    val word = readNullTerminatedWord(inputStream)
                    val offsetByteArray = readNBytesWithEOF(inputStream, offsetBytes)
                    val sizeByteArray = readNBytesWithEOF(inputStream, sizeBytes)
                    val offset = bigEndianByteArrayToLong(offsetByteArray)
                    val size = bigEndianByteArrayToLong(sizeByteArray).toInt()
                    words[word] = Pair(offset, size)
                } catch (_: EOFException) {
                    break
                }
            }
        }
    }
}

private fun readNullTerminatedWord(stream: FileInputStream): String {
    val bytes = ArrayList<Byte>()
    while (true) {
        val byte = readNBytesWithEOF(stream, 1)[0]
        if (byte == 0x0.toByte()) break
        bytes.add(byte)
    }
    val byteArray: ByteArray = bytes.toByteArray()
    return String(byteArray)
}

private fun readNBytesWithEOF(stream: FileInputStream, n: Int): ByteArray {
    val byteArray = stream.readNBytes(n)
    if (byteArray.size != n) throw EOFException()
    return byteArray
}
