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

import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;

/**
 * Iterates through the {@link ChainedOptionsBuilder}'s and runs each set of options in a new
 * {@linkplain org.openjdk.jmh.runner.Runner runner}.
 *
 * <p>Note that the {@linkplain ChainedOptionsBuilder#parent(Options) parent} will always be set to the
 * {@link org.openjdk.jmh.runner.options.CommandLineOptions ComandLineOptions} from the entry point. This means that
 * options on the command line will <em>not</em> override options in the defined options.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface AggregateOptions extends Iterable<ChainedOptionsBuilder> {
}
