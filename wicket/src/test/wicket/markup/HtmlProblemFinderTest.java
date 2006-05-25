/*
 * $Id: HtmlProblemFinderTest.java 5844 2006-05-24 20:53:56 +0000 (Wed, 24 May
 * 2006) joco01 $ $Revision$ $Date: 2006-05-24 20:53:56 +0000 (Wed, 24 May
 * 2006) $
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
package wicket.markup;


import wicket.WicketTestCase;
import wicket.markup.parser.XmlPullParser;
import wicket.markup.parser.filter.HtmlProblemFinder;

/**
 * @author Juergen Donnerstag
 */
public class HtmlProblemFinderTest extends WicketTestCase
{


	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public HtmlProblemFinderTest(String name)
	{
		super(name);
	}

	/**
	 * 
	 */
	public void testProblemFinder()
	{
		final MarkupParser parser = new MarkupParser(new XmlPullParser())
		{
			@Override
			public void initFilterChain()
			{
				appendMarkupFilter(new HtmlProblemFinder(HtmlProblemFinder.ERR_THROW_EXCEPTION));
			}
		};

		try
		{
			parser.parse("<img src=\"\"/>");
			assertTrue("Should have thrown an exception", false);
		}
		catch (Exception ex)
		{
			// ignore
		}
	}
}
