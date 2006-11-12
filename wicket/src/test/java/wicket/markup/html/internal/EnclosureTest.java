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
package wicket.markup.html.internal;

import wicket.WicketTestCase;
import wicket.markup.MarkupParser;
import wicket.markup.MarkupParserFactory;
import wicket.markup.MarkupResourceStream;
import wicket.markup.parser.filter.EnclosureHandler;
import wicket.markup.resolver.EnclosureResolver;
import wicket.protocol.http.WebApplication;
import wicket.resource.DummyApplication;
import wicket.util.tester.WicketTester;

/**
 * 
 * @author Juergen Donnerstag
 */
public class EnclosureTest extends WicketTestCase
{
	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public EnclosureTest(final String name)
	{
		super(name);
	}

	/**
	 * 
	 * @see wicket.WicketTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		WebApplication app = new DummyApplication()
		{
			@Override
			protected void init() 
			{
				super.init();
				getPageSettings().addComponentResolver(new EnclosureResolver());
			}
		};

		tester = new WicketTester(app);
		this.tester.getApplication().getMarkupSettings().setMarkupParserFactory(new MarkupParserFactory()
		{
			@Override
			public MarkupParser newMarkupParser(MarkupResourceStream resource)
			{
				MarkupParser parser = super.newMarkupParser(resource);
				// register the additional EnclosureHandler
				parser.registerMarkupFilter(new EnclosureHandler());
				return parser;
			}
		});
	}
	
	/**
	 * @throws Exception
	 */
	public void testRenderHomePage() throws Exception
	{
		executeTest(EnclosurePage_1.class, "EnclosurePageExpectedResult_1.html");
	}
}
