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
package wicket.util.diff;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.util.io.Streams;
import wicket.util.string.StringList;

/**
 * This is a utility class. It serves two purposes.
 * <p>
 * First: compare a string output generated by wicket with a file's content
 * (expected result).
 * <p>
 * Second: Create/replace the expected result file with the new content, if a
 * system property has be made available like
 * -Dwicket.replace.expected.results=true
 * 
 * @author Juergen Donnerstag
 */
public final class DiffUtil
{
	private static final Log log = LogFactory.getLog(DiffUtil.class);

	/**
	 * Compare the output generated by Wicket ("document") with the a previously
	 * generated file which contains the expected result.
	 * 
	 * @param document
	 *            Current output
	 * @param file
	 *            Expected ouput
	 * @param clazz
	 *            Used to load the file (relativ to clazz package)
	 * @param wicketTestCase
	 * @param failWithAssert
	 * @return true, if equal
	 * @throws IOException
	 */
	public static final boolean validatePage(String document, final Class clazz, final String file,
			boolean failWithAssert) throws IOException
	{
		String filename = clazz.getPackage().getName();
		filename = filename.replace('.', '/');
		filename += "/" + file;

		InputStream in = clazz.getClassLoader().getResourceAsStream(filename);
		if (in == null)
		{
			throw new IOException("File not found: " + filename);
		}

		String reference = Streams.readString(in);

		// replace all line endings with unix style line ending
		reference = reference.replaceAll("\n\r", "\n");
		reference = reference.replaceAll("\r\n", "\n");

		// replace all line endings with unix style line ending
		document = document.replaceAll("\n\r", "\n");
		document = document.replaceAll("\r\n", "\n");

		boolean equals = document.equals(reference);
		if (equals == false)
		{
			// Change the condition to true, if you want to make the new output
			// the reference output for future tests. That is, it is regarded as
			// correct. It'll replace the current reference files. Thus change
			// it only for one test-run.
			// -Dwicket.replace.expected.results=true
			final String prop = System.getProperty("wicket.replace.expected.results");
			if (prop != null)
			{
				in.close();
				in = null;

				replaceExpectedResultFile(document, clazz, file);
				return true;
			}

			if (failWithAssert)
			{
				Assert.assertEquals(filename, reference, document);
			}
			else
			{
				log.error("File name: " + file);
				/*  */
				log.error("===================");
				log.error(reference);
				log.error("===================");

				log.error(document);
				log.error("===================");
				/* */

				String[] test1 = StringList.tokenize(reference, "\n").toArray();
				String[] test2 = StringList.tokenize(document, "\n").toArray();
				Diff df = new Diff(test1);
				Revision r;
				try
				{
					r = df.diff(test2);
				}
				catch (DifferentiationFailedException e)
				{
					throw new RuntimeException(e);
				}

				System.out.println(r.toString());
			}
		}

		return equals;
	}

	/**
	 * Replace the expected result file with the current output.
	 * 
	 * @param document
	 *            How the expected result should look like
	 * @param clazz
	 *            Used to load the file (relativ to clazz package)
	 * @param file
	 *            The name of the expected result file to be created
	 * @throws IOException
	 */
	public final static void replaceExpectedResultFile(final String document, final Class clazz,
			final String file) throws IOException
	{
		String filename = clazz.getPackage().getName();
		filename = filename.replace('.', '/');
		filename += "/" + file;

		final URL url = clazz.getClassLoader().getResource(filename);
		filename = url.getFile();
		filename = filename.replaceAll("/target/test-classes/", "/src/test/java/");
		PrintWriter out = new PrintWriter(new FileOutputStream(filename));
		out.print(document);
		out.close();
	}

}