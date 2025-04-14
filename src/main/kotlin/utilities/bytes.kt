package com.tristanphan.utilities

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

fun longToBigEndianByteArray(long: Long, arraySize: Int): ByteArray {
    val buffer = ByteBuffer.allocate(8)
    // Using little endian because it will be reversed soon
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.putLong(long)
    val byteArray = buffer.array().copyOf(arraySize).reversedArray()
    if (bigEndianByteArrayToLong(byteArray) != long) {
        throw Exception("Long $long is too large for a ${arraySize}-byte array!")
    }
    return byteArray
}
