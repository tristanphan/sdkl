package com.tristanphan.stardict.reader

import com.tristanphan.stardict.StarDictParseException
import com.tristanphan.stardict.StarDictVersion
import com.tristanphan.stardict.TypeIdentifier
import java.io.File

private const val MAGIC_DATA = "StarDict's dict ifo file"

class InfoReader(file: File) {
    val version: StarDictVersion
    val bookname: String
    val wordcount: Int
    val synwordcount: Int?
    val idxfilesize: Int
    val idxoffsetbits: Int?
    val author: String?
    val email: String?
    val website: String?
    val description: String?
    val date: String?
    val sametypesequence: List<TypeIdentifier>?

    init {
        val options = HashMap<String, String>()
        file.useLines { lines: Sequence<String> ->
            val iterator = lines.iterator()

            // Check magic
            expect(iterator.next() == MAGIC_DATA, "Invalid magic data, expected \"$MAGIC_DATA\"")

            // Gather options
            while (iterator.hasNext()) {
                val (key, value) = parseLine(iterator.next())
                options[key] = value
            }
        }

        // TODO: Add type validation & check that no *more* options exist
        version = StarDictVersion.fromString(options["version"])
        bookname = options["bookname"] ?: throw StarDictParseException("Failed to find bookname")
        wordcount = options["wordcount"]?.toInt() ?: throw StarDictParseException("Failed to find wordcount")
        synwordcount = options["synwordcount"]?.toInt()
        idxfilesize = options["idxfilesize"]?.toInt() ?: throw StarDictParseException("Failed to find idxfilesize")
        idxoffsetbits = options["idxoffsetbits"]?.toInt()
        author = options["author"]
        email = options["email"]
        website = options["website"]
        description = options["description"]
        date = options["date"]
        sametypesequence = TypeIdentifier.listFromStringRepresentation(options["sametypesequence"])
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

fun parseLine(line: String): Pair<String, String> {
    val segments = line.split("=", limit = 2)
    expect(segments.size == 2, "Expected %s=%s format")
    return Pair(segments[0], segments[1])
}

fun expect(condition: Boolean, errorMessage: String) {
    if (!condition) throw StarDictParseException(errorMessage)
}
