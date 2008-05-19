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
package org.apache.wicket.examples.stateless;

import org.apache.wicket.Page;
import org.apache.wicket.examples.WicketExampleApplication;
import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;

/**
 * Application class for the stateless application.
 * 
 * @author Eelco Hillenius
 */
public class StatelessApplication extends WicketExampleApplication
{
	/**
	 * Constructor.
	 */
	public StatelessApplication()
	{
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page<?>> getHomePage()
	{
		return Index.class;
	}

	/**
	 * @see org.apache.wicket.examples.WicketExampleApplication#init()
	 */
	@Override
	protected void init()
	{
		mount(new QueryStringUrlCodingStrategy("/statefull", StatefulPage.class));
		mount(new QueryStringUrlCodingStrategy("/query", StatelessPage1.class));
		mount(new MixedParamUrlCodingStrategy("/mixed", StatelessPage2.class, new String[] {
				"param1", "param2" }));
		mount(new IndexedParamUrlCodingStrategy("/indexed", StatelessPage3.class));
		// mount("/public", PackageName.forClass(StatelessApplication.class));
		// mountBookmarkablePage("foo", StatelessPage.class);
	}
}
