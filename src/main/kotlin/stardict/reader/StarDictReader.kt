package com.tristanphan.stardict.reader

import com.tristanphan.stardict.TypeIdentifier
import java.io.File

class StarDictReader(ifoFile: File, idxFile: File, dictFile: File): Iterable<String> {
    private val info: InfoReader = InfoReader(ifoFile)
    private val index: IndexReader = IndexReader(idxFile, info)
    private val dictionary: DictReader = DictReader(dictFile, info)

    fun lookup(word: String): Map<TypeIdentifier, String>? {
        val (offset, size) = index.words[word] ?: return null
        return dictionary.lookup(offset, size)
    }

    override fun iterator(): Iterator<String> = iterator {
        yieldAll(elements = index)
    }
}