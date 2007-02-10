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
package wicket.util.string;


/**
 * Strips comments and whitespace from javascript
 * 
 * @author Matej Knopp
 */
public class JavascriptStripper
{
	/*
	 * Determines the state of script proessing.
	 */
	/** Inside regular text */
	private final static int REGULAR_TEXT = 1; 
		
	/** String started with single quote (') */
	private final static int STRING_SINGLE_QUOTE = 2;
		
	/** String started with double quotes (") */
	private final static int STRING_DOUBLE_QUOTES = 3; 
		
	/** Inside two or more whitespace characters */
	private final static int WHITE_SPACE = 4; 
		
	/** Inside a line comment (//   ) */
	private final static int LINE_COMMENT = 5; 
		
	/** Inside a multi line comment */
	private final static int MULTILINE_COMMENT = 6;

	/**
	 * Removes javascript comments and whitespaces from specified string.
	 * 
	 * @param original
	 *            Source string
	 * @return String with removed comments and whitespaces
	 */
	public static String stripCommentsAndWhitespace(String original)
	{
		// let's be optimistic
		StringBuffer result = new StringBuffer(original.length() / 2);
		int state = REGULAR_TEXT;

		for (int i = 0; i < original.length(); ++i)
		{
			char c = original.charAt(i);
			char next = (i < original.length() - 1) ? original.charAt(i + 1) : 0;
			char prev = (i > 0) ? original.charAt(i - 1) : 0;

			if (state == WHITE_SPACE)
			{
				if (Character.isWhitespace(next) == false)
				{
					state = REGULAR_TEXT;
				}
				continue;
			}

			if (state == REGULAR_TEXT)
			{
				if (c == '/' && next == '/')
				{
					state = LINE_COMMENT;
					continue;
				}
				else if (c == '/' && next == '*')
				{
					state = MULTILINE_COMMENT;
					++i;
					continue;
				}
				else if (Character.isWhitespace(c) && Character.isWhitespace(next))
				{
					// ignore all whitespace characters after this one
					state = WHITE_SPACE;
					c = '\n';
				}
				else if (c == '\'')
				{
					state = STRING_SINGLE_QUOTE;
				}
				else if (c == '"')
				{
					state = STRING_DOUBLE_QUOTES;
				}
				result.append(c);
				continue;
			}

			if (state == LINE_COMMENT)
			{
				if (c == '\n')
				{
					state = REGULAR_TEXT;
					continue;
				}
			}

			if (state == MULTILINE_COMMENT)
			{
				if (c == '*' && next == '/')
				{
					state = REGULAR_TEXT;
					++i;
					continue;
				}
			}

			if (state == STRING_SINGLE_QUOTE)
			{
				if (c == '\'' && prev != '\\')
				{
					state = REGULAR_TEXT;
				}
				result.append(c);
				continue;
			}

			if (state == STRING_DOUBLE_QUOTES)
			{
				if (c == '"' && prev != '\\')
				{
					state = REGULAR_TEXT;
				}
				result.append(c);
				continue;
			}
		}

		return result.toString();
	}
}
