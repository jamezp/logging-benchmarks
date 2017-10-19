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
import java.util.concurrent.TimeUnit;

import org.jboss.logmanager.ExtFormatter;
import org.jboss.logmanager.formatters.XmlFormatter;
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
@State(Scope.Benchmark)
public class XmlFormatterBenchmark extends AbstractFormatterBenchmark {
    private final XmlFormatter prettyPrint = new XmlFormatter();

    @Setup
    public void setup(final Blackhole bh) throws IOException {
        super.setup(bh);
        prettyPrint.setPrettyPrint(true);
    }

    @Benchmark
    public void prettyPrint() {
        bh.consume(prettyPrint.format(record));
    }

    @Override
    ExtFormatter createFormatter() {
        return new XmlFormatter();
    }
}
