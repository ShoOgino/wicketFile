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
package org.apache.wicket.resource.loader;

import org.apache.wicket.Page;
import org.apache.wicket.WicketTestCase;
import org.apache.wicket.resource.loader.pages.Test1;

/**
 * 
 */
public class PackageStringResourceLoaderTest extends WicketTestCase
{
	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public PackageStringResourceLoaderTest(String name)
	{
		super(name);
	}

	/**
	 * 
	 */
	public void testPackageFileInComponentPackage()
	{
		Page page = new Test1();
		PackageStringResourceLoader loader = new PackageStringResourceLoader();
		assertEquals("test successful", loader.loadStringResource(page, "my_package_test_1"));
	}

	/**
	 * 
	 */
	public void testPackageFileInParentPackage()
	{
		Page page = new Test1();
		PackageStringResourceLoader loader = new PackageStringResourceLoader();
		assertEquals("test 222", loader.loadStringResource(page, "my_package_test_2"));
	}

	/**
	 * 
	 */
	public void testNotFound()
	{
		Page page = new Test1();
		PackageStringResourceLoader loader = new PackageStringResourceLoader();
		assertNull(loader.loadStringResource(page, "abcdefgh"));
	}

	/**
	 * @throws Exception
	 */
	public void test_1() throws Exception
	{
		executeTest(Test1.class, "PackageTestPageExpectedResult_1.html");
	}
}
