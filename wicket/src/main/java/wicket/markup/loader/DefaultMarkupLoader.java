/*
 * $Id: MarkupCache.java 4639 2006-02-26 01:44:07 -0800 (Sun, 26 Feb 2006)
 * jdonnerstag $ $Revision$ $Date: 2006-02-26 01:44:07 -0800 (Sun, 26 Feb
 * 2006) $
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
package wicket.markup.loader;

import java.io.IOException;

import wicket.Application;
import wicket.MarkupContainer;
import wicket.markup.MarkupElement;
import wicket.markup.MarkupFragment;
import wicket.markup.MarkupResourceStream;
import wicket.util.resource.ResourceStreamNotFoundException;

/**
 * Load the markup via the MarkupParser, not more, not less. Caching is provided
 * separately as well as Inherited-Markup merging.
 * 
 * @author Juergen Donnerstag
 */
public class DefaultMarkupLoader implements IMarkupLoader
{
	/** The Wicket application */
	private final Application application;

	/**
	 * Constructor.
	 * 
	 * @param application
	 */
	public DefaultMarkupLoader(final Application application)
	{
		this.application = application;
	}

	/**
	 * @see wicket.markup.loader.IMarkupLoader#loadMarkup(wicket.MarkupContainer,
	 *      wicket.markup.MarkupResourceStream)
	 */
	public final MarkupFragment loadMarkup(final MarkupContainer container,
			final MarkupResourceStream markupResourceStream) throws IOException,
			ResourceStreamNotFoundException
	{
		// read and parse the markup
		MarkupFragment markup = application.getMarkupSettings().getMarkupParserFactory()
				.newMarkupParser(markupResourceStream).readAndParse();

		checkHeaders(markup);

		return markup;
	}

	/**
	 * On Pages with wicket:head and automatically added head tag, move the
	 * wicket:head tags inside the head tag.
	 * 
	 * @param markup
	 */
	private void checkHeaders(final MarkupFragment markup)
	{
		final MarkupFragment header = MarkupFragmentUtils.getHeadTag(markup);
		if (header != null)
		{
			markup.visitChildren(MarkupFragment.class, new MarkupFragment.IVisitor()
			{
				public Object visit(final MarkupElement element, final MarkupFragment parent)
				{
					if (element == header)
					{
						return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
					}

					if (((MarkupFragment)element).getTag().isWicketHeadTag())
					{
						if (parent.removeMarkupElement(element) == true)
						{
							header.addMarkupElement(header.size() - 1, element);
						}
					}

					return CONTINUE_TRAVERSAL;
				}
			});
		}
	}
}
