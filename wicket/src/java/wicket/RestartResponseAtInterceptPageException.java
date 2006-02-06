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
package wicket;

/**
 * Causes Wicket to interrupt current request processing and immediately
 * redirect to an intercept page.
 * <p>
 * Similar to calling session.redirectToInteceptPage(Page) with the difference
 * that this exception will interrupt processing of the current request.
 * 
 * @see wicket.Session#redirectToInterceptPage(Page)
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class RestartResponseAtInterceptPageException extends AbstractRestartResponseException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Redirects to the specified intercept page
	 * 
	 * @param interceptPage
	 *            redirect page
	 */
	public RestartResponseAtInterceptPageException(Page interceptPage)
	{
		Session.get().redirectToInterceptPage(interceptPage);
	}

	/**
	 * Redirects to the specified intercept page
	 * 
	 * @param pageClass
	 *            Class of page to instantiate
	 */
	public RestartResponseAtInterceptPageException(Class pageClass)
	{
		Session.get().redirectToInterceptPage(Session.get().getPageFactory().newPage(pageClass));
	}
}
