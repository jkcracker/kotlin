// ERROR_POLICY: SEMANTIC

// FILE: t.kt

fun bar(a: String, b: String)

fun foo() {
    bar("O", "K")
}

// FILE: b.kt

fun box(): String {
    foo()
    return "OK"
}