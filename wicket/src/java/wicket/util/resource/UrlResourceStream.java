/*
 * $Id$ $Revision:
 * 1.2 $ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.util.time.Time;

/**
 * UrlResourceStream implements IResource for URLs.
 * 
 * @see wicket.util.resource.IResourceStream
 * @see wicket.util.watch.IModifiable
 * @author Jonathan Locke
 */
public final class UrlResourceStream extends AbstractResourceStream
{
	/** Logging */
	private static Log log = LogFactory.getLog(UrlResourceStream.class);

	/** Resource stream */
	private transient InputStream inputStream;

	/** The URL to this resource */
	private URL url;

	/** Length of stream */
	private int contentLength;

	/** Content type for stream */
	private String contentType;

	/** Time the stream was last modified */
	private long lastModified;

	/**
	 * Private constructor to force use of static factory methods.
	 * 
	 * @param url
	 *            URL of resource
	 */
	public UrlResourceStream(final URL url)
	{
		// Save URL
		this.url = url;
		try
		{
			URLConnection connection = url.openConnection();
			contentLength = connection.getContentLength();
			contentType = connection.getContentType();
			lastModified = connection.getLastModified();
			if (connection instanceof HttpURLConnection)
			{
				((HttpURLConnection)connection).disconnect();
			}
		}
		catch (IOException ex)
		{
			// It should be impossible to get here or the original URL
			// couldn't have been constructed. But we re-throw with details
			// anyway.
			final IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
					"Invalid URL parameter " + url);
			illegalArgumentException.initCause(ex);
			throw illegalArgumentException;
		}
	}

	/**
	 * Closes this resource.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		if (inputStream != null)
		{
			inputStream.close();
			inputStream = null;
		}
	}

	/**
	 * @return The content type of this resource, such as "image/jpeg" or
	 *         "text/html"
	 */
	public String getContentType()
	{
		if (contentType == null)
		{
			return URLConnection.getFileNameMap().getContentTypeFor(url.getFile());
		}
		return contentType;
	}

	/**
	 * @return A readable input stream for this resource.
	 * @throws ResourceStreamNotFoundException
	 */
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		if (inputStream == null)
		{
			try
			{
				inputStream = url.openStream();
			}
			catch (IOException e)
			{
				throw new ResourceStreamNotFoundException("Resource " + url
						+ " could not be opened", e);
			}
		}

		return inputStream;
	}

	/**
	 * @return The URL to this resource (if any)
	 */
	public URL getURL()
	{
		return url;
	}

	/**
	 * @see wicket.util.watch.IModifiable#lastModifiedTime()
	 * @return The last time this resource was modified
	 */
	public Time lastModifiedTime()
	{
		return Time.milliseconds(lastModified);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return url.toString();
	}

	/**
	 * @see wicket.util.resource.IResourceStream#length()
	 */
	public long length()
	{
		return contentLength;
	}
}
