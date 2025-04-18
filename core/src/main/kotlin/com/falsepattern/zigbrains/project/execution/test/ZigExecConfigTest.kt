/*
 * This file is part of ZigBrains.
 *
 * Copyright (C) 2023-2025 FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * ZigBrains is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * ZigBrains is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ZigBrains. If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.zigbrains.project.execution.test

import com.falsepattern.zigbrains.ZigBrainsBundle
import com.falsepattern.zigbrains.project.execution.base.*
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.sm.runner.SMRunnerConsolePropertiesProvider
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.openapi.project.Project
import kotlin.io.path.pathString

class ZigExecConfigTest(project: Project, factory: ConfigurationFactory):     SMRunnerConsolePropertiesProvider,
    ZigExecConfig<ZigExecConfigTest>(project, factory, ZigBrainsBundle.message("exec.type.test.label")) {
    var filePath = FilePathConfigurable("filePath", ZigBrainsBundle.message("exec.option.label.file-path"))
        private set
    var optimization = OptimizationConfigurable("optimization")
        private set
    var compilerArgs = ArgsConfigurable("compilerArgs", ZigBrainsBundle.message("exec.option.label.compiler-args"))
        private set

    @Throws(ExecutionException::class)
    override suspend fun buildCommandLineArgs(debug: Boolean): List<String> {
        val result = ArrayList<String>()
        result.add("build")
        result.add("test")
        if (!debug || optimization.forced) {
            result.add("-Doptimize="+optimization.level.name)
        }
        result.addAll(compilerArgs.argsSplit())
        if (debug) {
            result.add("--test-no-exec")
        }
        result.add("--")
        result.add("--test-runner-test-file")
        result.add(filePath.path?.pathString ?: throw ExecutionException(ZigBrainsBundle.message("exception.zig.empty-file-path")))
        return result
    }

    override val suggestedName: String
        get() = ZigBrainsBundle.message("configuration.test.suggested-name")

    override fun clone(): ZigExecConfigTest {
        val clone = super.clone()
        clone.filePath = filePath.clone()
        clone.compilerArgs = compilerArgs.clone()
        clone.optimization = optimization.clone()
        return clone
    }

    override fun getConfigurables(): List<ZigConfigurable<*>> {
        return super.getConfigurables() + listOf(filePath, optimization, compilerArgs)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): ZigProfileState<ZigExecConfigTest> {
        return ZigProfileStateTest(environment, this)
    }

    override fun createTestConsoleProperties(executor: Executor): SMTRunnerConsoleProperties {
        val properties = ZigTestConsoleProperties(this, executor)
        properties.isIdBasedTestTree = true
        return properties
    }
}