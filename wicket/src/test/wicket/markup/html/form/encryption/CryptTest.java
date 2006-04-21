/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ======================================================================== 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may 
 * not use this file except in compliance with the License. You may obtain 
 * a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html.form.encryption;

import wicket.WicketTestCase;
import wicket.util.crypt.ICrypt;
import wicket.util.crypt.NoCrypt;
import wicket.util.crypt.SunJceCrypt;

/**
 * @author Juergen Donnerstag
 */
public class CryptTest extends WicketTestCase
{
    /**
     * Construct.
     * 
     * @param name
     */
    public CryptTest(String name)
    {
	super(name);
    }

	/**
	 * 
	 */
	public void testNoCrypt()
	{
		// The NoCrypt implementation does not modify the string at all
		final ICrypt crypt = new NoCrypt();

		assertEquals("test", crypt.encrypt("test"));
		assertEquals("test", crypt.decrypt("test"));
		assertEquals("test", crypt.encryptUrlSafe("test"));
		assertEquals("test", crypt.decryptUrlSafe("test"));
	}

	/**
	 * 
	 * 
	 */
	public void testCrypt()
	{
		final ICrypt crypt = new SunJceCrypt();

		try
		{
			if (crypt.encrypt("test") != null)
			{
				final String text = "abcdefghijkABC: A test which creates a '/' and/or a '+'";
				final String expectedDefaultEncrypted = "g+N/AGk2b3qe70kJ0we4Rsa8getbnPLm6NyE0BCd+go0P+0kuIe6UvAYP7dlzx+9mfmPaMQ5lCk=";
				final String expectedUrlSafeEncrypted = "g*N-AGk2b3qe70kJ0we4Rsa8getbnPLm6NyE0BCd*go0P*0kuIe6UvAYP7dlzx*9mfmPaMQ5lCk";
				
				assertEquals(expectedDefaultEncrypted, crypt.encrypt(text));
				assertEquals(text, crypt.decrypt(expectedDefaultEncrypted));
				assertEquals(expectedUrlSafeEncrypted, crypt.encryptUrlSafe(text));
				assertEquals(text, crypt.decrypt(expectedUrlSafeEncrypted));
			}
		}
		catch (Exception ex)
		{
		    // fails on JVMs without security provider (e.g. seems to be on
			// MAC in US)
		}
	}
}
