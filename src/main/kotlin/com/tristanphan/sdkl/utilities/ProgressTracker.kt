package com.tristanphan.sdkl.utilities

import java.io.PrintStream

private const val ERASE = "\u001b[K"

internal class ProgressTracker(
    private val prefix: String,
    private val total: Long? = null,
    private val updateFrequency: Long = total?.div(100) ?: 1,
    private val printStream: PrintStream = System.err
) {
    var progress: Long = 0

    fun increment() = incrementBy(1)

    fun incrementBy(number: Long) {
        progress += number
        if (progress % updateFrequency == 0L) {
            updatePrint()
        }
    }

    fun updatePrint() {
        if (total != null) {
            printStream.printf("$ERASE$prefix%,d / %,d\r", progress, total)
        } else {
            printStream.printf("$ERASE$prefix%,d\r", progress)
        }
        printStream.flush()
    }

    fun finish() {
        updatePrint()
        printStream.println()
    }
}