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
import java.util.Collection;
import java.util.Iterator;

import org.jboss.annotations.ServiceProvider;
import org.jboss.benchmark.shared.AggregateOptions;
import org.jboss.logmanager.LogManager;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ServiceProvider(AggregateOptions.class)
public class LoggerBenchmark implements AggregateOptions {

    protected static final String[] JVM_ARGS = {
            "-Xms768m", "-Xmx768m",
            "-XX:+HeapDumpOnOutOfMemoryError",
            "-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
    };

    private final Collection<ChainedOptionsBuilder> options;

    public LoggerBenchmark() {
        this.options = Arrays.asList(
                new OptionsBuilder()
                        .jvmArgsPrepend(JVM_ARGS),
                new OptionsBuilder()
                        .jvmArgsAppend("-D" + LogManager.PER_THREAD_LOG_FILTER_KEY + "=true")
                        .jvmArgsPrepend(JVM_ARGS)
        );
    }

    @Override
    public Iterator<ChainedOptionsBuilder> iterator() {
        return options.iterator();
    }
}
