package com.tristanphan.stardict.writer

import com.tristanphan.stardict.StarDictVersion
import com.tristanphan.stardict.TypeIdentifier
import com.tristanphan.stardict.toStringRepresentation
import java.io.File

private const val MAGIC_DATA = "StarDict's dict ifo file"

// TODO: Move common parameters from [InfoWriter] and [InfoReader] to a common one
class InfoWriter(
    val file: File,
    val version: StarDictVersion = StarDictVersion.V3_0_0,
    val bookname: String,
    var wordcount: Int = 0,
    val synwordcount: Int? = null,
    var idxfilesize: Int = 0,
    val idxoffsetbits: Int? = null,
    val author: String? = null,
    val email: String? = null,
    val website: String? = null,
    val description: String? = null,
    val date: String? = null,
    val sametypesequence: List<TypeIdentifier>? = null,
) {

    fun write() {
        file.delete()
        file.parentFile?.mkdirs()
        file.createNewFile()
        file.printWriter().use { writer ->
            writer.println(MAGIC_DATA)
            writer.println("version=$version")
            writer.println("bookname=$bookname")
            writer.println("wordcount=$wordcount")
            if (synwordcount != null) writer.println("synwordcount=$synwordcount")
            writer.println("idxfilesize=$idxfilesize")
            if (idxoffsetbits != null) writer.println("idxoffsetbits=$idxoffsetbits")
            if (author != null) writer.println("author=$author")
            if (email != null) writer.println("email=$email")
            if (website != null) writer.println("website=$website")
            if (description != null) writer.println("description=$description")
            if (date != null) writer.println("date=$date")
            if (sametypesequence != null) writer.println("sametypesequence=${sametypesequence.toStringRepresentation()}")
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(MAGIC_DATA + "\n")
        builder.append("version=$version\n")
        builder.append("bookname=$bookname\n")
        builder.append("wordcount=$wordcount\n")
        if (synwordcount != null) builder.append("synwordcount=$synwordcount\n")
        builder.append("idxfilesize=$idxfilesize\n")
        if (idxoffsetbits != null) builder.append("idxoffsetbits=$idxoffsetbits\n")
        if (author != null) builder.append("author=$author\n")
        if (email != null) builder.append("email=$email\n")
        if (website != null) builder.append("website=$website\n")
        if (description != null) builder.append("description=$description\n")
        if (date != null) builder.append("date=$date\n")
        if (sametypesequence != null) builder.append("sametypesequence=$sametypesequence\n")
        return builder.toString()
    }
}
