/*
 * $Id: BookDetails.java 5838 2006-05-24 20:44:49 +0000 (Wed, 24 May 2006)
 * joco01 $ $Revision$ $Date: 2006-05-24 20:44:49 +0000 (Wed, 24 May
 * 2006) $
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
package wicket.examples.linkomatic;

import wicket.examples.WicketExamplePage;
import wicket.markup.html.basic.Label;

/**
 * A book details page. Shows information about a book.
 * 
 * @author Jonathan Locke
 */
public final class BookDetails extends WicketExamplePage
{
	/**
	 * Constructor
	 * 
	 * @param book
	 *            The model
	 */
	public BookDetails(final Book book)
	{
		add(new Label(this, "title", book.getTitle()));
	}
}
