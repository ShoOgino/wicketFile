/*
 * $Id: ContainerWithAssociatedMarkupHelper.java,v 1.1 2006/03/10 22:20:42
 * jdonnerstag Exp $ $Revision: 5067 $ $Date: 2006/03/10 22:20:42 $
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
package wicket.markup.html.header.threelayers;

import wicket.WicketTestCase;

/**
 * Tests the inclusion of the wicket:head section from a panel in a subclassed
 * page.
 * 
 * @author Martijn Dashorst
 */
public class InheritanceHeadTest extends WicketTestCase
{
	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public InheritanceHeadTest(String name)
	{
		super(name);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void test_1() throws Exception
	{
		executeTest(MeldingPage.class, "ExpectedResult.html");
	}
}
