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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import wicket.Page;
import wicket.markup.parser.IMarkupFilter;
import wicket.markup.parser.IXmlPullParser;
import wicket.markup.parser.filter.BodyOnLoadHandler;
import wicket.markup.parser.filter.HtmlHandler;
import wicket.markup.parser.filter.HtmlHeaderSectionHandler;
import wicket.markup.parser.filter.TagTypeHandler;
import wicket.markup.parser.filter.WicketLinkTagHandler;
import wicket.markup.parser.filter.WicketMessageTagHandler;
import wicket.markup.parser.filter.WicketParamTagHandler;
import wicket.markup.parser.filter.WicketRemoveTagHandler;
import wicket.markup.parser.filter.WicketTagIdentifier;
import wicket.settings.IMarkupSettings;
import wicket.util.resource.ResourceStreamNotFoundException;
import wicket.util.value.ValueMap;


/**
 * This is a Wicket MarkupParser specifically for (X)HTML. It makes use of a
 * streaming XML parser to read the markup and IMarkupFilters to remove
 * comments, identify Wicket relevant tags, apply html specific treatments
 * etc.. <p>
 * The result will be an Markup object, which is basically a list, containing
 * Wicket relevant tags and RawMarkup.
 *
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 */
public class MarkupParser
{
    /** Name of desired componentId tag attribute.
     * E.g. &lt;tag wicket:id="..."&gt; */
    private String wicketNamespace = ComponentTag.DEFAULT_WICKET_NAMESPACE;

    /** True to strip out HTML comments. */
    private boolean stripComments;

    /** True to compress multiple spaces/tabs or line endings to a single space or line ending. */
    private boolean compressWhitespace;

    /** if true, <wicket:param ..> tags will be removed from markup */
    private boolean stripWicketTag;

    /** If true, MarkupParser will automatically create a WicketTag for
     * all tags surrounding a href attribute with a relative path to a
     * html file. E.g. &lt;a href="Home.html"&gt;
     */
    private boolean automaticLinking = false;

    /** The XML parser to use */
    private final IXmlPullParser xmlParser;

    /** The markup handler chain: each filter has a specific task */
    private IMarkupFilter markupFilterChain;

    /** The handler detecting wicket tags: wicket namespace */
    private WicketTagIdentifier detectWicketComponents;

    /** The resource stream containing the markup. May be null */
    private MarkupResourceStream resource;
    
    /**
     * Constructor.
     * @param xmlParser The streaming xml parser to read and parse the markup
     * @param wicketNamespace The wicket namespace to identifiy wicket tags; e.g. wicket:id="xxx"
     */
    public MarkupParser(final IXmlPullParser xmlParser, final String wicketNamespace)
    {
        this.xmlParser = xmlParser;
        this.wicketNamespace = wicketNamespace;
    }

    /**
     * Constructor.
     * 
     * @param xmlParser The streaming xml parser to read and parse the markup
     */
    public MarkupParser(final IXmlPullParser xmlParser)
    {
        this.xmlParser = xmlParser;
    }

    /**
	 * Configure the markup parser based on Wicket application settings
	 * @param settings Wicket application settings
	 */
	public final void configure(final IMarkupSettings settings)
	{
        this.stripWicketTag = settings.getStripWicketTags();
        this.stripComments = settings.getStripComments();
        this.compressWhitespace = settings.getCompressWhitespace();
        this.automaticLinking = settings.getAutomaticLinking();
	}
	
	/**
	 * Create a new markup filter chain and initialize with all default
	 * filters required.
	 * 
	 * @param tagList
	 * @return a preconfigured markup filter chain
	 */
	private final IMarkupFilter newFilterChain(final List tagList)
	{
        // Chain together all the different markup filters and configure them
        this.detectWicketComponents = new WicketTagIdentifier(this.wicketNamespace, xmlParser);
        IMarkupFilter filter = this.detectWicketComponents;

        filter = new TagTypeHandler(filter);
        filter = new HtmlHandler(filter);
        filter = new WicketParamTagHandler(this.stripWicketTag, filter);
        filter = new WicketRemoveTagHandler(filter);
        filter = new WicketLinkTagHandler(this.automaticLinking, filter); 
        
        // Provided the wicket component requesting the markup is known ...
        if ((this.resource != null) && (this.resource.getContainerInfo() != null))
        {
        	if (WicketMessageTagHandler.enable)
        	{
        		filter = new WicketMessageTagHandler(this.resource.getContainerInfo(), filter);
        	}
        	
	        filter = new BodyOnLoadHandler(filter);
	
	        // Pages require additional handlers
	        if (Page.class.isAssignableFrom(this.resource.getContainerInfo().getContainerClass()))
	        {
	            filter = new HtmlHeaderSectionHandler(tagList, filter);
	        }
        }
        
        return filter;
	}

	/**
	 * By default don't do anything. Subclasses may append additional markup
	 * filter if required.
	 */
	protected void initFilterChain()
	{
	}

	/**
	 * Append a new filter to the list of already pre-configured markup
	 * filters.
	 * 
	 * @param filter The filter to be appended
	 */
	public final void appendMarkupFilter(final IMarkupFilter filter)
	{
	    filter.setParent(markupFilterChain);
	    markupFilterChain = filter;
	}
	
    /**
     * Return the encoding used while reading the markup file.
     * You need to call @see #read(Resource) first to initialise
     * the value.
     *
     * @return if null, than JVM default is used.
     */
    public final String getEncoding()
    {
        return xmlParser.getEncoding();
    }

	/**
	 * Return the XML declaration string, in case if found in the
	 * markup.
	 * 
	 * @return Null, if not found.
	 */
    public final String getXmlDeclaration()
    {
        return xmlParser.getXmlDeclaration();
    }
    
    /**
     * Reads and parses markup from a file.
     * @param resource The file
     * @return The markup
     * @throws ParseException
     * @throws IOException
     * @throws ResourceStreamNotFoundException
     */
    final Markup readAndParse(final MarkupResourceStream resource) throws ParseException, IOException,
            ResourceStreamNotFoundException
    {
    	this.resource = resource;
        xmlParser.parse(resource);
        return new Markup(resource, parseMarkup(), 
                getXmlDeclaration(), getEncoding(), this.wicketNamespace);
    }

    /**
     * Parse the markup.
     * @param string The markup
     * @return The markup
     * @throws ParseException
     * @throws IOException
     * @throws ResourceStreamNotFoundException
     */
    final Markup parse(final String string) throws ParseException, IOException,
    	ResourceStreamNotFoundException
    {
    	this.resource = null;
        xmlParser.parse(string);
        return new Markup(null, parseMarkup(), getXmlDeclaration(), getEncoding(), 
                this.wicketNamespace);
    }

    /**
     * Scans the given markup string and extracts balancing tags.
     * @return An immutable list of immutable MarkupElement elements
     * @throws ParseException Thrown if markup is malformed or tags don't balance
     */
    private List parseMarkup() throws ParseException
    {
        final List autoAddList = new ArrayList();
        
        this.markupFilterChain = newFilterChain(autoAddList);
        initFilterChain();
        
        // List to return
        final List list = new ArrayList();

        try
        {
	        // Loop through tags
	        for (ComponentTag tag; null != (tag = (ComponentTag)markupFilterChain.nextTag());)
	        {
	            boolean add = (tag.getId() != null);
	            if (!add && tag.getXmlTag().isClose())
	            {
	                add = ((tag.getOpenTag() != null) && (tag.getOpenTag().getId() != null));
	            }
	            
	            // Determine wicket namespace: <html xmlns:wicket="http://wicket.sourceforge.net">
	            RawMarkup replaceTag = null;
				if (tag.isOpen() && "html".equals(tag.getName().toLowerCase()))
				{
					// if add already true, do not make it false
				    add |= determineWicketNamespace(tag);
				    
				    // If add and tag has no wicket:id, than
				    if ((add == true) && (tag.getId() == null))
				    {
				    	// Replace the current tag
				    	replaceTag = new RawMarkup(tag.toString());
				    }
				}
	
	            // Add tag to list?
	            if (add || (autoAddList.size() > 0) || tag.isModified())
	            {
	                final CharSequence text =
	                    	xmlParser.getInputFromPositionMarker(tag.getPos());
	
	                // Add text from last position to tag position
	                if (text.length() > 0)
	                {
	                    String rawMarkup = text.toString();
	
	                    if (stripComments)
	                    {
	                        rawMarkup = rawMarkup.replaceAll("<!--(.|\n|\r)*?-->", "");
	                    }
	
	                    if (compressWhitespace)
	                    {
	                        rawMarkup = rawMarkup.replaceAll("[ \\t]+", " ");
	                        rawMarkup = rawMarkup.replaceAll("( ?[\\r\\n] ?)+", "\n");
	                    }
	
	                    list.add(new RawMarkup(rawMarkup));
	                }
	
	                if ((add == false) && (autoAddList.size() > 0))
	                {
	                    xmlParser.setPositionMarker(tag.getPos());
	                }
	
	                list.addAll(autoAddList);
	                autoAddList.clear();
	            }
	            
	            if (add)
	            {
	                // Add to list unless preview component tag remover flagged as removed
	                if (!WicketRemoveTagHandler.IGNORE.equals(tag.getId()))
	                {
	                	if (replaceTag != null)
	                	{
	                		list.add(replaceTag);
	                	}
	                	else
	                	{
	                		list.add(tag);
	                	}
	                }
	                
	                xmlParser.setPositionMarker();
	            }
	            else if (tag.isModified())
	            {
                    list.add(new RawMarkup(tag.toString()));
	                xmlParser.setPositionMarker();
	            }
	        }
        }
        catch (ParseException ex)
        {
            // Add remaining input string
            final CharSequence text = xmlParser.getInputFromPositionMarker(-1);
            if (text.length() > 0)
            {
                list.add(new RawMarkup(text));
            }
            
        	Markup markup = new Markup(this.resource, list, getXmlDeclaration(), getEncoding(), 
                    this.wicketNamespace);
        	
        	MarkupStream markupStream = new MarkupStream(markup); 
        	markupStream.setCurrentIndex(list.size() - 1);
        	throw new MarkupException(markupStream, ex.getMessage());
        }
        
        // Add tail?
        final CharSequence text = xmlParser.getInputFromPositionMarker(-1);
        if (text.length() > 0)
        {
            list.add(new RawMarkup(text));
        }
        
        // Make all tags immutable. Note: We can not make tag immutable 
        // just prior to adding to the list, because <wicket:param> 
        // needs to modify its preceding tag (add the attributes). And 
        // because WicketParamTagHandler and ComponentTag are not in the 
        // same package, WicketParamTagHandler is not able to modify the
        // default protected variables of ComponentTag, either.
        for (int i=0; i < list.size(); i++)
        {
            MarkupElement elem = (MarkupElement) list.get(i);
            if (elem instanceof ComponentTag)
            {
                ((ComponentTag)elem).makeImmutable();
            }
        }
        
        // Return immutable list of all MarkupElements
        return Collections.unmodifiableList(list);
    }

    /**
     * Determine wicket namespace from xmlns:wicket or
     * xmlns:wicket="http://wicket.sourceforge.net"
     * 
     * @param tag
     * @return true, if tag has been modified
     */
    private boolean determineWicketNamespace(final ComponentTag tag)
    {
    	String attrValue = null;
        final ValueMap attributes = tag.getAttributes();
        final Iterator it = attributes.keySet().iterator();
        while (it.hasNext())
        {
            final String attributeName = (String)it.next();
            if (attributeName.startsWith("xmlns:"))
            {
                final String xmlnsUrl = attributes.getString(attributeName);
                if ((xmlnsUrl == null) || (xmlnsUrl.trim().length() == 0) ||
                        xmlnsUrl.toLowerCase().startsWith("http://wicket.sourceforge.net"))
                {
                    this.wicketNamespace = attributeName.substring(6);
                    this.detectWicketComponents.setWicketNamespace(this.wicketNamespace);
                    attrValue = attributeName;
                }
            }
        }
        
        // Note: <html ...> are usually no wicket tags and thus treated as raw 
        // markup and thus removing xmlns:wicket from markup does not have any 
        // effect. The solution approach does not work.
        if ((attrValue != null) && stripWicketTag)
        {
        	attributes.remove(attrValue);
        	return true;
        }
        
        return false;
    }
}
