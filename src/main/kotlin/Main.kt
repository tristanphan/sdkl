package com.tristanphan

import com.tristanphan.stardict.TypeIdentifier
import com.tristanphan.stardict.reader.StarDictReader
import com.tristanphan.stardict.writer.StarDictWriter
import java.io.File

fun main() {
    val writer = StarDictWriter(
        File("data/new/testdict.ifo"),
        File("data/new/testdict.idx"),
        File("data/new/testdict.dict"),
        bookname = "TestDict"
    )
    writer.addWord(
        "test1", hashMapOf(
            Pair(TypeIdentifier.MEANING, "meaning of the word!!"),
        )
    )
    writer.addWord(
        "atest2", hashMapOf(
            Pair(TypeIdentifier.MEANING, "another meaning of a different word!!"),
        )
    )
    writer.finish()

    val reader = StarDictReader(
        File("data/new/testdict.ifo"),
        File("data/new/testdict.idx"),
        File("data/new/testdict.dict"),
    )

    println("test1: " + reader.lookup("test1")?.get(TypeIdentifier.MEANING))
    println("atest2: " + reader.lookup("atest2")?.get(TypeIdentifier.MEANING))
}
