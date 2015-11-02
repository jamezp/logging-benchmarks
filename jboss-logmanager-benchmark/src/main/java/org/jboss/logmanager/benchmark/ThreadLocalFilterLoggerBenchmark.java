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

import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ThreadLocalFilterLoggerBenchmark extends AbstractLoggingBenchmark {

    @Setup
    public void setupFilter() throws IOException {
        LogManager.setThreadLocalLogLevel(record -> {
            final int l = record.getLevel().intValue();
            return l == Level.DEBUG.intValue();
        });
    }

    @TearDown
    public void tearDownFilter() {
        LogManager.setThreadLocalLogLevel(null);
    }
}