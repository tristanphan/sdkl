package com.tristanphan.sdkl.utilities

import java.util.*

internal fun <T> combineNavigableSets(s1: NavigableSet<T>, s2: NavigableSet<T>): NavigableSet<T> {
    val set = TreeSet<T>()

    assert(s1.comparator() == s2.comparator())
    val comparator = s1.comparator()
    val iterator1 = s1.iterator()
    val iterator2 = s2.iterator()

    // Weave the two sets
    var value1: T? = if (iterator1.hasNext()) iterator1.next() else null
    var values2: T? = if (iterator2.hasNext()) iterator2.next() else null
    while (value1 != null && values2 != null) {
        val comparison = comparator.compare(value1, values2)
        if (comparison < 0) {
            set.add(value1)
            value1 = if (iterator1.hasNext()) iterator1.next() else null
        } else {
            set.add(values2)
            values2 = if (iterator2.hasNext()) iterator2.next() else null
        }
    }

    // Add residual values
    while (value1 != null) {
        set.add(value1)
        value1 = if (iterator1.hasNext()) iterator1.next() else null
    }
    while (values2 != null) {
        set.add(values2)
        values2 = if (iterator2.hasNext()) iterator2.next() else null
    }
    return set
}
