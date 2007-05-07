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
package org.apache.wicket.examples.velocity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.examples.WicketExamplePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.resource.IStringResourceStream;
import org.apache.wicket.util.resource.PackageResourceStream;
import org.apache.wicket.velocity.markup.html.VelocityPanel;

/**
 * Template example page.
 * 
 * @author Eelco Hillenius
 */
public class DynamicPage extends WicketExamplePage
{
	/** the current template contents. */
	private PackageResourceStream template = new PackageResourceStream(DynamicPage.class, "fields.vm");
	/** context to be used by the template. */
	private final Model templateContext;

	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public DynamicPage(final PageParameters parameters)
	{
		Map<String, List<Field>> map = new HashMap<String, List<Field>>();
		List<Field> fields = VelocityTemplateApplication.getFields();
		map.put("fields", fields);
		templateContext = Model.valueOf(map);

		VelocityPanel panel;
		add(panel = new VelocityPanel("templatePanel", templateContext) {
			@Override
			protected IStringResourceStream getTemplateResource()
			{
				return template;
			}
			@Override
			protected boolean parseGeneratedMarkup()
			{
				return true;
			}
		});
		for (Field field : fields) {
			panel.add(new TextField(field.getFieldName()));
			panel.add(new Label("label_"+field.getFieldName(), new ResourceModel(field.getFieldName())));
		}
	}
}