package com.tristanphan.stardict

enum class TypeIdentifier(val code: String) {
    MEANING("m"),
    MEANING_LOCALE("l"),
    PANGO("g"),
    PHONETIC("t"),
    XDXF("x"),
    YINBIAO_KANA("y"),
    POWERWORD("k"),
    MEDIAWIKI("w"),
    HTML("h"),
    WORDNET("n"),
    RESOURCE_FILE_LIST("r"),
    WAV("W"),
    PICTURE("P"),
    EXPERIMENTAL_EXTENSIONS("X"),
    ;

    override fun toString(): String {
        return this.code
    }

    companion object {
        fun fromString(string: String?): TypeIdentifier {
            if (string == null) throw StarDictParseException("Type identifier not found")
            for (entry in TypeIdentifier.entries) {
                if (entry.code == string) return entry
            }
            throw StarDictParseException("Invalid version")
        }

        fun listFromStringRepresentation(string: String?): List<TypeIdentifier>? {
            if (string == null) return null
            val list = ArrayList<TypeIdentifier>()
            string.forEach { character ->
                val identifier = TypeIdentifier.fromString(character.toString())
                list.add(identifier)
            }
            return list
        }
    }

    fun fieldIsNullTerminated(): Boolean {
        return code.first().isLowerCase()
    }
}

fun List<TypeIdentifier>.toStringRepresentation(): String {
    val builder = StringBuilder()
    this.forEach { typeIdentifier ->
        builder.append(typeIdentifier.toString())
    }
    return builder.toString()
}

