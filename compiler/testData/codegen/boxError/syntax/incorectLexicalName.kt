// ERROR_POLICY: SYNTAX

// FILE: t.kt


384jfjfj2934829...:::%:ББББ

fun 124gga() {}

fun foo() { 124gga() }

// FILE: b.kt

fun box(): String {
    try {
        foo()
    } catch (e: IllegalStateException) {
        return "OK"
    }
    return "FAIL"
}