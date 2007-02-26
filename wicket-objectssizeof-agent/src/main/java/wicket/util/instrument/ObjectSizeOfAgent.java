/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.util.instrument;

import java.lang.instrument.Instrumentation;

import wicket.util.lang.Objects;
import wicket.util.lang.Objects.IObjectSizeOfStrategy;

/**
 * Instrumentation agent for calculating object sizes using Java's
 * instrumentation API. To use it, have the jar somewhere we you can access it
 * (just having this class on the classpath is not enough) and startup your
 * application with a -javaagent argument like e.g:
 * '-javaagent:/mydir/wicket-objectsizeof-agent-1.3-incubating-SNAPSHOT.jar'.
 * When the application starts up, this agent will register an
 * {@link IObjectSizeOfStrategy} at
 * {@link Objects#setObjectSizeOfStrategy(IObjectSizeOfStrategy)}. Note that
 * this is a static registration.
 * 
 * @author eelcohillenius
 */
public class ObjectSizeOfAgent {

	/**
	 * Initializes agent when it is attached to an already running JVM.
	 * 
	 * @param agentArgs
	 *            Arguments passed in to the agent
	 * @param instrumentation
	 *            The instrumentation class
	 */
	public static void agentmain(String agentArgs,
			Instrumentation instrumentation) {

		InstrumentationObjectSizeOfStrategy strategy = new InstrumentationObjectSizeOfStrategy(
				instrumentation);
		Objects.setObjectSizeOfStrategy(strategy);
	}

	/**
	 * Initializes agent before the main function of the application is
	 * executed.
	 * 
	 * @param agentArgs
	 *            Arguments passed in to the agent
	 * @param instrumentation
	 *            The instrumentation class
	 */
	public static void premain(String agentArgs, Instrumentation instrumentation) {

		InstrumentationObjectSizeOfStrategy strategy = new InstrumentationObjectSizeOfStrategy(
				instrumentation);
		Objects.setObjectSizeOfStrategy(strategy);
	}
}
