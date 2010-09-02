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
package org.apache.wicket.protocol.http;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.request.UrlDecoder;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

/**
 * Wicket Http specific utilities class.
 */
public final class RequestUtils
{
	/**
	 * Decode the provided queryString as a series of key/ value pairs and set them in the provided
	 * value map.
	 * 
	 * @param queryString
	 *            string to decode, uses '&' to separate parameters and '=' to separate key from
	 *            value
	 * @param params
	 *            parameters map to write the found key/ value pairs to
	 */
	public static void decodeParameters(String queryString, PageParameters params)
	{
		for (String paramTuple : Strings.split(queryString, '&'))
		{
			final String[] bits = Strings.split(paramTuple, '=');

			if (bits.length == 2)
			{
				params.addNamedParameter(UrlDecoder.QUERY_INSTANCE.decode(bits[0], getCurrentCharset()),
				                         UrlDecoder.QUERY_INSTANCE.decode(bits[1], getCurrentCharset()));
			}
			else
			{
				params.addNamedParameter(UrlDecoder.QUERY_INSTANCE.decode(bits[0], getCurrentCharset()), "");
			}
		}
	}

// TODO review
// NO LONGER USED SINCE WE HAVE URL OBJECT
// /**
// * decores url parameters form <code>queryString</code> into <code>parameters</code> map
// *
// * @param queryString
// * @param parameters
// */
// public static void decodeUrlParameters(String queryString, Map<String, String[]> parameters)
// {
// Map<String, List<String>> temp = new HashMap<String, List<String>>();
// final String[] paramTuples = queryString.split("&");
// for (int t = 0; t < paramTuples.length; t++)
// {
// final String[] bits = paramTuples[t].split("=");
// final String key;
// final String value;
// key = WicketURLDecoder.QUERY_INSTANCE.decode(bits[0]);
// if (bits.length == 2)
// {
// value = WicketURLDecoder.QUERY_INSTANCE.decode(bits[1]);
// }
// else
// {
// value = "";
// }
// List<String> l = temp.get(key);
// if (l == null)
// {
// l = new ArrayList<String>();
// temp.put(key, l);
// }
// l.add(value);
// }
//
// for (Map.Entry<String, List<String>> entry : temp.entrySet())
// {
// String s[] = new String[entry.getValue().size()];
// entry.getValue().toArray(s);
// parameters.put(entry.getKey(), s);
// }
// }

	/**
	 * Remove occurrences of ".." from the path
	 * 
	 * @param path
	 * @return path string with double dots removed
	 */
	public static String removeDoubleDots(String path)
	{
		List<String> newcomponents = new ArrayList<String>(Arrays.asList(path.split("/")));

		for (int i = 0; i < newcomponents.size(); i++)
		{
			if (i < newcomponents.size() - 1)
			{
				// Verify for a ".." component at next iteration
				if ((newcomponents.get(i)).length() > 0 && newcomponents.get(i + 1).equals(".."))
				{
					newcomponents.remove(i);
					newcomponents.remove(i);
					i = i - 2;
					if (i < -1)
					{
						i = -1;
					}
				}
			}
		}
		String newpath = Strings.join("/", newcomponents.toArray(new String[newcomponents.size()]));
		if (path.endsWith("/"))
		{
			return newpath + "/";
		}
		return newpath;
	}

	/**
	 * Hidden utility class constructor.
	 */
	private RequestUtils()
	{
	}


	/**
	 * Calculates absolute path to url relative to another absolute url.
	 * 
	 * @param requestPath
	 *            absolute path.
	 * @param relativePagePath
	 *            path, relative to requestPath
	 * @return absolute path for given url
	 */
	public static String toAbsolutePath(final String requestPath, String relativePagePath)
	{
		final StringBuffer result;
		if (requestPath.endsWith("/"))
		{
			result = new StringBuffer(requestPath);
		}
		else
		{
			// Remove everything after last slash (but not slash itself)
			result = new StringBuffer(requestPath.substring(0, requestPath.lastIndexOf('/') + 1));
		}

		if (relativePagePath.startsWith("./"))
		{
			relativePagePath = relativePagePath.substring(2);
		}

		if (relativePagePath.startsWith("../"))
		{
			StringBuffer tempRelative = new StringBuffer(relativePagePath);

			// Go up through hierarchy until we find most common directory for both pathes.
			while (tempRelative.indexOf("../") == 0)
			{
				// Delete ../ from relative path
				tempRelative.delete(0, 3);

				// Delete last slash from result
				result.setLength(result.length() - 1);

				// Delete everyting up to last slash
				result.delete(result.lastIndexOf("/") + 1, result.length());
			}
			result.append(tempRelative);
		}
		else
		{
			// Pages are in the same directory
			result.append(relativePagePath);
		}
		return result.toString();
	}

	private static Charset getDefaultCharset()
	{
		String charsetName = null;

		Application application = Application.get();
		if (application != null)
		{
			charsetName = application.getRequestCycleSettings().getResponseRequestEncoding();
		}
		if (Strings.isEmpty(charsetName))
		{
			charsetName = "UTF-8";
		}
		return Charset.forName(charsetName);
	}

	private static Charset getCurrentCharset()
	{
		return RequestCycle.get().getRequest().getCharset();
	}

	public static Charset getCharset(HttpServletRequest request)
	{
		String charsetName = null;
		if (request != null)
		{
			charsetName = request.getCharacterEncoding();
		}
		if (Strings.isEmpty(charsetName))
		{
			Application application = Application.get();
			if (application != null)
			{
				charsetName = application.getRequestCycleSettings().getResponseRequestEncoding();
			}
		}
		if (Strings.isEmpty(charsetName))
		{
			charsetName = "UTF-8";
		}
		return Charset.forName(charsetName);
	}

	/**
	 * set all required headers to disable caching
	 *
	 * "Pragma" is required for older browsers only supporting HTTP 1.0.
	 * "Cache" is recommended for HTTP 1.1.
	 * "Expires" additionally sets the content expiry in the past which effectively prohibits caching.
	 * "Date" is recommended in general
	 *
	 * @param response web response
	 */
	public static void disableCaching(WebResponse response)
	{
		response.setDateHeader("Date", System.currentTimeMillis());
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
	}
}
