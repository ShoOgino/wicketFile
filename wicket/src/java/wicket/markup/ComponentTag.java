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
package wicket.markup;

import java.util.Map;

import wicket.markup.parser.XmlTag;
import wicket.markup.parser.XmlTag.Type;
import wicket.markup.parser.filter.HtmlHandler;
import wicket.util.string.StringValue;
import wicket.util.value.ValueMap;

/**
 * A subclass of MarkupElement which represents a "significant" markup tag, such
 * as a component open tag. Insignificant markup tags (those which are
 * merely concerned with markup formatting operations and do not denote
 * components or component nesting) are coalesced into instances of RawMarkup
 * (also a subclass of MarkupElement).
 * 
 * @author Jonathan Locke
 */
public class ComponentTag extends MarkupElement
{
    /**
     * Standard component name attribute always available for components
     * regardless of user ApplicationSettings for componentName attribute; 
     * value == 'wicket'.
     */
    public static final String DEFAULT_COMPONENT_NAME_ATTRIBUTE = "wicket";

    /** The component's name. Wicket supports several means to identify
     * Wicket components. E.g. wicket="name", id="wicket-name" */
    private String componentName;

    /** The underlying xml tag */
    protected final XmlTag xmlTag;
    
    /** Assuming this is a open (or open-close) tag, 'closes' refers to
     * the ComponentTag which closes it.
     */
    protected ComponentTag closes;
    
    /** True if a href attribute is available and autolinking is on */
    private boolean autolink = false;

    /**
     * Construct.
     * @param The underlying xml tag
     */
    public ComponentTag(final XmlTag tag)
    {
        super();
        xmlTag = tag;
    }

    /**
     * Get the tag's component name 
     * 
     * @return The component name attribute of this tag
     */
    public String getComponentName()
    {
        return componentName;
    }

    /**
     * Set the component's name. The value is usually taken from
     * the tag's id attribute, e.g. id="wicket-name" or wicket="name".
     * @param name The component's name assigned to the tag.
     */
    public void setComponentName(final String name)
    {
        this.componentName = name;
    }

    /**
     * @return Returns the underlying xml tag.
     */
    public XmlTag getXmlTag()
    {
        return xmlTag;
    }

    /**
     * Gets whether this tag closes the provided open tag.
     * @param open The open tag
     * @return True if this tag closes the given open tag
     */
    public final boolean closes(final MarkupElement open)
    {
        if (open instanceof ComponentTag)
        {
            return (closes == open) 
            		|| getXmlTag().closes(((ComponentTag)open).getXmlTag());
        }
        
        return false;
    }
    
    /**
     * Assuming this is a close tag, assign it's corresponding open tag.
     * 
     * @param tag the open-tag
     * @throws RuntimeException if 'this' is not a close tag
     */
    public void setOpenTag(final ComponentTag tag)
    {
        this.closes = tag;
        getXmlTag().setOpenTag(tag.getXmlTag());
    }

    /**
     * If set, return the corresponding open tag (ComponentTag).
     * @return The corresponding open tag
     */
    public ComponentTag getOpenTag()
    {
        return closes;
    }

    /**
     * Gets this tag if it is already mutable, or a mutable copy of this tag if
     * it is immutable.
     * 
     * @return This tag if it is already mutable, or a mutable copy of this tag
     *         if it is immutable.
     */
    public ComponentTag mutable()
    {
        if (xmlTag.isMutable())
        {
            return this;
        } 
        else
        {
            final ComponentTag tag = new ComponentTag(xmlTag.mutable());
            tag.componentName = componentName;
            return tag;
        }
    }

    /**
     * Makes this tag object immutable by making the attribute map unmodifiable. Immutable
     * tags cannot be made mutable again. They can only be copied into new mutable tag
     * objects.
     */
    public void makeImmutable()
    {
        xmlTag.makeImmutable();
    }

    /**
     * Clears component name attribute from this tag if the tag is mutable.
     * 
     * @param componentNameAttribute
     *            The attribute name to remove
     */
    public void removeComponentName(final String componentNameAttribute)
    {
        if (xmlTag.isMutable())
        {
            this.componentName = null;
            xmlTag.remove(componentNameAttribute);
            xmlTag.remove(DEFAULT_COMPONENT_NAME_ATTRIBUTE);
        } else
        {
            throw new UnsupportedOperationException(
                    "Attempt to clear component name attribute of immutable tag");
        }
    }

    /**
     * Converts this object to a string representation.
     * 
     * @return String version of this object
     */
    public String toString()
    {
        return xmlTag.toString();
    }

    /**
     * Converts this object to a string representation including
     * useful information for debugging
     * 
     * @return String version of this object
     */
    public String toUserDebugString()
    {
        return xmlTag.toUserDebugString();
    }

    /**
     * @see wicket.util.xml.XmlTag#getAttributes()
     * @return The tag#s attributes
     */
    public ValueMap getAttributes()
    {
        return xmlTag.getAttributes();
    }

    /**
     * @see wicket.util.xml.XmlTag#put(String, boolean)
     * @param key The key
     * @param value The value
     */
    public void put(String key, boolean value)
    {
        xmlTag.put(key, value);
    }

    /**
     * @see wicket.util.xml.XmlTag#put(String, int)
     * @param key The key
     * @param value The value
     */
    public void put(String key, int value)
    {
        xmlTag.put(key, value);
    }

    /**
     * @see wicket.util.xml.XmlTag#put(String, String)
     * @param key The key
     * @param value The value
     */
    public void put(String key, String value)
    {
        xmlTag.put(key, value);
    }

    /**
     * @see wicket.util.xml.XmlTag#put(String, StringValue)
     * @param key The key
     * @param value The value
     */
    public void put(String key, StringValue value)
    {
        xmlTag.put(key, value);
    }

    /**
     * @see wicket.util.xml.XmlTag#putAll(Map)
     * @param map a key/value map
     */
    public void putAll(final Map map)
    {
        xmlTag.putAll(map);
    }

    /**
     * @see wicket.util.xml.XmlTag#getString(String)
     * @param key The key
     * @return The string value
     */
    public String getString(String key)
    {
        return xmlTag.getString(key);
    }

    /**
     * @see wicket.util.xml.XmlTag#getName()
     * @return The tag's name
     */
    public String getName()
    {
        return xmlTag.getName();
    }

    /**
     * @see wicket.util.xml.XmlTag#getNamespace()
     * @return The tag's namespace
     */
    public String getNamespace()
    {
        return xmlTag.getNamespace();
    }

    /**
     * @see wicket.util.xml.XmlTag#remove(String)
     * @param key The key to remove
     */
    public void remove(String key)
    {
        xmlTag.remove(key);
    }

    /**
     * @see wicket.util.xml.XmlTag#getType()
     * @return the tag type (OPEN, CLOSE or OPEN_CLOSE).
     */
    public Type getType()
    {
        return xmlTag.getType();
    }

    /**
     * @see wicket.util.xml.XmlTag#isOpen()
     * @return True if this tag is an open tag
     */
    public boolean isOpen()
    {
        return xmlTag.isOpen();
    }

    /**
     * @see wicket.util.xml.XmlTag#isOpen(String)
     * @param componentName Required component name attribute
     * @return True if this tag is an open tag with the given component name
     */
    public boolean isOpen(String componentName)
    {
        return xmlTag.isOpen(componentName);
    }

    /**
     * @see wicket.util.xml.XmlTag#isOpenClose()
     * @return True if this tag is an open and a close tag
     */
    public boolean isOpenClose()
    {
        return xmlTag.isOpenClose();
    }

    /**
     * @see wicket.util.xml.XmlTag#isOpenClose(String)
     * @param componentName Required component name attribute
     * @return True if this tag is an openclose tag with the given component name
     */
    public boolean isOpenClose(String componentName)
    {
        return xmlTag.isOpenClose(componentName);
    }

    /**
     * @see wicket.util.xml.XmlTag#setType(Type)
     * @param type The new type
     */
    public void setType(Type type)
    {
        xmlTag.setType(type);
    }
    
    /**
     * True if autolink is enabled and the tag contains a href attribute.
     * @return True, if the href contained should automatically be converted
     */
    public boolean isAutolinkEnabled()
    {
        return this.autolink;
    }
    
    /**
     * If autolink is set to true, href attributes will automatically be
     * converted into Wicket bookmarkable URLs.
     * @param autolink enable/disable automatic href conversion
     */
    public void enableAutolink(final boolean autolink)
    {
        this.autolink = autolink;
    }
    
    /**
     * @see wicket.util.xml.XmlTag#getNameChanged()
     * @return Returns true if the name of this component tag was changed
     */
    public boolean getNameChanged()
    {
        return xmlTag.getNameChanged();
    }
    
    /**
     * @see wicket.util.xml.XmlTag#setName(String)
     * @param name New tag name
     */
    public void setName(String name)
    {
        xmlTag.setName(name);
    }
    
    /**
     * @see wicket.util.xml.XmlTag#isClose()
     * @return True if this tag is a close tag
     */
    public boolean isClose()
    {
        return xmlTag.isClose();
    }
    
    /**
     * @see wicket.util.xml.XmlTag#getPos()
	 * @return Tag location (index in input string)
     */
    public int getPos()
    {
        return xmlTag.getPos();
    }
    
	/**
	 * Gets whether this tag does not require a closing tag.
	 * 
	 * @return True if this tag does not require a closing tag
	 */
    public boolean requiresCloseTag()
    {
        return HtmlHandler.requiresCloseTag(this.getName());
    }
    
	/**
	 * Gets the length of the tag in characters.
	 * 
	 * @return The tag's length
	 */
    public int getLength()
    {
        return xmlTag.getLength();
    }
}

// /////////////////////////////// End of File /////////////////////////////////
