package com.tristanphan.stardict.reader

import com.tristanphan.stardict.StarDictFile
import com.tristanphan.stardict.TypeIdentifier
import com.tristanphan.stardict.writer.StarDictWriter
import com.tristanphan.utilities.ProgressTracker

class StarDictReader(file: StarDictFile) : Iterable<String> {
    internal val info: InfoReader = InfoReader(file.info)
    private val index: IndexReader = IndexReader(file.index, info)
    private val dictionary: DictReader = DictReader(file.dict, info)

    fun lookup(word: String): Map<TypeIdentifier, String>? {
        val (offset, size) = index.words[word] ?: return null
        return dictionary.lookup(offset, size)
    }

    fun copyLinearized(newFile: StarDictFile) {
        val writer = StarDictWriter(
            newFile,
            bookname = info.bookname,
            description = info.description,
            author = info.author,
            website = info.website,
            email = info.email,
            date = info.date,
            sametypesequence = info.sametypesequence
        )
        val progress = ProgressTracker(
            "Copying word progress: ",
            total = size().toLong(),
        )
        for (word in this) {
            val definition = this[word]!!
            writer.addWord(word, definition)
            progress.increment()
        }
        progress.finish()
        writer.finish()
    }

    operator fun get(word: String): Map<TypeIdentifier, String>? = lookup(word)

    override fun iterator(): Iterator<String> = iterator {
        yieldAll(elements = index)
    }

    fun size() = info.wordcount
}