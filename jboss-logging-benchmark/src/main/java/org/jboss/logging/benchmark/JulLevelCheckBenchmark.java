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

import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Fork(jvmArgs = {
        "-server",
        "-Dorg.jboss.logging.provider=jdk"
})
@BenchmarkMode({Mode.Throughput})
@State(Scope.Benchmark)
public class JulLevelCheckBenchmark extends LevelCheckBenchmark {

    @Setup
    public void configureLogManager() {
        LogManager.getLogManager().getLogger("").setLevel(Level.OFF);
    }
}
