/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2017 Red Hat, Inc., and individual contributors
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

package org.jboss.logmanager.benchmark.formatter;

import java.io.IOException;

import org.jboss.logmanager.ExtFormatter;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.benchmark.Environment;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public abstract class AbstractFormatterBenchmark {
    Blackhole bh;
    @SuppressWarnings("WeakerAccess")
    ExtFormatter formatter;
    ExtLogRecord record;
    @SuppressWarnings("WeakerAccess")
    ExtLogRecord recordWithCause;

    @Setup
    public void setup(final Blackhole bh) throws IOException {
        this.bh = bh;
        formatter = createFormatter();
        record = new ExtLogRecord(Level.INFO, "This is a test message.", Environment.FQCN);
        recordWithCause = new ExtLogRecord(Level.INFO, "This is a test message with a cause", Environment.FQCN);
        recordWithCause.setThrown(new RuntimeException("First cause", new RuntimeException("Second cause")));
    }

    @Benchmark
    public void noCause() {
        bh.consume(formatter.format(record));
    }

    @Benchmark
    public void withCause() {
        bh.consume(formatter.format(recordWithCause));
    }

    abstract ExtFormatter createFormatter();
}
