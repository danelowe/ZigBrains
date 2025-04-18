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

import com.falsepattern.zigbrains.project.execution.base.ZigProfileState
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.util.Disposer
import com.intellij.ui.content.Content
import com.intellij.util.ui.UIUtil

class ZigProfileStateTest(environment: ExecutionEnvironment, configuration: ZigExecConfigTest) : ZigProfileState<ZigExecConfigTest>(environment, configuration) {

    @Throws(ExecutionException::class)
    override fun createConsole(executor: Executor): ConsoleView? {
        val testConsoleProperties = configuration.createTestConsoleProperties(executor)
        testConsoleProperties.setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false)
        val consoleView = UIUtil.invokeAndWaitIfNeeded<SMTRunnerConsoleView> {
            SMTestRunnerConnectionUtil.createConsole(testConsoleProperties)
        }
        consoleView.resultsViewer.testsRootNode.executionId = environment.executionId
        // TODO:
        //   For resources required while a tool window tab is displayed,
        //   pass your instance implementing Disposable to Content.setDisposer().
        //Disposer.register(configuration.project, consoleView)


        /*
        processTCMessage , called from PythonConsoleView.print
        So we either have a custom ConsoleView that readsstdout/stderr,
        or use SMTRunnerConsoleView, and send updates via a different channel.
        SMTRunnerConsoleView is more likely to work with async execution.
        stderr can be assigned per process, not per thread.
        zig likes to send a lot to stderr.
         */

        return consoleView
    }
}