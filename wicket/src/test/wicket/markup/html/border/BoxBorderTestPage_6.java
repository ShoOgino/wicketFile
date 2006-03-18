/*
 * $Id: BoxBorderTestPage_3.java 3749 2006-01-14 00:54:30Z ivaynberg $
 * $Revision$
 * $Date: 2006-01-14 01:54:30 +0100 (Sa, 14 Jan 2006) $
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup.html.border;

import wicket.markup.html.WebPage;


/**
 * Mock page for testing.
 * 
 */
public class BoxBorderTestPage_6 extends WebPage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct.
	 * 
	 * 
	 */
	public BoxBorderTestPage_6()
	{
		Border border1 = new BoxBorder("border1");
		add(border1);
		
		Border border2 = new BoxBorder("border2");
		border1.add(border2);
	}
}
