/*
 * $Id$ $Revision:
 * 1.5 $ $Date$
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
package wicket.markup.parser.filter;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.markup.ComponentTag;
import wicket.markup.MarkupElement;
import wicket.markup.parser.AbstractMarkupFilter;
import wicket.markup.parser.IMarkupFilter;
import wicket.markup.parser.XmlTag;

/**
 * This is a markup inline filter. It identifies tags which are allowed
 * open-close in the markup, but which for Wicket to be processed correctly
 * must be open-body-close.
 * 
 * @author Juergen Donnerstag
 */
public final class TagTypeHandler extends AbstractMarkupFilter
{
	/** Logging */
	private static Log log = LogFactory.getLog(TagTypeHandler.class);

	/** Tag stack to find balancing tags */
	final private Stack stack = new Stack();

	/** Map of simple tags. */
	private static final Map requireOpenBodyCloseTag = new HashMap();

	static
	{
		// Tags which require open-body-close
		requireOpenBodyCloseTag.put("select", Boolean.TRUE);
	}

	/**
	 * Construct.
	 * 
	 * @param parent
	 *            The next MarkupFilter in the chain
	 */
	public TagTypeHandler(final IMarkupFilter parent)
	{
		super(parent);
	}

	/**
	 * Get the next MarkupElement from the parent MarkupFilter and handle it if
	 * the specific filter criteria are met. Depending on the filter, it may
	 * return the MarkupElement unchanged, modified or it remove by asking the
	 * parent handler for the next tag.
	 * 
	 * @see wicket.markup.parser.IMarkupFilter#nextTag()
	 * @return Return the next eligible MarkupElement
	 */
	public MarkupElement nextTag() throws ParseException
	{
		// If there is something in the stack, ...
		while (stack.size() > 0)
		{
			final ComponentTag top = (ComponentTag)stack.pop();
			return top;
		}

		// Get the next tag. If null, no more tags are available
		// in the markup
		final ComponentTag tag = (ComponentTag)getParent().nextTag();
		if (tag == null)
		{
			return tag;
		}

		if (tag.isOpenClose())
		{
			String name = tag.getName();
			if (tag.getNamespace() != null)
			{
				name = tag.getNamespace() + ":" + tag.getName();
			}
			
			// Pop any simple tags off the top of the stack
			if (requiresOpenBodyCloseTag(name))
			{
				tag.setType(XmlTag.OPEN);
				XmlTag closeTag = new XmlTag();
				closeTag.setType(XmlTag.CLOSE);
				closeTag.setName(tag.getName());
				closeTag.setNamespace(tag.getNamespace());
				closeTag.closes(tag);
				
				stack.push(new ComponentTag(closeTag));
			}
		}

		return tag;
	}

	/**
	 * Gets whether this tag does not require open-body-close tags.
	 * 
	 * @param name
	 *            The tag's name, e.g. a, br, div, etc.
	 * @return True if this tag must be converted into open-body-close if openClose
	 */
	public static boolean requiresOpenBodyCloseTag(final String name)
	{
		return requireOpenBodyCloseTag.get(name) != null;
	}
}
