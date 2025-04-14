package com.tristanphan.utilities

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile

const val BUFFER_SIZE = 256

class BufferedRandomAccessFile(file: File) : RandomAccessFile(file, "r") {

    private var buffer: BufferedInputStream = BufferedInputStream(FileInputStream(this.fd), BUFFER_SIZE)
    private var bufferPosition: Long = 0

    override fun read(): Int {
        return buffer.read()
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return buffer.read(b, off, len)
    }

    override fun read(b: ByteArray): Int {
        return buffer.read(b)
    }

    override fun seek(pos: Long) {
        super.seek(pos)
        regenerateBuffer()
    }

    override fun getFilePointer(): Long {
        return super.getFilePointer() + bufferPosition
    }

    override fun skipBytes(n: Int): Int {
        val result = super.skipBytes(n)
        regenerateBuffer()
        return result
    }

    override fun close() {
        super.close()
        buffer.close()
    }

    private fun regenerateBuffer() {
        buffer = BufferedInputStream(FileInputStream(this.fd), BUFFER_SIZE)
        bufferPosition = 0
    }
}