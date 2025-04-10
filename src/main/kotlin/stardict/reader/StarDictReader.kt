package com.tristanphan.stardict.reader

import com.tristanphan.stardict.TypeIdentifier
import java.io.File

class StarDictReader(ifoFile: File, idxFile: File, dictFile: File) {
    private val info: InfoReader = InfoReader(ifoFile)
    private val index: IndexReader = IndexReader(idxFile, info)
    private val dictionary: DictReader = DictReader(dictFile, info)

    fun lookup(word: String): HashMap<TypeIdentifier, String>? {
        val (offset, size) = index.words[word] ?: return null
        return dictionary.lookup(offset, size)
    }
}