// ERROR_POLICY: SEMANTIC

// FILE: t.kt

var storage = ""

fun bar(a: String, b: String) { storage += a; storage += b; }

fun foo() {
    bar("O", "K")
    bar("FAIL1")
    bar("FAIL2", "FAIL2", "FAIL2", "FAIL2")
}

// FILE: b.kt

fun box(): String {
    try {
        foo()
    } catch (e: IllegalStateException) {
        return storage
    }
    return "FAIL"
}