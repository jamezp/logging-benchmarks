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

package org.jboss.benchmark.shared;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatFactory;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.Options;

/**
 * A default entry point for running JMH benchmarks.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class MainEntry {

    private static final PrintStream out = System.out;

    public static void main(final String[] args) throws Exception {

        final Map<Options, Collection<RunResult>> results = new LinkedHashMap<>();

        final CommandLineOptions cmdOptions = new CommandLineOptions(args);

        if (cmdOptions.shouldHelp()) {
            cmdOptions.showHelp();
            return;
        }

        if (cmdOptions.shouldListProfilers()) {
            cmdOptions.listProfilers();
            return;
        }

        if (cmdOptions.shouldListResultFormats()) {
            cmdOptions.listResultFormats();
            return;
        }

        final ServiceLoader<AggregateOptions> loader = ServiceLoader.load(AggregateOptions.class);
        final Iterator<AggregateOptions> iter = loader.iterator();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                results.putAll(run(cmdOptions, iter.next()));
            }
        } else {
            Runner runner = new Runner(cmdOptions);

            if (cmdOptions.shouldList()) {
                runner.list();
                return;
            }

            if (cmdOptions.shouldListWithParams()) {
                runner.listWithParams(cmdOptions);
                return;
            }

            // Execute the runner
            runner.run();
        }

        // Print the results
        if (!results.isEmpty()) {
            out.println();
            out.println("Aggregate Benchmark Results:");
            // Print the results for each run
            results.forEach((options, runResults) -> {
                final StringBuilder vmOptions = new StringBuilder();
                options.getJvmArgsPrepend().orElse(Collections.emptyList()).forEach(s -> vmOptions.append(s).append(' '));
                options.getJvmArgs().orElse(Collections.emptyList()).forEach(s -> vmOptions.append(s).append(' '));
                options.getJvmArgsAppend().orElse(Collections.emptyList()).forEach(s -> vmOptions.append(s).append(' '));
                out.println("=========================================================================");
                out.printf(" VM Invoker: %s%n", options.getJvm().orElse(Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString()));
                out.printf(" VM Options: %s%n", vmOptions);
                out.println("=========================================================================");
                ResultFormatFactory.getInstance(ResultFormatType.TEXT, out).writeOut(runResults);
                out.println();
            });
        }
    }

    private static Map<Options, Collection<RunResult>> run(final CommandLineOptions cmdOptions, final AggregateOptions aggregateOptions) throws RunnerException, IOException {
        final Map<Options, Collection<RunResult>> results = new LinkedHashMap<>();

        for (ChainedOptionsBuilder optionsBuilder : aggregateOptions) {
            Options options = optionsBuilder.parent(cmdOptions).build();

            Runner runner = new Runner(options);
            boolean run = true;

            if (cmdOptions.shouldList()) {
                runner.list();
                run = false;
            }

            if (cmdOptions.shouldListWithParams()) {
                runner.listWithParams(cmdOptions);
                run = false;
            }

            // Execute the runner
            if (run) {
                final Collection<RunResult> runResults = runner.run();
                results.put(options, runResults);
            }
        }
        return results;
    }
}
