plugins {
    kotlin("jvm")
    id("jps-compatible")
}

dependencies {
    compile(project(":core:descriptors"))
    compile(project(":compiler:fir:cones"))
    compile(project(":compiler:fir:tree"))
    compile(project(":compiler:fir:resolve"))
    implementation(project(":compiler:fir:fir-deserialization"))
    implementation(project(":core:deserialization"))

    compileOnly(intellijCoreDep()) { includeJars("intellij-core", rootProject = rootProject) }
}

sourceSets {
    "main" { projectDefault() }
    "test" { none() }
}
