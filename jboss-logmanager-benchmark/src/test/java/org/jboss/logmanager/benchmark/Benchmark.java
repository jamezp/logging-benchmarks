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

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.jboss.benchmark.shared.BenchmarkRunner;
import org.jboss.logmanager.LogManager;
import org.junit.AfterClass;
import org.junit.Test;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Allows benchmarks to be run in tests or run a single benchmark at a time.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class Benchmark {

    private static final BenchmarkRunner runner = new BenchmarkRunner();

    @AfterClass
    public static void printResults() {
        runner.printResults();
    }

    @Test
    public void loggerBenchmark() throws Exception {
        final String include = createInclude(LoggerBenchmark.class, LoggerThreadLocalFilterBenchmark.class);
        ChainedOptionsBuilder builder = new OptionsBuilder()
                .jvmArgsPrepend(LogManagerBenchmarkRunner.JVM_ARGS)
                .include(include);

        runner.run(builder.build());

        builder = new OptionsBuilder()
                .jvmArgsAppend("-D" + LogManager.PER_THREAD_LOG_FILTER_KEY + "=true")
                .jvmArgsPrepend(LogManagerBenchmarkRunner.JVM_ARGS)
                .include(include);

        runner.run(builder.build());
    }

    @Test
    public void configurationWriterBenchmark() throws Exception {
        final ChainedOptionsBuilder builder = new OptionsBuilder()
                .jvmArgsPrepend(LogManagerBenchmarkRunner.JVM_ARGS)
                .include(createInclude(ConfigurationWriterBenchmark.class));

        runner.run(builder.build());
    }

    @Test
    public void periodicRotatingFileHandlerBenchmark() throws Exception {
        final ChainedOptionsBuilder builder = new OptionsBuilder()
                .jvmArgsPrepend(LogManagerBenchmarkRunner.JVM_ARGS)
                .include(createInclude(PeriodicRotatingFileHandlerBenchmark.class));

        runner.run(builder.build());
    }

    @Test
    public void periodicSizeRotatingFileHandlerBenchmark() throws Exception {
        final ChainedOptionsBuilder builder = new OptionsBuilder()
                .jvmArgsPrepend(LogManagerBenchmarkRunner.JVM_ARGS)
                .include(createInclude(PeriodicSizeRotatingFileHandlerBenchmark.class));

        runner.run(builder.build());
    }

    @Test
    public void sizeRotatingFileHandlerBenchmark() throws Exception {
        final ChainedOptionsBuilder builder = new OptionsBuilder()
                .jvmArgsPrepend(LogManagerBenchmarkRunner.JVM_ARGS)
                .include(createInclude(SizeRotatingFileHandlerBenchmark.class));

        runner.run(builder.build());
    }

    private static String createInclude(final Class<?> include) {
        return Pattern.quote(include.getName());
    }

    private static String createInclude(final Class<?>... includes) {
        final StringBuilder result = new StringBuilder();
        final Iterator<Class<?>> iterator = Arrays.asList(includes).iterator();
        while (iterator.hasNext()) {
            result.append(Pattern.quote(iterator.next().getName()));
            if (iterator.hasNext()) {
                result.append('|');
            }
        }
        return result.toString();
    }
}
