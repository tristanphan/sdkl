package com.tristanphan.stardict.writer

import com.tristanphan.stardict.TypeIdentifier
import java.io.File

class StarDictWriter(
    ifoFile: File,
    idxFile: File,
    dictFile: File,
    bookname: String,
    description: String? = null,
    author: String? = null,
    email: String? = null,
    website: String? = null,
    date: String? = null,
) {
    private val info: InfoWriter = InfoWriter(
        ifoFile,
        bookname = bookname,
        description = description,
        author = author,
        email = email,
        website = website,
        date = date,
        sametypesequence = listOf(TypeIdentifier.MEANING),
    )
    private val index: IndexWriter = IndexWriter(idxFile, info)
    private val dictionary: DictWriter = DictWriter(dictFile, info)

    fun addWord(word: String, definitions: HashMap<TypeIdentifier, String>) {
        ++info.wordcount
        val (offset, size) = dictionary.addWord(definitions)
        index.addWord(word, offset, size)
    }

    fun finish() {
        val indexFileSize = index.write()
        info.idxfilesize = indexFileSize
        info.write()
    }
}
