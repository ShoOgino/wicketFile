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
package wicket.resource;


/**
 * IPropertyiesFactory is not a 100% replacement for java.util.Properties as it
 * does not provide the same interface. But it serves kind of the same purpose
 * with Wicket specific features. E.g. besides Locale it take 'styles' and
 * 'variations' into account as well, it allows to register listeners which get
 * called when a property resource has changed and it allows to clear the
 * locally cached properties.
 * 
 * @see wicket.resource.Properties
 * 
 * @author Juergen Donnerstag
 */
public interface IPropertiesFactory
{
	/**
	 * Add a listener which will be called when a change to the underlying
	 * resource stream (e.g. properties file) has been detected
	 * 
	 * @param listener
	 */
	void addListener(final IPropertiesChangeListener listener);

	/**
	 * Load the properties associated with the path
	 * 
	 * @param clazz
	 *            The class requesting the properties
	 * @param path
	 *            The path to identify the resource
	 * @return The properties
	 */
	Properties load(final Class clazz, final String path);

	/**
	 * Remove all cached properties
	 */
	abstract void clearCache();
}