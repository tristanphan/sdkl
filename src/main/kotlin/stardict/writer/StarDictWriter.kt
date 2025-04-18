package com.tristanphan.stardict.writer

import com.tristanphan.stardict.StarDictFile
import com.tristanphan.stardict.TypeIdentifier

class StarDictWriter(
    file: StarDictFile,
    bookname: String,
    description: String? = null,
    author: String? = null,
    email: String? = null,
    website: String? = null,
    date: String? = null,
    sametypesequence: List<TypeIdentifier>? = null
) {
    private val info: InfoWriter = InfoWriter(
        file.info,
        bookname = bookname,
        description = description,
        author = author,
        email = email,
        website = website,
        date = date,
        sametypesequence = sametypesequence,
    )
    private val index: IndexWriter = IndexWriter(file.index, info)
    private val dictionary: DictWriter = DictWriter(file.dict, info)

    fun addWord(word: String, definitions: Map<TypeIdentifier, String>) {
        val (offset, size) = dictionary.addWord(definitions)
        index.addWord(word, offset, size)
    }

    fun addAlias(originalWord: String, newWord: String) {
        index.addAlias(originalWord, newWord)
    }

    fun finish() {
        val (indexFileSize, wordCount) = index.write()
        info.idxfilesize = indexFileSize
        info.wordcount = wordCount
        info.write()
        dictionary.finish()
        System.err.println("Successfully generated StarDict file for ${info.bookname}!")
    }
}
