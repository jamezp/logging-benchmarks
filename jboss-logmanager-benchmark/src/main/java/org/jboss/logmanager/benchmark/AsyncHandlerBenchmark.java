/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2016 Red Hat, Inc., and individual contributors
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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.AsyncHandler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class AsyncHandlerBenchmark {

    private String loggerClassName;
    private Logger logger;
    private Logger asyncLogger;

    @Setup
    public void setup(final Blackhole bh) throws IOException {
        loggerClassName = getClass().getName();
        logger = Logger.getLogger(Environment.FQCN);
        logger.setLevel(Level.INFO);
        final BlackholeHandler handler = new BlackholeHandler(bh);
        handler.setFormatter(new PatternFormatter("%s%n"));
        handler.setAutoFlush(true);
        logger.addHandler(handler);

        asyncLogger = Logger.getLogger(Environment.FQCN + ".async");
        asyncLogger.setLevel(Level.INFO);
        final AsyncHandler asyncHandler = new AsyncHandler(10000);
        asyncHandler.addHandler(handler);
        asyncHandler.setAutoFlush(true);
        asyncHandler.setOverflowAction(AsyncHandler.OverflowAction.BLOCK);
        asyncLogger.addHandler(asyncHandler);
    }

    @Benchmark
    public void logSync() {
        logger.log(createLogRecord());
    }

    @Benchmark
    public void logAsync() {
        asyncLogger.log(createLogRecord());
    }

    private ExtLogRecord createLogRecord() {
        final String message = "This is a test message";
        return new ExtLogRecord(Level.INFO, message, ExtLogRecord.FormatStyle.NO_FORMAT, loggerClassName);
    }
}
