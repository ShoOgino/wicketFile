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
package org.apache.wicket.request.resource;

import java.util.Locale;

import org.apache.wicket.WicketTestCase;
import org.apache.wicket.request.resource.ResourceReference.UrlAttributes;
import org.junit.Test;


/**
 * @author Pedro Santos
 */
public class PackageResourceReferenceTest extends WicketTestCase
{
	/**
	 * 
	 */
	@Test
	public void testResourceResolution()
	{
		Locale[] locales = { null, new Locale("en"), new Locale("en", "US") };
		String[] styles = { null, "style" };
		String[] variations = { null, "var" };
		for (Locale locale : locales)
		{
			for (String style : styles)
			{
				for (String variation : variations)
				{
					ResourceReference reference = new PackageResourceReference(
						PackageResourceReferenceTest.class, "resource.txt", locale, style,
						variation);
					UrlAttributes urlAttributes = reference.getUrlAttributes();
					assertEquals(urlAttributes.getLocale(), locale);
					assertEquals(urlAttributes.getStyle(), style);
					assertEquals(urlAttributes.getVariation(), variation);
				}
			}
		}
	}
}
