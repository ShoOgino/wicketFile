/*
 * $Id$ $Revision:
 * 1.11 $ $Date$
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
package wicket.markup.html.panel;

import wicket.Page;
import wicket.PageParameters;
import wicket.RequestCycle;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.PasswordTextField;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.IValidationFeedback;
import wicket.util.value.ValueMap;

/**
 * Reusable user sign in panel with username and password as well as support for
 * cookie persistence of the both. When the SignInPanel's form is submitted, the
 * abstract method signIn(String, String) is called, passing the username and
 * password submitted. The signIn() method should sign the user in and return
 * null if no error ocurred, or a descriptive String in the event that the sign
 * in fails.
 * 
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 * @author Eelco Hillenius
 */
public abstract class SignInPanel extends Panel
{
	/** Field for user name. */
	private TextField username;

	/** Field for password. */
	private PasswordTextField password;

	/**
	 * @see wicket.Component#Component(String)
	 */
	public SignInPanel(String componentName)
	{
		super(componentName);

		// Create feedback panel and add to page
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);

		// Add sign-in form to page, passing feedback panel as
		// validation error handler
		add(new SignInForm("signInForm", feedback));
	}

	/**
	 * Convenience method to access the username.
	 * 
	 * @return The user name
	 */
	public String getUsername()
	{
		return username.getModelObjectAsString();
	}

	/**
	 * Convenience method to access the password.
	 * 
	 * @return The password
	 */
	public String getPassword()
	{
		return password.getModelObjectAsString();
	}

	/**
	 * Convenience method set persistence for username and password.
	 * 
	 * @param enable
	 *            Whether the fields should be persistent
	 */
	public void setPersistent(boolean enable)
	{
		username.setPersistent(enable);
		password.setPersistent(enable);
	}

	/**
	 * Sign in user if possible.
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 * @return Error message to display, or null if the user was signed in
	 */
	public abstract String signIn(final String username, final String password);

	/**
	 * Sign in form.
	 */
	public final class SignInForm extends Form
	{
		/** Serial Version ID. */
		private static final long serialVersionUID = 303695648327317416L;

		/** El-cheapo model for form. */
		private final ValueMap properties = new ValueMap();

		/**
		 * Constructor.
		 * 
		 * @param componentName
		 *            Name of the form component
		 * @param feedback
		 *            The feedback panel to update
		 */
		public SignInForm(final String componentName, final IValidationFeedback feedback)
		{
			super(componentName, feedback);

			// Attach textfield components that edit properties map
			// in lieu of a formal beans model
			add(username = new TextField("username", properties, "username"));
			add(password = new PasswordTextField("password", properties, "password"));
		}

		/**
		 * @see wicket.markup.html.form.Form#handleSubmit()
		 */
		public final void handleSubmit()
		{
			// Sign the user in
			final String error = signIn(getUsername(), getPassword());

			if (error == null)
			{
				// Get active request cycle
				final RequestCycle cycle = getRequestCycle();

				// If login has been called because the user was not yet
				// logged in, than continue to the original destination.
				// Else to the Home page
				if (cycle.continueToOriginalDestination())
				{
					// HTTP redirect response has been committed. No more data
					// shall be written to the response.
					cycle.setPage((Page)null);
				}
				else
				{
					cycle.setPage(getApplicationSettings().getDefaultPageFactory().newPage(
							getApplicationPages().getHomePage(), (PageParameters)null));
				}
			}
			else
			{
				error(error);
			}
		}
	}
}

