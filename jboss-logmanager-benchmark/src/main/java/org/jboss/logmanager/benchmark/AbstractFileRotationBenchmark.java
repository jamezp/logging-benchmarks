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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.FileHandler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * Note that this actually writes logs to a file which will add the cost of writing to disk into the benchmark results.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
abstract class AbstractFileRotationBenchmark {

    private String loggerClassName;
    private Logger logger;
    private Path tempLogDir;

    @Setup
    public void setup() throws IOException {
        loggerClassName = getClass().getName();
        logger = Logger.getLogger(Environment.FQCN);
        logger.setLevel(Level.INFO);
        tempLogDir = Files.createTempDirectory("benchmark-logs");
        final Path tempLogFile = tempLogDir.resolve("benchmark-rotation.log");
        Files.deleteIfExists(tempLogFile);
        final FileHandler handler = createFileHandler(tempLogFile);
        handler.setFormatter(new PatternFormatter("%s%n"));
        handler.setAutoFlush(true);
        logger.addHandler(handler);
    }

    @TearDown
    public void tearDown() throws IOException {
        logger.setHandlers(new Handler[0]);
        Files.walkFileTree(tempLogDir, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Benchmark
    public void logWithRotation() {
        final String message = "This is a test message";
        logger.log(new ExtLogRecord(Level.INFO, message, ExtLogRecord.FormatStyle.NO_FORMAT, loggerClassName));
    }

    protected abstract FileHandler createFileHandler(Path tempLogFile) throws FileNotFoundException;
}
