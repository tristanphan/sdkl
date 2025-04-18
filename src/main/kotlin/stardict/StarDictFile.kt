package com.tristanphan.stardict

import java.io.File

class StarDictFile(
    baseFileName: String,
    indexIsCompressed: Boolean = false,
    dictIsCompressed: Boolean = false
) {
    val info: File
    val index: File
    val dict: File
    private val baseFileName: String

    init {
        val indexExtension = if (indexIsCompressed) ".idx.gz" else ".idx"
        val dictExtension = if (dictIsCompressed) ".dict.dz" else ".dict"
        this.baseFileName = baseFileName.trimEnd('/')

        info = File("${this.baseFileName}.ifo")
        index = File("${this.baseFileName}$indexExtension")
        dict = File("${this.baseFileName}$dictExtension")
    }

    fun withSuffix(suffix: String): StarDictFile {
        return StarDictFile("$baseFileName$suffix")
    }

    fun copyTo(targetFile: StarDictFile, overwrite: Boolean, bufferSize: Int = DEFAULT_BUFFER_SIZE): StarDictFile {
        info.copyTo(targetFile.info, overwrite = overwrite, bufferSize = bufferSize)
        index.copyTo(targetFile.index, overwrite = overwrite, bufferSize = bufferSize)
        dict.copyTo(targetFile.dict, overwrite = overwrite, bufferSize = bufferSize)
        return targetFile
    }

    fun renameTo(targetFile: StarDictFile): Boolean {
        return info.renameTo(targetFile.info)
                && index.renameTo(targetFile.index)
                && dict.renameTo(targetFile.dict)
    }

    fun delete(): Boolean {
        return info.delete() && index.delete() && dict.delete()
    }
}