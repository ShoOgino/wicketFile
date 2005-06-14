/*
 * $Id: WicketTagComponentResolver.java,v 1.4 2005/01/18 08:04:29 jonathanlocke
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
package wicket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.WicketTag;

/**
 * THIS IS PART OF MARKUP INHERITANCE AND CURRENTLY EXPERIMENTAL ONLY.
 * 
 * Detect &lt;wicket:extend&gt; regions and thus allows to implement markup
 * inheritance. See MarkupInheritanceContainer for additional information.
 * 
 * @author Juergen Donnerstag
 */
public class MarkupInheritanceResolver implements IComponentResolver
{
	/** Logging */
	private static Log log = LogFactory.getLog(MarkupInheritanceResolver.class);

	/**
	 * @see wicket.IComponentResolver#resolve(MarkupContainer,
	 *      MarkupStream, ComponentTag)
	 * @param container
	 *            The container parsing its markup
	 * @param markupStream
	 *            The current markupStream
	 * @param tag
	 *            The current component tag while parsing the markup
	 * @return true, if componentId was handle by the resolver. False, otherwise
	 */
	public boolean resolve(final MarkupContainer container, final MarkupStream markupStream,
			final ComponentTag tag)
	{
		// It must be <wicket:...>
		if (tag instanceof WicketTag)
		{
			final WicketTag wicketTag = (WicketTag)tag;
			
			// It must be <wicket:extend...>
			if (wicketTag.isExtendTag())
			{
			    if (markupStream.getCurrentIndex() != 0)
			    {
			        log.warn("nothing should precede the <wicket:extend> tag");
			    }

			    // wicket:extend regions are handled by MarkupInheritanceContainer
		        final MarkupInheritanceContainer inherit = new MarkupInheritanceContainer();
	            container.autoAdd(inherit);
		        
			    return true;
			}
		}
		// We were not able to handle the componentId
		return false;
	}
}