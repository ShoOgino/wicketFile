/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup.html.link;

import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.html.HtmlContainer;


/**
 * Simple &lt;a href="..."&gt; pointing to any URL. Usually this is used
 * for links to destinations outside of Wicket.
 * 
 * @author Juergen Donnerstag
 */
public class ExternalLink extends HtmlContainer
{
    /** the href attribute. */
    final private String href;

    /** this links' label. */
    final private String label;
    
    /**
     * Constructor.
     * @param componentName The name of this component
     * @param href the href attribute to set
     * @param label the label (body)
     */
    public ExternalLink(final String componentName, final String href, final String label)
    {
        super(componentName);
        
        this.href = href;
        this.label = label;
    }

    /**
     * Constructor.
     * @param componentName The name of this component
     * @param href the href attribute to set
     */
    public ExternalLink(final String componentName, final String href)
    {
        this(componentName, href, null);
    }
    
    /**
     * Processes the component tag.
     * @param tag Tag to modify
     * @see wicket.Component#handleComponentTag(wicket.markup.ComponentTag)
     */
    protected void handleComponentTag(ComponentTag tag)
    {
        if (href != null)
        {
            tag.put("href", href.replaceAll("&", "&amp;"));
        }
    }

    /**
     * Handle the container's body.
     * @param markupStream The markup stream
     * @param openTag The open tag for the body
     * @see wicket.Component#handleComponentTagBody(wicket.markup.MarkupStream, wicket.markup.ComponentTag)
     */
    protected void handleComponentTagBody(MarkupStream markupStream,
            ComponentTag openTag)
    {
        this.checkComponentTag(openTag, "a");
        if (label != null)
        {
            replaceComponentTagBody(markupStream, openTag, label);
        }
        else
        {
            super.handleComponentTagBody(markupStream, openTag);
        }
    }
}