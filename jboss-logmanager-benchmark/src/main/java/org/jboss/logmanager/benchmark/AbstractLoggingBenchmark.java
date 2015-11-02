/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2015 Red Hat, Inc., and individual contributors
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
import java.util.logging.Handler;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public abstract class AbstractLoggingBenchmark {

    static final String FQCN = AbstractLoggingBenchmark.class.getPackage().getName();

    private final static Logger LOGGER = Logger.getLogger(FQCN);

    @Setup
    public void setup() throws IOException {
        LOGGER.setLevel(Level.INFO);
        LOGGER.addHandler(new BlackholeHandler());
    }

    @TearDown
    public void tearDown() {
        LOGGER.setHandlers(new Handler[0]);
    }

    @Benchmark
    public void logInfoAndDebug() {
        final String message = "This is a test message";
        LOGGER.log(FQCN, Level.INFO, message, ExtLogRecord.FormatStyle.NO_FORMAT, null, null);
        LOGGER.log(FQCN, Level.DEBUG, message, ExtLogRecord.FormatStyle.NO_FORMAT, null, null);
    }

    static class BlackholeHandler extends ExtHandler {
        private final Blackhole bh = new Blackhole();

        public BlackholeHandler() {
        }

        @Override
        protected void doPublish(final ExtLogRecord record) {
            bh.consume(record);
        }
    }
}
