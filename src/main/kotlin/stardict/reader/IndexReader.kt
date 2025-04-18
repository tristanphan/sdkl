package com.tristanphan.stardict.reader

import com.tristanphan.stardict.StarDictVersion
import com.tristanphan.utilities.ProgressTracker
import com.tristanphan.utilities.bigEndianByteArrayToLong
import java.io.EOFException
import java.io.File
import java.io.InputStream

private const val DEFAULT_OFFSET_BITS = 32
private const val DEFAULT_SIZE_BITS = 32

class IndexReader(file: File, info: InfoReader) : Iterable<String> {

    // TODO: Change this into a TreeMap or list (assume already sorted) and use binary search
    val words = HashMap<String, Pair<Long, Int>>()

    init {
        val offsetBits =
            if (info.version == StarDictVersion.V3_0_0 && info.idxoffsetbits == 64) info.idxoffsetbits
            else DEFAULT_OFFSET_BITS
        val offsetBytes = offsetBits / 8
        val sizeBytes = DEFAULT_SIZE_BITS / 8

        val progress = ProgressTracker(
            "Loading index progress: ",
            total = info.wordcount.toLong(),
        )
        file.inputStream().buffered(bufferSize = 2_097_152).use { inputStream ->
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
                progress.increment()
            }
        }
        progress.finish()
    }

    override fun iterator(): Iterator<String> = iterator {
        yieldAll(elements = words.keys)
    }
}

private fun readNullTerminatedWord(stream: InputStream): String {
    val bytes = ArrayList<Byte>(256)
    while (true) {
        val byte = stream.read()
        if (byte == -1) throw EOFException()
        if (byte == 0x0) break
        bytes.add(byte.toByte())
    }
    val byteArray: ByteArray = bytes.toByteArray()
    return String(byteArray)
}

private fun readNBytesWithEOF(stream: InputStream, n: Int): ByteArray {
    val byteArray = stream.readNBytes(n)
    if (byteArray.size != n) throw EOFException()
    return byteArray
}
