/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.markup.html;


/**
 * An interface to be implemented by components which are able to render
 * header sections. Usually this is only Page. However, Border implements it
 * as well to handle bordered pages (common page layout).
 * 
 * @author Juergen Donnerstag
 */
public interface IHeaderContributor
{
	/**
	 * Print to the web response what ever the component wants
	 * to contribute to the head section.
	 * <p>
	 * Note: This method is kind of dangerous as users are able
	 * to write to the output whatever they like. 
	 * 
	 * @param container The HtmlHeaderContainer
	 */
	public void printHead(final HtmlHeaderContainer container);
}
