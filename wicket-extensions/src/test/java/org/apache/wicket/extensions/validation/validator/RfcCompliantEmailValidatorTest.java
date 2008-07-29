/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.extensions.validation.validator;

import junit.framework.TestCase;

import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.Validatable;

/**
 * Test that it really validates RFC valid email addresses.
 * 
 * @author Frank Bille
 */
public class RfcCompliantEmailValidatorTest extends TestCase
{
	/**
	 * Test a couple of valid email addresses.
	 */
	public void testValidEmailAddresses()
	{
		IValidator validator = RfcCompliantEmailAddressValidator.getInstance();

		String[] validEmails = new String[] { "bill.gates@gmail.com",
				"firstname.middlename@lastname.dk", "buy@something.nu", "user@post.inet.tele.dk",
				"read@my.info", "my @email.com", "my@ email.com", "\"John Doe\"@email.com",
				"no@domain", "german@m�dchen.de", "another.german@�m��l.com" };
		for (int i = 0; i < validEmails.length; i++)
		{
			String emailAddress = validEmails[i];
			Validatable validatable = new Validatable(emailAddress);
			validator.validate(validatable);
			assertTrue(emailAddress + " wasn't valid but should be", validatable.isValid());
		}
	}


	/**
	 * Test a couple of invalid email addresses.
	 */
	public void testInValidEmailAddresses()
	{
		IValidator validator = RfcCompliantEmailAddressValidator.getInstance();

		String[] inValidEmails = new String[] { "whatever", "dont.end.in.a.dot.@gmail.com",
				".dot.in.the.beginning.is.not.good@wicketframework.org", " space@front.com",
				"space@back.com ", "\ttab@front.com", "tab@back.com\t" };

		for (int i = 0; i < inValidEmails.length; i++)
		{
			String emailAddress = inValidEmails[i];
			Validatable validatable = new Validatable(emailAddress);
			validator.validate(validatable);
			assertFalse(emailAddress + " was valid but shouldn't be", validatable.isValid());
		}
	}
}
