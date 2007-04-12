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
package org.apache.wicket.markup.transformer;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;

/**
 * An IBehavior which can be added to any component except ListView. It allows
 * to post-process (XSLT) the markup generated by the component. The *.xsl
 * resource must be located in the same path as the nearest parent with an
 * associated markup and must have a filename equal to the component's id.
 * <p>
 * The containers tag will be the root element of the xml data applied for
 * transformation to ensure the xml data are well formed (single root element).
 * In addition the attribute
 * <code>xmlns:wicket="http://wicket.apache.org"</code> is added to
 * the root element to allow the XSL processor to handle the org.apache.wicket
 * namespace.
 * <p>
 * The reason why the transformer can not be used to XSLT the ListViews output
 * is because of the ListViews markup being reused for each ListItem. Please use
 * a XsltOutputTransformerContainer instead. Note: if the ListView is used to
 * print a list of &lt;tr&gt; tags, than the transformer container must enclose
 * the &lt;table&gt; tag as well to be HTML compliant.
 * 
 * @see org.apache.wicket.markup.transformer.AbstractOutputTransformerContainer
 * @see org.apache.wicket.markup.transformer.XsltOutputTransformerContainer
 * 
 * @author Juergen Donnerstag
 */
public class XsltTransformerBehavior extends AbstractTransformerBehavior
{
	private static final long serialVersionUID = 1L;

	/** An optional xsl file path */
	private final String xslFile;

	/**
	 * Construct.
	 */
	public XsltTransformerBehavior()
	{
		this.xslFile = null;
	}

	/**
	 * @param xslFilePath
	 * @see XsltTransformer#XsltTransformer(String)
	 */
	public XsltTransformerBehavior(final String xslFilePath)
	{
		this.xslFile = xslFilePath;
	}

	/**
	 * @see org.apache.wicket.behavior.IBehavior#onComponentTag(org.apache.wicket.Component,
	 *      org.apache.wicket.markup.ComponentTag)
	 */
	public void onComponentTag(final Component component, final ComponentTag tag)
	{
		tag.put("xmlns:wicket", "http://wicket.apache.org");

		// Make the XSLT processor happy and allow it to handle the
		// org.apache.wicket
		// tags and attributes that are in the wicket namespace
		super.onComponentTag(component, tag);
	}

	/**
	 * @see org.apache.wicket.markup.transformer.ITransformer#transform(org.apache.wicket.Component,
	 *      java.lang.String)
	 */
	public CharSequence transform(final Component component, final String output) throws Exception
	{
		return new XsltTransformer(this.xslFile).transform(component, output);
	}
}
