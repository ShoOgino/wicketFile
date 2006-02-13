/*
 * $Id$ $Revision:
 * 1.2 $ $Date$
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
package wicket.authentication.example;

import wicket.PageParameters;
import wicket.authentication.AuthenticatedWebSession;
import wicket.authentication.SignInPanel;

/**
 * Simple example of a sign in page. It extends SignInPage, a base class which
 * provide standard functionality for typical log-in pages
 * 
 * @author Jonathan Locke
 */
public final class SignInPage extends wicket.authentication.SignInPage
{
	/**
	 * Construct
	 */
	public SignInPage()
	{
		this(null);
	}

	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            The page parameters
	 */
	public SignInPage(final PageParameters parameters)
	{
		add(new SignInPanel("signInPanel")
		{
			public boolean signIn(final String username, final String password)
			{
				return AuthenticatedWebSession.get().authenticate(username, password);
			}
		});
	}
}
