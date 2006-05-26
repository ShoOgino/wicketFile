/**
 * Copyright (C) 2005, Jonathan W. Locke. All Rights Reserved.
 */

package wicket.extensions.signin;

import wicket.markup.html.link.BookmarkablePageLink;

/**
 * Simple example of a sign in page. It extends SignIn, a base class which
 * provide standard functionality for typical log-in pages
 * 
 * @author Jonathan Locke
 */
@SuppressWarnings("serial")
public final class SignIn extends VoicetribeWebPage
{
    /**
     * Construct
     */
    public SignIn()
    {
        new SignInPanel(this,"signInPanel", getVoicetribeWebSession(), true);
        new BookmarkablePageLink(this,"forgotPassword", ForgotPassword.class);
    }
}
