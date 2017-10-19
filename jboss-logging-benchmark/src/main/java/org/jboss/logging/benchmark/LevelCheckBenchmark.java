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

package org.jboss.logging.benchmark;

import org.jboss.logging.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public abstract class LevelCheckBenchmark {

    private static final Logger LOGGER = Logger.getLogger(LevelCheckBenchmark.class);

    @Benchmark
    public void traceLog() {
        LOGGER.tracef("test %s", "test");
    }

    @Benchmark
    public void debugLog() {
        LOGGER.debugf("test %s", "test");
    }

    @Benchmark
    public void infoLog() {
        LOGGER.infof("test %s", "test");
    }

    @Benchmark
    public void warnLog() {
        LOGGER.warnf("test %s", "test");
    }

    @Benchmark
    public void errorLog() {
        LOGGER.errorf("test %s", "test");
    }

    @Benchmark
    public void fatalLog() {
        LOGGER.fatalf("test %s", "test");
    }

    @Benchmark
    public void traceCheck(final Blackhole bh) {
        bh.consume(LOGGER.isEnabled(Logger.Level.TRACE));
    }

    @Benchmark
    public void debugCheck(final Blackhole bh) {
        bh.consume(LOGGER.isEnabled(Logger.Level.DEBUG));
    }

    @Benchmark
    public void infoCheck(final Blackhole bh) {
        bh.consume(LOGGER.isEnabled(Logger.Level.INFO));
    }

    @Benchmark
    public void warnCheck(final Blackhole bh) {
        bh.consume(LOGGER.isEnabled(Logger.Level.WARN));
    }

    @Benchmark
    public void errorCheck(final Blackhole bh) {
        bh.consume(LOGGER.isEnabled(Logger.Level.ERROR));
    }

    @Benchmark
    public void fatalCheck(final Blackhole bh) {
        bh.consume(LOGGER.isEnabled(Logger.Level.FATAL));
    }
}
