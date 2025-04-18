package com.tristanphan

import com.tristanphan.stardict.StarDictFile
import com.tristanphan.stardict.TypeIdentifier
import com.tristanphan.stardict.reader.StarDictReader

val STAR_DICT_MASTER_FILE = StarDictFile("data/output/wiktionary-en-master")

fun main() {
    interactiveSearch(STAR_DICT_MASTER_FILE)
}

private fun interactiveSearch(masterFile: StarDictFile) {
    val reader = StarDictReader(masterFile)
    while (true) {
        print(" > ")
        val line = readlnOrNull() ?: break
        println(reader[line.trim()]?.get(TypeIdentifier.MEDIAWIKI) ?: "Nothing found...")
        println()
    }
}
