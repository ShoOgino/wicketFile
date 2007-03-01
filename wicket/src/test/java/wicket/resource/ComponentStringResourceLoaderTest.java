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

import java.util.Locale;

import junit.framework.Assert;
import wicket.Component;
import wicket.MockPageWithOneComponent;
import wicket.markup.html.WebMarkupContainer;
import wicket.resource.loader.ComponentStringResourceLoader;
import wicket.resource.loader.IStringResourceLoader;

/**
 * Test case for the <code>ComponentStringResourceLoader</code> class.
 * 
 * @author Chris Turner
 */
public class ComponentStringResourceLoaderTest extends StringResourceLoaderTestBase
{
	/**
	 * Create the test case.
	 * 
	 * @param message
	 *            The test name
	 */
	public ComponentStringResourceLoaderTest(String message)
	{
		super(message);
	}

	/**
	 * Create and return the loader instance
	 * 
	 * @return The loader instance to test
	 */
	@Override
	protected IStringResourceLoader createLoader()
	{
		return new ComponentStringResourceLoader();
	}

	/**
	 * @see wicket.resource.StringResourceLoaderTestBase#testLoaderUnknownResources()
	 */
	@Override
	public void testLoaderUnknownResources()
	{
		MockPageWithOneComponent page = new MockPageWithOneComponent();
		Component c = new DummyComponent(page, "component", application)
		{
			private static final long serialVersionUID = 1L;
		};
		IStringResourceLoader loader = new ComponentStringResourceLoader();
		Assert.assertNull("Missing resource should return null", loader.loadStringResource(c
				.getClass(), "test.string.bad", Locale.getDefault(), null));
	}

	/**
	 * 
	 */
	public void testNullComponent()
	{
		Assert.assertNull("Null component should skip resource load", loader.loadStringResource(
				null, "test.string", Locale.getDefault(), null));
	}

	/**
	 * 
	 */
	public void testMultiLevelEmbeddedComponentLoadFromComponent()
	{
		MockComponentStringResourceLoaderTestPage p = new MockComponentStringResourceLoaderTestPage();
		WebMarkupContainer panel = new WebMarkupContainer(p, "component");
		DummyComponent c = new DummyComponent(panel, "hello", application);
		IStringResourceLoader loader = new ComponentStringResourceLoader();
		Assert.assertEquals("Valid resourse string should be found", "Component string", loader
				.loadStringResource(c.getClass(), "component.string", Locale.getDefault(), null));
	}

	/**
	 * 
	 */
	public void testLoadDirectFromPage()
	{
		DummyPage p = new DummyPage();
		IStringResourceLoader loader = new ComponentStringResourceLoader();
		Assert
				.assertEquals("Valid resourse string should be found", "Another string", loader
						.loadStringResource(p.getClass(), "another.test.string", Locale
								.getDefault(), null));
	}

	/**
	 * 
	 */
	public void testSearchClassHierarchyFromPage()
	{
		DummySubClassPage p = new DummySubClassPage();
		IStringResourceLoader loader = new ComponentStringResourceLoader();
		Assert.assertEquals("Valid resource string should be found", "SubClass Test String",
				loader.loadStringResource(p.getClass(), "subclass.test.string",
						Locale.getDefault(), null));
		Assert
				.assertEquals("Valid resource string should be found", "Another string", loader
						.loadStringResource(p.getClass(), "another.test.string", Locale
								.getDefault(), null));
	}
}
