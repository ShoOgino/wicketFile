/*
 * $Id$
 * $Revision$
 * $Date$
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
package wicket;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import wicket.markup.ComponentTag;
import wicket.markup.html.HtmlHeaderContainer;
import wicket.markup.html.IHeaderContributor;
import wicket.markup.html.PackageResourceReference;
import wicket.markup.html.ajax.IBodyOnloadContributor;
import wicket.util.io.Streams;
import wicket.util.resource.IResourceStream;

/**
 * Abstract class for handling Ajax roundtrips. This class serves as a base for
 * javascript specific implementations, like ones based on Dojo or Scriptaculous.
 *
 * @author Eelco Hillenius
 */
public abstract class AjaxHandler
	implements Serializable, IHeaderContributor, IBodyOnloadContributor
{
	/** the component that this handler is bound to. */
	private Component component;

	/** The actual raw resource this class is rendering */
	protected IResourceStream resourceStream;

	/** thread local for onload contributions. */
	private static final ThreadLocal bodyOnloadContribHolder = new ThreadLocal();

	/** thread local for head contributions. */
	private static final ThreadLocal headContribHolder = new ThreadLocal();

	/**
	 * Construct.
	 */
	public AjaxHandler()
	{
	}

	/**
	 * @see wicket.markup.html.ajax.IBodyOnloadContributor#getBodyOnload()
	 */
	public final String getBodyOnload()
	{
		String staticContrib = null;
		Set contributors = (Set)bodyOnloadContribHolder.get();

		// were any contributors set?
		if (contributors == null)
		{
			contributors = new HashSet(1);
			bodyOnloadContribHolder.set(contributors);
		}

		// get the id of the implementation; we need this trick to be
		// able to support multiple implementations
		String implementationId = getImplementationId();

		// was a contribution for this specific implementation done yet?
		if(!contributors.contains(implementationId))
		{
			staticContrib = getBodyOnloadInitContribution();
			contributors.add(implementationId);
		}

		String contrib = getBodyOnloadContribution();
		if (staticContrib != null)
		{
			return (contrib != null) ? staticContrib + contrib : staticContrib;
		}
		return contrib;
	}

	/**
	 * @see wicket.markup.html.IHeaderContributor#renderHead(wicket.markup.html.HtmlHeaderContainer)
	 */
	public final void renderHead(HtmlHeaderContainer container)
	{
		Set contributors = (Set)headContribHolder.get();

		//	were any contributors set?
		if (contributors == null)
		{
			contributors = new HashSet(1);
			headContribHolder.set(contributors);
		}

		// get the id of the implementation; we need this trick to be
		// able to support multiple implementations
		String implementationId = getImplementationId();

		// was a contribution for this specific implementation done yet?
		if(!contributors.contains(implementationId))
		{
			renderHeadInitContribution(container);
			contributors.add(implementationId);
		}

		renderHeadContribution(container);
	}

	/**
	 * Called any time a component that has this handler registered is rendering the component tag.
	 * Use this method e.g. to bind to javascript event handlers of the tag
	 * @param tag the tag that is rendered
	 */
	public void onComponentTag(ComponentTag tag)
	{
	}

	/**
	 * Gets the url that references this handler.
	 * @return the url that references this handler
	 */
	public final String getCallbackUrl()
	{
		return component.urlFor(this);
	}

	/**
	 * Gets the unique id of an ajax implementation. This should be implemented by
	 * base classes only - like the dojo or scriptaculous implementation - to provide
	 * a means to differentiate between implementations while not going to the level
	 * of concrete implementations. It is used to ensure 'static' header contributions
	 * are done only once per implementation.
	 * @return unique id of an ajax implementation
	 */
	protected abstract String getImplementationId();

	/**
	 * Gets the response to render to the requester.
	 * @return the response to render to the requester
	 */
	protected abstract IResourceStream getResponse();

	/**
	 * Do a one time (per page) header contribution that is the same for all ajax variant
	 * implementations (e.g. Dojo, Scriptaculous).
	 * This implementation does nothing.
	 * @param container head container
	 */
	protected void renderHeadInitContribution(HtmlHeaderContainer container)
	{
	}

	/**
	 * Let this handler print out the needed header contributions.
	 * This implementation does nothing.
	 * @param container head container
	 */
	protected void renderHeadContribution(HtmlHeaderContainer container)
	{
	}

	/**
	 * Gets  the component that this handler is bound to.
	 * @return  the component that this handler is bound to
	 */
	protected final Component getComponent()
	{
		return component;
	}

	/**
	 * Called when the component was bound to it's host component. You can get the bound host component by calling getComponent.
	 */
	protected void onBind()
	{
	}

	/**
	 * One time (per page) body onload contribution that is the same for all ajax variant
	 * implementations (e.g. Dojo, Rico, Qooxdoo).
	 * @return the onload statement(s) for the body component
	 */
	protected String getBodyOnloadInitContribution()
	{
		return null;
	}

	/**
	 * Gets the onload statement(s) for the body component.
	 * Override this method to provide custom contributions.
	 * @return the onload statement(s) for the body component
	 */
	protected String getBodyOnloadContribution()
	{
		return null;
	}

	/**
	 * Called to indicate that the component that has this handler registered has been rendered.
	 * Use this method to do any cleaning up of temporary state
	 */
	protected void onComponentRendered()
	{
	}

	/**
	 * Configures the response, default by setting the content type and length.
	 * @param response the response
	 * @param resourceStream the resource stream that will be rendered
	 */
	protected void configure(final Response response, final IResourceStream resourceStream)
	{
		// Configure response with content type of resource
		response.setContentType(getResponseType());
		response.setContentLength((int)resourceStream.length());
	}

	/**
	 * Gets the response type mime, e.g. 'text/html' or 'text/javascript'.
	 * @return the response type mime
	 */
	protected String getResponseType()
	{
		return "text/html";
	}

	/**
	 * Convenience method to add a javascript reference.
	 * @param container the header container
	 * @param ref reference to add
	 */
	protected void addJsReference(HtmlHeaderContainer container, PackageResourceReference ref)
	{
		String url = container.getPage().urlFor(ref.getPath());
		String s = 
			"\t<script language=\"JavaScript\" type=\"text/javascript\" " +
			"src=\"" + url + "\"></script>\n";
		write(container, s);
	}

	/**
	 * Bind this handler to the given component.
	 *
	 * @param hostComponent the component to bind to
	 */
	final void bind(Component hostComponent)
	{
		if (hostComponent == null)
		{
			throw new NullPointerException("argument hostComponent must be not null");
		}

		if (this.component != null)
		{
			throw new IllegalStateException("this kind of handler cannot be attached to " +
					"multiple components; it is allready attached to component " + this.component +
					", but component " + hostComponent + " wants to be attached too");

		}

		this.component = hostComponent;

		// call the calback
		onBind();
	}

	/**
	 * Called to indicate that the component that has this handler registered has been rendered.
	 * Use this method to do any cleaning up of temporary state.
	 */
	final void internalOnComponentRendered()
	{
		bodyOnloadContribHolder.set(null);
		headContribHolder.set(null);
		onComponentRendered();
	}

	/**
	 * Called when an Ajax request is to be handled.
	 */
	final void onRequest()
	{
		respond();
	}

	/**
	 * Writes the given string to the header container.
	 * @param container the header container
	 * @param s the string to write
	 */
	private final void write(HtmlHeaderContainer container, String s)
	{
		container.getResponse().write(s);
	}

	/**
	 * Responds on the event request.
	 */
	private final void respond()
	{
		try
		{
			// Get request cycle
			final RequestCycle cycle = RequestCycle.get();

			// The cycle's page is set to null so that it won't be rendered back to
			// the client since the resource being requested has nothing to do with pages
			cycle.setResponsePage((Page)null);

			resourceStream = getResponse();
			if (resourceStream == null)
			{
				throw new WicketRuntimeException("Could not get resource stream");
			}

			// Get servlet response to use when responding with resource
			final Response response = cycle.getResponse();

			configure(response, resourceStream);

			// Respond with resource
			respond(response);
		}
		finally
		{
			resourceStream = null;
		}
	}

	/**
	 * Respond.
	 * @param response the response to write to
	 */
	private final void respond(final Response response)
	{
		try
		{
			final OutputStream out = response.getOutputStream();
			try
			{
				Streams.copy(resourceStream.getInputStream(), out);
			}
			finally
			{
				resourceStream.close();
				out.flush();
			}
		}
		catch (Exception e)
		{
			throw new WicketRuntimeException("Unable to render resource stream " + resourceStream, e);
		}
	}
}
