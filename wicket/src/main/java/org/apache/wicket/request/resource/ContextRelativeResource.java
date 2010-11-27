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
package org.apache.wicket.request.resource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.resource.WebExternalResourceStream;

/**
 * Resource served from a file relative to the context root.
 * 
 * @author almaw
 */
public class ContextRelativeResource extends AbstractResource
{
	private static final long serialVersionUID = 1L;

	private final String path;

	/**
	 * Construct.
	 * 
	 * @param pathRelativeToContextRoot
	 */
	public ContextRelativeResource(String pathRelativeToContextRoot)
	{
		if (pathRelativeToContextRoot == null)
		{
			throw new IllegalArgumentException("Cannot have null path for ContextRelativeResource.");
		}

		// Make sure there is a leading '/'.
		if (!pathRelativeToContextRoot.startsWith("/"))
		{
			pathRelativeToContextRoot = "/" + pathRelativeToContextRoot;
		}
		path = pathRelativeToContextRoot;
	}

	@Override
	protected ResourceResponse newResourceResponse(final Attributes attributes)
	{
		final ResourceResponse resourceResponse = new ResourceResponse();

		if (resourceResponse.dataNeedsToBeWritten(attributes))
		{
			final WebExternalResourceStream webExternalResourceStream = new WebExternalResourceStream(
				path);

			resourceResponse.setContentType(webExternalResourceStream.getContentType());
			resourceResponse.setLastModified(webExternalResourceStream.lastModifiedTime().toDate());
			resourceResponse.setFileName(path);
			resourceResponse.setWriteCallback(new WriteCallback()
			{
				@Override
				public void writeData(final Attributes attributes)
				{
					try
					{
						InputStream inputStream = webExternalResourceStream.getInputStream();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						Streams.copy(inputStream, baos);
						attributes.getResponse().write(baos.toByteArray());
					}
					catch (ResourceStreamNotFoundException rsnfx)
					{
						throw new WicketRuntimeException(rsnfx);
					}
					catch (IOException iox)
					{
						throw new WicketRuntimeException(iox);
					}
				}
			});
		}

		return resourceResponse;
	}
}
