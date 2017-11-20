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

package org.jboss.logmanager.benchmark.config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.config.FormatterConfiguration;
import org.jboss.logmanager.config.HandlerConfiguration;
import org.jboss.logmanager.config.LogContextConfiguration;
import org.jboss.logmanager.config.LoggerConfiguration;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.ConsoleHandler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class LogContextConfigurationBenchmark {

    @State(Scope.Benchmark)
    public static class Config {
        private LogContextConfiguration configuration;

        @Setup(Level.Invocation)
        public void setup() throws IOException {
            configuration = LogContextConfiguration.Factory.create(LogContext.create());
            final LoggerConfiguration loggerConfiguration = configuration.addLoggerConfiguration("");
            for (int i = 0; i < 1000; i++) {
                final FormatterConfiguration formatterConfiguration = configuration.addFormatterConfiguration(null,
                        PatternFormatter.class.getName(), "PATTERN" + i, "pattern");
                formatterConfiguration.setPropertyValueString("pattern", "%d{MM-dd-yyyy'T'HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n");

                final HandlerConfiguration handlerConfiguration = configuration.addHandlerConfiguration(null,
                        ConsoleHandler.class.getName(), "CONSOLE" + i);
                handlerConfiguration.setFormatterName(formatterConfiguration.getName());
                loggerConfiguration.addHandlerName(handlerConfiguration.getName());
            }

            configuration.commit();

            // Add removal to test the validation
            for (String name : configuration.getLoggerNames()) {
                configuration.removeLoggerConfiguration(name);
            }
            for (String name : configuration.getHandlerNames()) {
                configuration.removeHandlerConfiguration(name);
            }
            for (String name : configuration.getFormatterNames()) {
                configuration.removeFormatterConfiguration(name);
            }
        }
    }

    /**
     * Tests doing a prepare of a {@link LogContextConfiguration}.
     *
     * @param config the configuration used for this benchmark
     */
    @Benchmark
    public void removalPrepare(final Config config) {
        config.configuration.prepare();
    }
}
