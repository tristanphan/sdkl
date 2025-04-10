package com.tristanphan

import com.tristanphan.stardict.TypeIdentifier
import com.tristanphan.stardict.reader.StarDictReader
import java.io.File

fun main() {
    val reader = StarDictReader(
        File("data/dictd_han-viet.ifo"),
        File("data/dictd_han-viet.idx"),
        File("data/dictd_han-viet.dict")
    )
    val result = reader.lookup("清")
    print("\"${result?.get(TypeIdentifier.HTML)}\"")
}
