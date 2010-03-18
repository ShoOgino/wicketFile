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
package org.apache.wicket.markup.transformer;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.response.StringResponse;

/**
 * This abstract container provides the means to post-process the markup generated by its child
 * components (excluding the containers tag)
 * <p>
 * Please see <code>IBehavior</code> for an alternative based on IBehavior
 * 
 * @see org.apache.wicket.markup.transformer.AbstractTransformerBehavior
 * @see org.apache.wicket.markup.transformer.ITransformer
 * 
 * @author Juergen Donnerstag
 * */
public abstract class AbstractOutputTransformerContainer extends MarkupContainer
	implements
		ITransformer
{
	private static final long serialVersionUID = 1L;

	/** Whether the containers tag shall be transformed as well */
	private boolean transformBodyOnly = true;

	/**
	 * Construct
	 * 
	 * @see org.apache.wicket.Component#Component(String)
	 */
	public AbstractOutputTransformerContainer(final String id)
	{
		super(id);
	}

	/**
	 * Construct
	 * 
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public AbstractOutputTransformerContainer(final String id, final IModel<?> model)
	{
		super(id, model);
	}

	/**
	 * You can choose whether the body of the tag excluding the tag shall be transformed or
	 * including the tag.
	 * 
	 * @param value
	 *            If true, only the body is applied to transformation.
	 * @return this
	 */
	public MarkupContainer setTransformBodyOnly(final boolean value)
	{
		transformBodyOnly = value;
		return this;
	}

	/**
	 * Create a new response object which is used to store the markup generated by the child
	 * objects.
	 * 
	 * @return Response object. Must not be null
	 */
	protected Response newResponse()
	{
		return new StringResponse();
	}

	/**
	 * 
	 * @see org.apache.wicket.markup.transformer.ITransformer#transform(org.apache.wicket.Component,
	 *      CharSequence)
	 */
	public abstract CharSequence transform(final Component component, final CharSequence output)
		throws Exception;

	/**
	 * @see org.apache.wicket.Component#onComponentTagBody(org.apache.wicket.markup.MarkupStream,
	 *      org.apache.wicket.markup.ComponentTag)
	 */
	@Override
	protected final void onComponentTagBody(final MarkupStream markupStream,
		final ComponentTag openTag)
	{
		if (transformBodyOnly == true)
		{
			execute(new Runnable()
			{
				public void run()
				{
					// Invoke default execution
					AbstractOutputTransformerContainer.super.onComponentTagBody(markupStream,
						openTag);
				}
			});
		}
		else
		{
			super.onComponentTagBody(markupStream, openTag);
		}
	}

	/**
	 * @see org.apache.wicket.Component#onRender()
	 */
	@Override
	protected final void onRender()
	{
		if (transformBodyOnly == false)
		{
			execute(new Runnable()
			{
				public void run()
				{
					// Invoke default execution
					AbstractOutputTransformerContainer.super.onRender();
				}
			});
		}
		else
		{
			super.onRender();
		}
	}

	/**
	 * 
	 * @param code
	 */
	private final void execute(final Runnable code)
	{
		// Temporarily replace the web response with a String response
		final Response webResponse = getResponse();

		try
		{
			// Create a new response object
			final Response response = newResponse();
			if (response == null)
			{
				throw new IllegalStateException("newResponse() must not return null");
			}

			// and make it the current one
			getRequestCycle().setResponse(response);

			// Invoke default execution
			code.run();

			try
			{
				// Tranform the data
				CharSequence output = transform(this, response.toString());
				webResponse.write(output);
			}
			catch (Exception ex)
			{
				throw new WicketRuntimeException("Error while transforming the output: " + this, ex);
			}
		}
		finally
		{
			// Restore the original response
			getRequestCycle().setResponse(webResponse);
		}
	}
}
