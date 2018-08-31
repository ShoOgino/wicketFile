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
package org.apache.wicket.markup;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 */
public class MarkupInheritanceBaseBase_11 extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
    MarkupInheritanceBaseBase_11()
	{
		WebMarkupContainer css = new WebMarkupContainer("css");
		css.add(AttributeModifier.replace("src", Model.of("myStyle.css")));
		add(css);

		add(new Label("label1", "base label 1"));
		add(new Label("label2", "base label 2"));
	}
}
