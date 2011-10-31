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
package org.apache.wicket.resource;

import java.util.Locale;

import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.junit.Test;

/**
 * Tests for the <code>ApplicationStringResourceLoader</code> class.
 * 
 * @author Chris Turner
 */
public class ApplicationStringResourceLoaderTest extends StringResourceLoaderTestBase
{
	/**
	 * Return the loader instance
	 * 
	 * @return The loader instance to test
	 */
	@Override
	protected IStringResourceLoader createLoader()
	{
		return new ClassStringResourceLoader(tester.getApplication().getClass());
	}

	/**
	 * @see org.apache.wicket.resource.StringResourceLoaderTestBase#testLoaderUnknownResources()
	 */
	@Override
	@Test
	public void loaderUnknownResources()
	{
		assertNull(
			"Unknown resource should return null",
			createLoader().loadStringResource(component.getClass(),
				"test.string.that.does.not.exist", Locale.getDefault(), null, null));
	}
}
