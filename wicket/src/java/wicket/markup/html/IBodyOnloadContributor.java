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
 * An interface to be implemented by {@link wicket.markup.html.ajax.IAjaxHandler}s to
 * contribute statements to the onload hander of the &lt;body... tag.
 * 
 * @author Eelco Hillenius
 * @author Juergen Donnerstag
 */
public interface IBodyOnloadContributor
{
	/**
	 * Gets the onload statement(s) for the body component.
	 * @return the onload statement(s) for the body component
	 */
	String getBodyOnload();
}
