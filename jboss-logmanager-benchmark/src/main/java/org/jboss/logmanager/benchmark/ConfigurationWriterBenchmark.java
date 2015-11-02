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
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.jboss.annotations.ServiceProvider;
import org.jboss.benchmark.shared.RunnerOptions;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.PropertyConfigurator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@ServiceProvider(RunnerOptions.class)
public class ConfigurationWriterBenchmark implements RunnerOptions {

    protected static final String[] JVM_ARGS = {
            "-Xms768m", "-Xmx768m",
            "-XX:+HeapDumpOnOutOfMemoryError",
            "-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
    };

    private final Collection<ChainedOptionsBuilder> options;

    private BlackholeOutputStream out;
    private PropertyConfigurator propertyConfigurator;

    public ConfigurationWriterBenchmark() {
        this.options = Collections.singletonList(
                new OptionsBuilder()
                        .jvmArgsPrepend(JVM_ARGS)
                        .include(Pattern.quote(ConfigurationWriterBenchmark.class.getName()))
        );
    }

    @Override
    public Iterator<ChainedOptionsBuilder> iterator() {
        return options.iterator();
    }

    @Setup
    public void setup() throws IOException {
        out = new BlackholeOutputStream();
        propertyConfigurator = new PropertyConfigurator(LogContext.create());
        propertyConfigurator.configure(ConfigurationWriterBenchmark.class.getResourceAsStream("/simple-logging.properties"));
    }

    @Benchmark
    public void writeConfiguration() throws IOException {
        propertyConfigurator.writeConfiguration(out);
    }

    static class BlackholeOutputStream extends OutputStream {
        private final Blackhole bh = new Blackhole();

        public BlackholeOutputStream() {
        }

        @Override
        public void write(final int b) throws IOException {
            bh.consume(b);
        }
    }
}
