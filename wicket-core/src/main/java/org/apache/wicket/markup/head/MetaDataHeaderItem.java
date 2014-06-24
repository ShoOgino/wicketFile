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
package org.apache.wicket.markup.head;

import java.util.Collections;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.value.ValueMap;

/**
 * {@link HeaderItem} for meta informations such as &lt;meta&gt; tags or 
 * canonical &lt;link&gt;
 * 
 * @author andrea del bene
 *
 */
public class MetaDataHeaderItem extends HeaderItem
{

	private final Map<String, Object> tagAttributes = new ValueMap();
	private final String tagName;

	public static final String META_TAG = "meta";
	public static final String LINK_TAG = "link";
	public static final String CANONICAL_LINK = "canonical";

	/**
	 * Build a new {@link MetaDataHeaderItem} having {@code tagName} as tag.
	 * 
	 * @param tagName
	 * 		the name of the tag
	 */
	public MetaDataHeaderItem(String tagName)
	{
		this.tagName = tagName;
	}

	/**
	 * Add a tag attribute to the item. If the attribute value is a {@link IModel}, 
	 * the object wrapped inside the model is used as actual value.
	 * 
	 * @param attributeName
	 * 		the attribute name
	 * @param attributeValue
	 * 		the attribute value
	 * @return
	 * 		The current item.
	 */
	public MetaDataHeaderItem addTagAttribute(String attributeName, Object attributeValue)
	{
		tagAttributes.put(attributeName, attributeValue);
		return this;
	}

	/**
	 * Generate the string representation for the current item.
	 * 
	 * @return
	 * 		The string representation for the current item.
	 */
	public String generateString()
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append('<').append(tagName);

		for (Map.Entry<String, Object> entry : tagAttributes.entrySet())
		{
			Object value = entry.getValue();

			if (value instanceof IModel)
			{
				value = ((IModel<?>)value).getObject();
			}

			if (value != null)
			{
				buffer.append(' ')
					.append(entry.getKey())
					.append('=')
					.append('"')
					.append(value)
					.append('"');
			}
		}

		buffer.append(" />\n");

		return buffer.toString();
	}

	@Override
	public Iterable<?> getRenderTokens()
	{
		return Collections.singletonList(generateString());
	}

	@Override
	public void render(Response response)
	{
		response.write(generateString());
	}

	/**
	 * Factory method to create &lt;meta&gt; tag.
	 * 
	 * @param name
	 * 		the 'name' attribute of the tag
	 * @param content
	 * 		the 'content' attribute of the tag
	 * @return
	 * 		A new {@link MetaDataHeaderItem}
	 */
	public static MetaDataHeaderItem forMetaTag(String name, String content)
	{
		return forMetaTag(Model.<String> of(name), Model.<String> of(content));
	}

	/**
	 * Factory method to create &lt;meta&gt; tag.
	 * 
	 * @param name
	 * 		the 'name' attribute of the tag as String model
	 * @param content
	 * 		the 'content' attribute of the tag as String model
	 * @return
	 * 		A new {@link MetaDataHeaderItem}
	 */
	public static MetaDataHeaderItem forMetaTag(IModel<String> name, IModel<String> content)
	{
		MetaDataHeaderItem headerItem = new MetaDataHeaderItem(META_TAG);

		headerItem.addTagAttribute("name", name);
		headerItem.addTagAttribute("content", content);

		return headerItem;
	}

	/**
	 * Factory method to create &lt;link&gt; tag.
	 *  
	 * @param rel
	 * 		the 'rel' attribute of the tag
	 * @param href
	 * 		the 'href' attribute of the tag
	 * @return
	 * 		A new {@link MetaDataHeaderItem}
	 */
	public static MetaDataHeaderItem forLinkTag(String rel, String href)
	{
		return forLinkTag(Model.<String> of(rel), Model.<String> of(href));
	}
	
	/**
	 * Factory method to create &lt;link&gt; tag.
	 *  
	 * @param rel
	 * 		the 'rel' attribute of the tag as String model
	 * @param href
	 * 		the 'href' attribute of the tag as String model
	 * @return
	 * 		A new {@link MetaDataHeaderItem}
	 */
	public static MetaDataHeaderItem forLinkTag(IModel<String> rel, IModel<String> href)
	{
		MetaDataHeaderItem headerItem = new MetaDataHeaderItem(LINK_TAG);

		headerItem.addTagAttribute("rel", rel);
		headerItem.addTagAttribute("href", href);

		return headerItem;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof MetaDataHeaderItem)
			return ((MetaDataHeaderItem)obj).generateString().equals(generateString());
		return false;
	}
}
