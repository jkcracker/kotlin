// ERROR_POLICY: SEMANTIC

// FILE: t.kt

fun <reified T> bar(t: T) = t

fun foo(): String {
    return bar<String>("OK")
}

// FILE: b.kt

fun box(): String {
    return foo()
}