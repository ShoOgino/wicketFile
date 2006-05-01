/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar
 * 2006) eelco12 $ $Revision: 5004 $ $Date: 2006-03-17 20:47:08 -0800 (Fri, 17
 * Mar 2006) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.protocol.http;

import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * Tests for ClientProperties that failed on Mac OS X Java platform.
 * 
 * @author Martijn Dashorst
 */
public class ClientPropertiesTest extends TestCase
{
	/**
	 * Tests GMT-2:00
	 */
	public void testTimezoneMinus2()
	{
		String utc = "-2.0";
		ClientProperties props = new ClientProperties();
		props.setProperty(ClientProperties.UTC_OFFSET, utc);

		assertEquals(TimeZone.getTimeZone("GMT-2:00"), props.getTimeZone());
	}

	/**
	 * Tests GMT+2:00
	 */
	public void testTimezonePlus2()
	{
		String utc = "+2.0";
		ClientProperties props = new ClientProperties();
		props.setProperty(ClientProperties.UTC_OFFSET, utc);

		assertEquals(TimeZone.getTimeZone("GMT+2:00"), props.getTimeZone());
	}

	/**
	 * Tests GMT+11:00
	 */
	public void testTimezonePlus10()
	{
		String utc = "+11.0";
		ClientProperties props = new ClientProperties();
		props.setProperty(ClientProperties.UTC_OFFSET, utc);

		assertEquals(TimeZone.getTimeZone("GMT+11:00"), props.getTimeZone());
	}

	/**
	 * Tests GMT+2:30
	 */
	public void testTimezonePlus2andAHalf()
	{
		String utc = "+2.5";
		ClientProperties props = new ClientProperties();
		props.setProperty(ClientProperties.UTC_OFFSET, utc);

		assertEquals(TimeZone.getTimeZone("GMT+2:30"), props.getTimeZone());
	}

	/**
	 * Tests GMT-2:30
	 */
	public void testTimezoneMinus2andAHalf()
	{
		String utc = "-2.5";
		ClientProperties props = new ClientProperties();
		props.setProperty(ClientProperties.UTC_OFFSET, utc);

		assertEquals(TimeZone.getTimeZone("GMT-2:30"), props.getTimeZone());
	}

	/**
	 * Tests GMT+3:00
	 */
	public void testTimezonePlus3()
	{
		String utc = "3";
		ClientProperties props = new ClientProperties();
		props.setProperty(ClientProperties.UTC_OFFSET, utc);

		assertEquals(TimeZone.getTimeZone("GMT+3:00"), props.getTimeZone());
	}

	/**
	 * Tests GMT-3:00
	 */
	public void testTimezoneMinus3()
	{
		String utc = "-3";
		ClientProperties props = new ClientProperties();
		props.setProperty(ClientProperties.UTC_OFFSET, utc);

		assertEquals(TimeZone.getTimeZone("GMT-3:00"), props.getTimeZone());
	}
}
