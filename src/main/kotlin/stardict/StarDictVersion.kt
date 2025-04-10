package com.tristanphan.stardict

enum class StarDictVersion(val value: String) {
    V2_4_2("2.4.2"),
    V3_0_0("3.0.0"),
    ;

    override fun toString(): String {
        return this.value
    }

    companion object {
        fun fromString(string: String?): StarDictVersion {
            if (string == null) throw StarDictParseException("Version not found")
            for (entry in StarDictVersion.entries) {
                if (entry.value == string) return entry
            }
            throw StarDictParseException("Invalid version")
        }
    }
}