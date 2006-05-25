/*
 * $Id: DiffUtil.java 5395 2006-04-16 13:42:28 +0000 (Sun, 16 Apr 2006)
 * jdonnerstag $ $Revision$ $Date: 2006-04-16 13:42:28 +0000 (Sun, 16 Apr
 * 2006) $
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
package wicket.examples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

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
	private static Log log = LogFactory.getLog(DiffUtil.class);

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
	 * @return true, if equal
	 * @throws IOException
	 */
	public static final boolean validatePage(final String document, final Class clazz,
			final String file) throws IOException
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

			log.error("File name: " + file);
			/*  */
			log.error("===================");
			log.error(reference);
			log.error("===================");

			log.error(document);
			log.error("===================");
			/* */

			String[] test1 = StringList.tokenize(document, "\n").toArray();
			String[] test2 = StringList.tokenize(reference, "\n").toArray();
			Diff diff = new Diff(test1, test2);
			Diff.change script = diff.diff_2(false);
			DiffPrint.Base p = new DiffPrint.UnifiedPrint(test1, test2);
			p.setOutput(new PrintWriter(System.err));
			p.print_script(script);
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
		filename = filename.replaceAll("/target/test-classes/", "/src/test/");
		PrintWriter out = new PrintWriter(new FileOutputStream(filename));
		out.print(document);
		out.close();
	}

}