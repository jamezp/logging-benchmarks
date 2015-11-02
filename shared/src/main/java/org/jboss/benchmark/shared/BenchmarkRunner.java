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

import java.io.OutputStream;
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
 * A benchmark runner which aggregates a collection of {@linkplain RunResult results} and allows the results to be printed.
 *
 * <p>
 * This can also be used as an entry point to run benchmarks.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class BenchmarkRunner {

    private final Map<Options, Collection<RunResult>> results;

    /**
     * Creates a new benchmark runner.
     */
    public BenchmarkRunner() {
        results = new LinkedHashMap<>();
    }

    /**
     * An entry point that will allow {@linkplain CommandLineOptions command line options} to be passed as well as look
     * for {@link RunnerOptions} implementations using a service loader.
     * <p>
     * Iterates through the {@link ChainedOptionsBuilder}'s and runs each set of options in a new
     * {@linkplain org.openjdk.jmh.runner.Runner runner}.
     * </p>
     *
     * <p>
     * Note that the {@linkplain ChainedOptionsBuilder#parent(Options) parent} will always be set to the
     * {@link org.openjdk.jmh.runner.options.CommandLineOptions ComandLineOptions} from the entry point. This means that
     * options on the command line will <em>not</em> override options in the defined options.
     * </p>
     *
     * @param args the command line options
     *
     * @throws Exception if an error occurs running the benchmarks
     */
    public static void main(final String[] args) throws Exception {
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

        final BenchmarkRunner benchmarkRunner = new BenchmarkRunner();

        final ServiceLoader<RunnerOptions> loader = ServiceLoader.load(RunnerOptions.class);
        final Iterator<RunnerOptions> iter = loader.iterator();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                benchmarkRunner.run(cmdOptions, iter.next());
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
            benchmarkRunner.run(runner, cmdOptions);
        }
        benchmarkRunner.printResults();
    }

    /**
     * Runs a benchmark with the options provided.
     *
     * @param options the benchmark options
     *
     * @throws RunnerException if an error occurs running the benchmark
     */
    public void run(final Options options) throws RunnerException {
        run(new Runner(options), options);
    }

    /**
     * Iterates through each {@link ChainedOptionsBuilder}, {@linkplain ChainedOptionsBuilder#build() builds} each option
     * and creates a new {@linkplain Runner runner} for each set of {@linkplain Options options} built.
     *
     * @param runnerOptions the benchmark runner options
     *
     * @throws RunnerException if an error occurs while running the benchmark
     */
    public void run(final RunnerOptions runnerOptions) throws RunnerException {
        for (ChainedOptionsBuilder builder : runnerOptions) {
            run(builder.build());
        }
    }

    /**
     * Print the results from each run to {@link System#out}.
     * <p>
     * Same as running {@code printResults(System.out)}.
     * </p>
     *
     * @see #printResults(OutputStream)
     */
    public void printResults() {
        printResults(System.out);
    }

    /**
     * Prints the results of each run.
     * <p>
     * Same as running {@code printResults(out, ResultFormatType.TEXT)}.
     * </p>
     *
     * @param out the output stream to write the results to
     *
     * @see #printResults(OutputStream, ResultFormatType)
     */
    public void printResults(final OutputStream out) {
        printResults(out, ResultFormatType.TEXT);
    }

    /**
     * Prints the results of each run.
     *
     * @param out        the output stream to write the results to
     * @param formatType the format type used to format the results
     */
    public void printResults(final OutputStream out, final ResultFormatType formatType) {
        final PrintStream printStream;
        if (out instanceof PrintStream) {
            printStream = (PrintStream) out;
        } else {
            printStream = new PrintStream(out);
        }
        // Print the results
        if (!results.isEmpty()) {
            printStream.println();
            printStream.println("Aggregate Benchmark Results:");
            // Print the results for each run
            results.forEach((options, runResults) -> {
                final StringBuilder vmOptions = new StringBuilder();
                options.getJvmArgsPrepend().orElse(Collections.emptyList()).forEach(s -> vmOptions.append(s).append(' '));
                options.getJvmArgs().orElse(Collections.emptyList()).forEach(s -> vmOptions.append(s).append(' '));
                options.getJvmArgsAppend().orElse(Collections.emptyList()).forEach(s -> vmOptions.append(s).append(' '));
                printStream.println("=========================================================================");
                printStream.printf(" VM Invoker: %s%n", options.getJvm().orElse(Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString()));
                printStream.printf(" VM Options: %s%n", vmOptions);
                printStream.println("=========================================================================");
                ResultFormatFactory.getInstance(formatType, printStream).writeOut(runResults);
                printStream.println();
            });
        }
    }

    private void run(final Runner runner, final Options options) throws RunnerException {
        results.put(options, runner.run());
    }

    private void run(final CommandLineOptions cmdOptions, final RunnerOptions runnerOptions) throws RunnerException {
        for (ChainedOptionsBuilder optionsBuilder : runnerOptions) {
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
                run(runner, options);
            }
        }
    }
}
