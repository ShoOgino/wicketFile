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
package org.apache.wicket.util.resource;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * 
 * @author Kent Tong
 */
public class UrlResourceStreamTest extends TestCase
{
	/**
	 * lastModified() shouldn't change the content length if the file isn't really changed.
	 * 
	 * @throws IOException
	 */
	public void testLastModifiedForResourceInJar() throws IOException
	{
		String anyClassInJarFile = "/java/lang/String.class";
		URL url = getClass().getResource(anyClassInJarFile);
		UrlResourceStream stream = new UrlResourceStream(url);
		long length = stream.length();
		stream.lastModifiedTime();
		assertEquals(stream.length(), length);
		stream.close();
	}

}
