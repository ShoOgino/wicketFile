/*
 * $Id: OutputTransformerContainer.java,v 1.1 2005/12/31 10:09:31 jdonnerstag
 * Exp $ $Revision$ $Date$
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
package wicket.markup.outputTransformer;

import wicket.MarkupContainer;
import wicket.Response;
import wicket.WicketRuntimeException;
import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.model.IModel;
import wicket.response.StringResponse;

/**
 * This abstract container provides the means to post-process the markup
 * generated by its child components.
 * 
 * @author Juergen Donnerstag
 */
public abstract class AbstractOutputTransformerContainer extends MarkupContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct
	 * 
	 * @see wicket.Component#Component(String)
	 */
	public AbstractOutputTransformerContainer(final String id)
	{
		super(id);
	}

	/**
	 * Construct
	 * 
	 * @see wicket.Component#Component(String, IModel)
	 */
	public AbstractOutputTransformerContainer(final String id, final IModel model)
	{
		super(id, model);
	}

	/**
	 * 
	 * @see wicket.MarkupContainer#getMarkupType()
	 */
	public String getMarkupType()
	{
		return "xsl";
	}
	
	/**
	 * Create a new response object which is used to store the markup generated
	 * by the child objects.
	 * 
	 * @return Response object. Must not be null
	 */
	protected Response newResponse()
	{
		return new StringResponse();
	}

	/**
	 * Will be invoked after all child components have been processed to allow
	 * for transforming the markup generated.
	 * 
	 * @param output
	 *            The markup generated by the child components
	 * @return The output which will be appended to the orginal response
	 * @throws Exception
	 */
	protected abstract String transform(final String output) throws Exception;

	/**
	 * @see wicket.Component#onComponentTagBody(wicket.markup.MarkupStream,
	 *      wicket.markup.ComponentTag)
	 */
	protected final void onComponentTagBody(final MarkupStream markupStream,
			final ComponentTag openTag)
	{
		// Temporarily replace the web response with a String response
		final Response webResponse = this.getResponse();

		try
		{
			// Create a new response object
			final Response response = newResponse();
			if (response == null)
			{
				throw new IllegalStateException("newResponse() must not return null");
			}

			// and make it the current one
			this.getRequestCycle().setResponse(response);

			// Invoke default execution
			super.onComponentTagBody(markupStream, openTag);

			try
			{
				// Tranform the data
				String output = transform(response.toString());
				webResponse.write(output);
			}
			catch (Exception ex)
			{
				throw new WicketRuntimeException(
						"Error while transforming the output: " + this, ex);
			}
		}
		finally
		{
			// Restore the original response
			this.getRequestCycle().setResponse(webResponse);
		}
	}
}
