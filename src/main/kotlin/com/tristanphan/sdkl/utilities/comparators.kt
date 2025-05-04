package com.tristanphan.sdkl.utilities

internal val caselessComparator = java.util.Comparator { s1: String, s2: String ->
    val result = s1.compareTo(s2, ignoreCase = true)
    if (result != 0) return@Comparator result
    else return@Comparator s1.compareTo(s2, ignoreCase = false)
}
