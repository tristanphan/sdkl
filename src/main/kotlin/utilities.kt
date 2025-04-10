package com.tristanphan

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun bigEndianByteArrayToLong(byteArray: ByteArray): Long {
    val littleEndianArray = byteArray.reversedArray()
    val paddedArray = littleEndianArray.copyOf(8)
    val buffer = ByteBuffer.wrap(paddedArray)
    // Using little endian because it was reversed already
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    return buffer.getLong()
}
