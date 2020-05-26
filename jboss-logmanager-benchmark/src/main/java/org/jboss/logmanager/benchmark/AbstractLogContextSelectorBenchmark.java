/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2019 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.logmanager.benchmark;

import java.util.concurrent.TimeUnit;

import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.LogContextSelector;
import org.jboss.logmanager.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public abstract class AbstractLogContextSelectorBenchmark {
    static final String LOGGER_NAME = Environment.FQCN;

    private final LogContextSelector defaultSelector = LogContext.getLogContextSelector();
    private LogContextSelector selector;

    @Setup
    public void setup() {
        selector = createLogContextSelector();
        LogContext.setLogContextSelector(selector);
    }

    @TearDown
    public void tearDown() {
        LogContext.setLogContextSelector(defaultSelector);
    }

    @Benchmark
    public LogContext selectLogContext() {
        return selector.getLogContext();
    }

    @Benchmark
    public Logger getLogger() {
        return Logger.getLogger(LOGGER_NAME);
    }

    abstract LogContextSelector createLogContextSelector();
}
