/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.fir.low.level.api

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.idea.jsonUtils.getString
import java.nio.file.Path

sealed class TestProjectStructure {
    abstract val modules: List<TestProjectModule>
    abstract val fileToResolve: FileToResolve

    companion object {
        fun parse(json: JsonElement): TestProjectStructure {
            require(json is JsonObject)
            return MultiModuleTestProjectStructure(
                json.getAsJsonArray("modules").map { TestProjectModule.parse(it) },
                FileToResolve.parse(json.getAsJsonObject("fileToResolve"))
            )
        }
    }
}

data class FileToResolve(val moduleName: String, val relativeFilePath: String) {
    val filePath get() = "$moduleName/$relativeFilePath"

    companion object {
        fun parse(json: JsonElement): FileToResolve {
            require(json is JsonObject)
            return FileToResolve(
                moduleName = json.getString("module"),
                relativeFilePath = json.getString("file")
            )
        }
    }
}

data class MultiModuleTestProjectStructure(
    override val modules: List<TestProjectModule>,
    override val fileToResolve: FileToResolve
) : TestProjectStructure()

data class TestProjectModule(val name: String, val dependsOnModules: List<String>) {
    companion object {
        fun parse(json: JsonElement): TestProjectModule {
            require(json is JsonObject)
            val dependencies = if (json.has(DEPENDS_ON_FIELD)) {
                json.getAsJsonArray(DEPENDS_ON_FIELD).map { (it as JsonPrimitive).asString }
            } else emptyList()
            return TestProjectModule(
                json.getString("name"),
                dependencies
            )
        }

        private const val DEPENDS_ON_FIELD = "dependsOn"
    }
}

object TestProjectStructureReader {
    fun read(testDirectory: Path, jsonFileName: String = "structure.json"): TestProjectStructure {
        val jsonFile = testDirectory.resolve(jsonFileName)
        val json = JsonParser.parseString(FileUtil.loadFile(jsonFile.toFile(), /*convertLineSeparators=*/true))
        return TestProjectStructure.parse(json)
    }
}