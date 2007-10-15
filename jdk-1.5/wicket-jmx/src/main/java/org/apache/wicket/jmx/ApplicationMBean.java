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
package org.apache.wicket.jmx;

import java.io.IOException;

import org.apache.wicket.Application;


/**
 * MBean interface for exposing application related information and functionality.
 * 
 * @author eelcohillenius
 */
public interface ApplicationMBean
{
	/**
	 * Clears the markup cache, so that templates and properties etc will be reloaded the next time
	 * they are requested.
	 * 
	 * @throws IOException
	 */
	void clearMarkupCache() throws IOException;

	/**
	 * Gets the class of the application.
	 * 
	 * @return the class of the application
	 * @throws IOException
	 */
	String getApplicationClass() throws IOException;

	/**
	 * The configuration type, either {@link org.apache.wicket.Application#DEVELOPMENT} or
	 * {@link Application#DEPLOYMENT}.
	 * 
	 * @return The configuration type
	 */
	String getConfigurationType();

	/**
	 * Gets the configured home page for this application.
	 * 
	 * @return the configured home page for this application
	 * @throws IOException
	 */
	String getHomePageClass() throws IOException;

	/**
	 * Gets the number of elements currently in the markup cache.
	 * 
	 * @return the number of elements currently in the markup cache
	 * @throws IOException
	 */
	int getMarkupCacheSize() throws IOException;

	/**
	 * Lists the registered URL mounts.
	 * 
	 * @return the registered URL mounts
	 * @throws IOException
	 */
	String[] getMounts() throws IOException;

	/**
	 * Gets the Wicket version. The Wicket version is in the same format as the version element in
	 * the pom.xml file (project descriptor). The version is generated by maven in the build/release
	 * cycle and put in the wicket.properties file located in the root folder of the Wicket jar.
	 * 
	 * The version usually follows one of the following formats:
	 * <ul>
	 * <li>major.minor[.bug] for stable versions. 1.1, 1.2, 1.2.1 are examples</li>
	 * <li>major.minor-state for development versions. 1.2-beta2, 1.3-SNAPSHOT are examples</li>
	 * </ul>
	 * 
	 * @return the Wicket version
	 * @throws IOException
	 */
	String getWicketVersion() throws IOException;
}
