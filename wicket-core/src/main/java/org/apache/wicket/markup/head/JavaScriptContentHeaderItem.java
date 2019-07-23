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

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.value.AttributeMap;

/**
 * {@link HeaderItem} for internal (embedded in the header) javascript content.
 * 
 * @author papegaaij
 */
public class JavaScriptContentHeaderItem extends JavaScriptHeaderItem
{
	private final CharSequence javaScript;

	/**
	 * Creates a new {@code JavaScriptContentHeaderItem}.
	 * 
	 * @param javaScript
	 *            javascript content to be rendered.
	 * @param id
	 *            unique id for the javascript element. This can be null, however in that case the
	 *            ajax header contribution can't detect duplicate script fragments.
	 * @param condition
	 *            the condition to use for Internet Explorer conditional comments. E.g. "IE 7".
	 */
	public JavaScriptContentHeaderItem(CharSequence javaScript, String id, String condition)
	{
		super(condition);
		this.javaScript = javaScript;
		setId(id);
	}

	/**
	 * @return javascript content to be rendered.
	 */
	public CharSequence getJavaScript()
	{
		return javaScript;
	}

	@Override
	public void render(Response response)
	{
		boolean hasCondition = Strings.isEmpty(getCondition()) == false;
		if (hasCondition)
		{
			response.write("<!--[if ");
			response.write(getCondition());
			response.write("]>");
		}
		AttributeMap attributes = new AttributeMap();
		attributes.putAttribute(JavaScriptUtils.ATTR_TYPE, "text/javascript");
		attributes.putAttribute(JavaScriptUtils.ATTR_ID, getId());
		attributes.putAttribute(JavaScriptUtils.ATTR_CSP_NONCE, getNonce());
		JavaScriptUtils.writeInlineScript(response, getJavaScript(), attributes);

		if (hasCondition)
		{
			response.write("<![endif]-->\n");
		}
	}

	@Override
	public Iterable<?> getRenderTokens()
	{
		if (Strings.isEmpty(getId()))
			return Collections.singletonList(getJavaScript());
		return Arrays.asList(getId(), getJavaScript());
	}

	@Override
	public String toString()
	{
		return "JavaScriptHeaderItem(" + getJavaScript() + ")";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		JavaScriptContentHeaderItem that = (JavaScriptContentHeaderItem) o;
		return Objects.equals(javaScript, that.javaScript);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), javaScript);
	}
}
