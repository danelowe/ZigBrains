package com.falsepattern.zigbrains.project.execution.test

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties

class ZigTestConsoleProperties(config: RunConfiguration, executor: Executor): SMTRunnerConsoleProperties(config, "zig-build-test", executor) {

}