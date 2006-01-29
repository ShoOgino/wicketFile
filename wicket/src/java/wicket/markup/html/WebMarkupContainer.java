/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.markup.html;

import wicket.Component;
import wicket.MarkupContainer;
import wicket.Response;
import wicket.markup.ComponentTag;
import wicket.markup.Markup;
import wicket.markup.MarkupElement;
import wicket.markup.MarkupStream;
import wicket.markup.WicketTag;
import wicket.markup.html.internal.HtmlHeaderContainer;
import wicket.markup.resolver.IComponentResolver;
import wicket.model.IModel;
import wicket.response.NullResponse;
import wicket.util.lang.Classes;

/**
 * A container of HTML markup and components. It is very similar to the base
 * class MarkupContainer, except that the markup type is defined to be HTML.
 * 
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 */
public class WebMarkupContainer extends MarkupContainer 
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see Component#Component(String)
	 */
	public WebMarkupContainer(final String id)
	{
		super(id);
	}

	/**
	 * @see wicket.Component#Component(String, IModel)
	 */
	public WebMarkupContainer(final String id, IModel model)
	{
		super(id, model);
	}

	/**
	 * Gets the markup type for this component.
	 * 
	 * @return Markup type of HTML
	 */
	public final String getMarkupType()
	{
		return "html";
	}

	/**
	 * Print to the web response what ever the component wants to contribute to
	 * the head section.
	 * 
	 * @param container
	 *            The HtmlHeaderContainer
	 */
	public void renderHeadFromAssociatedMarkupFile(final HtmlHeaderContainer container)
	{
		// Ask the child component if it has something to contribute
		final HeaderPartContainer headerPart = getHeaderPart();

		// If the child component has something to contribute to
		// the header and in case the very same Component has not
		// contributed to the page, than ...
		// A component's header section must only be added once,
		// no matter how often the same Component has been added
		// to the page or any other container in the hierachy.
		if (headerPart != null)
		{
			if (container.get(headerPart.getId()) == null)
			{
				container.autoAdd(headerPart);
	
				// Check if the component requires some <body onload="..">
				// attribute to be copied to the page's body tag.
				checkBodyOnLoad();
			}
			else
			{
				// TODO I haven't found a more efficient solution yet
				// Already added but all the components in this header part must be
				// touched (that they are rendered)
				Response response = getRequestCycle().getResponse();
				try
				{
					getRequestCycle().setResponse(NullResponse.getInstance());
					container.autoAdd(headerPart);
				}
				finally
				{
					getRequestCycle().setResponse(response);
				}
			}
		}
	}

	/**
	 * Check if the component requires some <body onload=".."> attribute to be
	 * copied to the page's body tag.
	 */
	private void checkBodyOnLoad()
	{
		// Gracefully getAssociateMarkupStream. Throws no exception in case
		// markup is not found
		final MarkupStream associatedMarkupStream = getApplication().getMarkupCache()
				.getMarkupStream(this, false);

		// No associated markup => no body tag
		if (associatedMarkupStream == null)
		{
			return;
		}

		// Remember the current position within markup, where we need to
		// go back to, at the end.
		int index = associatedMarkupStream.getCurrentIndex();

		try
		{
			// Start at the beginning
			associatedMarkupStream.setCurrentIndex(0);

			// Iterate the markup and find <body onload="...">
			do
			{
				final MarkupElement element = associatedMarkupStream.get();
				if (element instanceof ComponentTag)
				{
					final ComponentTag tag = (ComponentTag)element;
					if ("body".equalsIgnoreCase(tag.getName()))
					{
						final String onLoad = tag.getAttributes().getString("onload");
						if (onLoad != null)
						{
							((WebPage)getPage()).appendToBodyOnLoad(onLoad);
						}

						// There can only be one body tag
						break;
					}
				}
			}
			while (associatedMarkupStream.next() != null);
		}
		finally
		{
			// Make sure we return to the orginal position in the markup
			associatedMarkupStream.setCurrentIndex(index);
		}
	}

	/**
	 * Gets the header part for the markup container. Returns null if it doesn't
	 * contribute to a header.
	 * 
	 * @return the header part for this markup container or null if it doesn't
	 *         contribute anything.
	 */
	private final HeaderPartContainer getHeaderPart()
	{
		// Gracefully getAssociateMarkupStream. Throws no exception in case
		// markup is not found
		final MarkupStream associatedMarkupStream = getApplication().getMarkupCache()
				.getMarkupStream(this, false);

		// No associated markup => no header section
		if (associatedMarkupStream == null)
		{
			return null;
		}

		// Lazy scan the markup for a header component tag, if necessary
		// 'index' will be where <wicket:head> resides in the markup
		int index = Markup.NO_HEADER_FOUND;
		if (associatedMarkupStream.getHeaderIndex() != Markup.NO_HEADER_FOUND)
		{
			// The markup has been scanned already. Get the index where the
			// header tag resides from the markup
			index = associatedMarkupStream.getHeaderIndex();
		}

		// Ok, finished scanning the markup for header tag
		// If markup contains a header section, handle it now.
		if (index != Markup.NO_HEADER_FOUND)
		{
			// Position markup stream at beginning of header tag
			associatedMarkupStream.setCurrentIndex(index);

			// Create a HtmlHeaderContainer for the header tag found
			final MarkupElement element = associatedMarkupStream.get();
			if (element instanceof WicketTag)
			{
				final WicketTag wTag = (WicketTag)element;
				if ((wTag.isHeadTag() == true) && (wTag.getNamespace() != null))
				{
					// found <wicket:head>
					// create a unique id for the HtmlHeaderContainer to be
					// created
					final String headerId = "_" + Classes.name(this.getClass())
							+ this.getVariation() + "Header";

					// Create the header container and associate the markup with
					// it
					HeaderPartContainer headerContainer = new HeaderPartContainer(
							headerId, this, wTag.getAttributes().getString("scope"));
					headerContainer.setMarkupStream(associatedMarkupStream);
					headerContainer.setRenderBodyOnly(true);

					// The container does have a header component
					return headerContainer;
				}
			}
		}

		// Though the container does have markup, it does not have a
		// <wicket:head> region.
		return null;
	}

	/**
	 * For each wicket:head tag a HeaderPartContainer is created and 
	 * added to the HtmlHeaderContainer which has been added to the Page.
	 */
	private static final class HeaderPartContainer extends WebMarkupContainer
			implements IComponentResolver
	{
		private static final long serialVersionUID = 1L;

		/** The panel or bordered page the header part is associated with */
		private final MarkupContainer container;

		/** <wicket:head scope="...">. A kind of namespace */
		private final String scope;
		
		/**
		 * @param id The component id
		 * @param container The Panel (or bordered page) the header part is associated with
		 * @param scope The scope of the wicket:head tag
		 */
		public HeaderPartContainer(final String id, final MarkupContainer container, final String scope)
		{
			super(id);
			this.container = container;
			this.scope = scope;
		}

		/**
		 * Get the scope of the header part
		 * 
		 * @return The scope name
		 */
		public final String getScope()
		{
			return this.scope;
		}
		
		/**
		 * 
		 * @see wicket.MarkupContainer#isTransparent()
		 */
		public boolean isTransparent()
		{
			return true;
		}
		
		/**
		 * @see IComponentResolver#resolve(MarkupContainer, MarkupStream, ComponentTag)
		 */
		public final boolean resolve(final MarkupContainer container,
				final MarkupStream markupStream, final ComponentTag tag)
		{
			// The tag must be resolved against the panel and not against the page
			Component component = this.container.get(tag.getId());
			if (component != null)
			{
				component.render(markupStream);
				return true;
			}

			return false;
		}
	}
}