/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.examples.encodings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import junit.framework.Test;
import wicket.examples.WicketWebTestCase;

/**
 * jWebUnit test for Hello World.
 */
public class EncodingTest extends WicketWebTestCase
{
	/**
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return suite(EncodingTest.class);
	}

	/**
	 * Construct.
	 * 
	 * @param name
	 *            name of test
	 */
	public EncodingTest(String name)
	{
		super(name);
	}

	/**
	 * Test page.
	 * 
	 * @throws Exception
	 */
	public void test_1() throws Exception
	{
		beginAt("/encodings");
		this.dumpResponse(System.out);
		assertTitleEquals("Wicket Examples - encodings");
		assertTextPresent("Hello world! Test: ���");
	}
}
