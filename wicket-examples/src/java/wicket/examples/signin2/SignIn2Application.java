/*
 * $Id$
 * $Revision$
 * $Date$
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
package wicket.examples.signin2;

import wicket.ISessionFactory;
import wicket.Session;
import wicket.examples.WicketExampleApplication;
import wicket.markup.html.form.encryption.NoCrypt;

/**
 * Forms example.
 * @author Jonathan Locke
 */
public final class SignIn2Application extends WicketExampleApplication
{
    /**
     * Constructor.
     */
    public SignIn2Application()
    {
        getPages().setHomePage(Home.class);
    }
    
    /**
     * @see wicket.protocol.http.WebApplication#getSessionFactory()
     */
    public ISessionFactory getSessionFactory()
    {
        return new ISessionFactory()
        {
            public Session newSession()
            {
                return new SignIn2Session(SignIn2Application.this);
            }
        };
    }
}


