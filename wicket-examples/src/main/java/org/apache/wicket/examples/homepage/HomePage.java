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
package org.apache.wicket.examples.homepage;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * @author mocleiri
 */
public class HomePage extends WebPage
{

	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public HomePage()
	{
		super();

		add(new Label("version", new IModel<String>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject()
			{
				/*
				 * Read the specification version from the wicket-core MANIFEST.MF file.
				 */
				Package p = Application.class.getPackage();

				String version = p.getSpecificationVersion();

				if (version == null || version.length() == 0)
				{
					return "Missing Version";
				}
				else
				{
					return version;
				}
			}
		}));
	}

}
