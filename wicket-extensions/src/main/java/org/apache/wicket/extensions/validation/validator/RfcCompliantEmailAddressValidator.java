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

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;


/**
 * Validator for validating email addresses according to the RFC 822. This validator uses the
 * regular expression taken from the Perl implementation of RFC 822.
 * <p>
 * Most users will be satisfied with the {@link EmailAddressValidator}
 * </p>
 * 
 * @see <a href="http://www.ex-parrot.com/~pdw/Mail-RFC822-Address.html">Perl Regex implementation
 *      of RFC 822</a>
 * @see <a href="http://www.ietf.org/rfc/rfc0822.txt?number=822">RFC 822</a>
 * @author Frank Bille
 */
public class RfcCompliantEmailAddressValidator extends StringValidator
{
	private static final long serialVersionUID = 1L;

	private static final String EMAIL_PATTERN = "(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t]"
		+ ")+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:"
		+ "\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:("
		+ "?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ "
		+ "\\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\0"
		+ "31]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\"
		+ "](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+"
		+ "(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:"
		+ "(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z"
		+ "|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)"
		+ "?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\"
		+ "r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?["
		+ " \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)"
		+ "?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t]"
		+ ")*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?["
		+ " \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*"
		+ ")(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t]"
		+ ")+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)"
		+ "*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+"
		+ "|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r"
		+ "\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:"
		+ "\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t"
		+ "]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031"
		+ "]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\]("
		+ "?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?"
		+ ":(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?"
		+ ":\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?"
		+ ":(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?"
		+ "[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\".\\[\\] "
		+ "\\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|"
		+ "\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>"
		+ "@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\""
		+ "(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t]"
		+ ")*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\"
		+ "\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?"
		+ ":[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\["
		+ "\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\".\\[\\] \\000-"
		+ "\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|("
		+ "?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;"
		+ ":\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[(["
		+ "^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\""
		+ ".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\"
		+ "]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\"
		+ "[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\"
		+ "r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] "
		+ "\\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]"
		+ "|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\".\\[\\] \\0"
		+ "00-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\"
		+ ".|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,"
		+ ";:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\"(?"
		+ ":[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*"
		+ "(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\"."
		+ "\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:["
		+ "^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]"
		+ "]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*("
		+ "?:(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\"
		+ "\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:("
		+ "?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=["
		+ "\\[\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t"
		+ "])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t"
		+ "])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?"
		+ ":\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|"
		+ "\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:"
		+ "[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\"
		+ "]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)"
		+ "?[ \\t])*(?:@(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\""
		+ "()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)"
		+ "?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>"
		+ "@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?["
		+ " \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,"
		+ ";:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t]"
		+ ")*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\"
		+ "\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?"
		+ "(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\"."
		+ "\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:"
		+ "\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\["
		+ "\"()<>@,;:\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])"
		+ "*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])"
		+ "+|\\Z|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\"
		+ ".(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z"
		+ "|(?=[\\[\"()<>@,;:\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:("
		+ "?:\\r\\n)?[ \\t])*))*)?;\\s*)";

	private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

	/** Singleton instance */
	private static final RfcCompliantEmailAddressValidator INSTANCE = new RfcCompliantEmailAddressValidator();

	/**
	 * @return Instance of RFC compliant emailaddress validator
	 */
	public static RfcCompliantEmailAddressValidator getInstance()
	{
		return INSTANCE;
	}

	protected RfcCompliantEmailAddressValidator()
	{
	}

	/**
	 * Checks a value against this <code>PatternValidator</code>'s {@link Pattern}.
	 * 
	 * @param validatable
	 *            the <code>IValidatable</code> to check
	 */
	@Override
	protected Map<String, Object> variablesMap(IValidatable<String> validatable)
	{
		final Map<String, Object> map = super.variablesMap(validatable);
		map.put("pattern", EMAIL_PATTERN);
		return map;
	}

	@Override
	protected void onValidate(IValidatable<String> validatable)
	{
		String email = validatable.getValue().toString();
		if (email.length() != email.trim().length())
		{
			error(validatable);
		}
		else if (!PATTERN.matcher(validatable.getValue()).matches())
		{
			error(validatable);
		}
	}
}
