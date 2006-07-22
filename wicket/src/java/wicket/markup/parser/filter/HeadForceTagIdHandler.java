/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar
 * 2006) eelco12 $ $Revision: 5004 $ $Date: 2006-03-17 20:47:08 -0800 (Fri, 17
 * Mar 2006) $
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

import wicket.markup.ComponentTag;
import wicket.markup.MarkupElement;
import wicket.markup.parser.AbstractMarkupFilter;
import wicket.markup.parser.IMarkupFilter;
import wicket.util.string.AppendingStringBuffer;

/**
 * Handler that sets unique tag id for every inline script and style element in
 * &lt;wicket:head&gt;, unless the element already has one. <br/> This is needed
 * to be able to dedect multiple ajax header contribution. Tags that are not
 * inline (stript with src attribute set and link with href attribute set) do
 * not require id, because the detection is done by comparing URLs.
 * <p>
 * Tags with wicket:id are <strong>not processed</strong>. To
 * setOutputWicketId(true) on attached component is developer's responsibility.
 * FIXME: Really? And if so, document properly
 * 
 * @author Matej Knopp
 */
public class HeadForceTagIdHandler extends AbstractMarkupFilter
{
	/** Common prefix for all id's generated by this filter */
	private final String headElementIdPrefix;

	/** Unique value per markup file */
	private int counter = 0;

	/** we are in wicket:head */
	private boolean inHead = false;

	/**
	 * Construct.
	 * 
	 * @param parent
	 * @param markupFileClass
	 *            Used to generated the a common prefix for the id
	 */
	public HeadForceTagIdHandler(final IMarkupFilter parent, final Class markupFileClass)
	{
		super(parent);

		// generate the prefix from class name
		final AppendingStringBuffer buffer = new AppendingStringBuffer(markupFileClass.getName());
		for (int i = 0; i < buffer.getValue().length; ++i)
		{
			if (Character.isLetterOrDigit(buffer.getValue()[i]) == false)
			{
				buffer.getValue()[i] = '-';
			}
		}

		buffer.append("-");
		this.headElementIdPrefix = buffer.toString();
	}

	/**
	 * @see wicket.markup.parser.IMarkupFilter#nextTag()
	 */
	public MarkupElement nextTag() throws ParseException
	{
		final ComponentTag tag = super.nextComponentTag();

		if (tag != null)
		{
			// is it a <wicket:head> tag?
			if (tag.isWicketHeadTag())
			{
				this.inHead = tag.isOpen();
			}
			// no, it's not. Are we in <wicket:head> ?
			else if (this.inHead == true)
			{
				// is the tag open and has empty wicket:id?
				if ((tag.isWicketTag() == false) && (tag.getId() == null)
						&& (tag.isOpen() || tag.isOpenClose()) && needId(tag))
				{
					if (tag.getAttributes().get("id") == null)
					{
						tag.getAttributes().put("id", headElementIdPrefix + nextValue());
						tag.setModified(true);
					}
				}
			}
		}

		return tag;
	}

	/**
	 * Gets whether the tag needs to have a wicket id.
	 * 
	 * @param tag
	 *            The tag
	 * @return whether the tag needs to have a wicket id
	 */
	private final boolean needId(final ComponentTag tag)
	{
		final String name = tag.getName().toLowerCase();
		if (name.equals("script") && tag.getAttributes().containsKey("src") == false)
		{
			return true;
		}
		else if (name.equals("style") && tag.getAttributes().containsKey("href") == false)
		{
			return true;
		}

		return false;
	}

	/**
	 * @return increased counter value
	 */
	private final int nextValue()
	{
		return counter++;
	}
}
