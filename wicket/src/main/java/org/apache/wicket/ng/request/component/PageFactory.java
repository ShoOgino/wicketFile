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
package org.apache.wicket.ng.request.component;

import org.apache.wicket.IPageFactory;
import org.apache.wicket.Page;
import org.apache.wicket.ng.Session;
import org.apache.wicket.ng.WicketRuntimeException;


/**
 * A factory class that creates Pages. A Page can be created by Class, with or without a
 * PageParameters argument to pass to the Page's constructor.
 * <p>
 * IMPORTANT NOTE: Implementations must let subclasses of
 * {@link org.apache.wicket.AbstractRestartResponseException} thrown from the constructing page's
 * constructor bubble up.
 * 
 * @see org.apache.wicket.settings.ISessionSettings#setPageFactory(IPageFactory)
 * @see Session#getPageFactory()
 * @see Session#getPageFactory(Page)
 * 
 * @author Juergen Donnerstag
 * @author Jonathan Locke
 */
public interface PageFactory
{
	/**
	 * Creates a new page using a page class.
	 * 
	 * @param <C>
	 * 
	 * @param pageClass
	 *            The page class to instantiate
	 * @return The page
	 * @throws WicketRuntimeException
	 *             Thrown if the page cannot be constructed
	 */
	<C extends RequestablePage> RequestablePage newPage(final Class<C> pageClass);

	/**
	 * Creates a new Page, passing PageParameters to the Page constructor if such a constructor
	 * exists. If no such constructor exists and the parameters argument is null or empty, then any
	 * available default constructor will be used.
	 * 
	 * @param <C>
	 * 
	 * @param pageClass
	 *            The class of Page to create
	 * @param parameters
	 *            Any parameters to pass to the Page's constructor
	 * @return The new page
	 * @throws WicketRuntimeException
	 *             Thrown if the page cannot be constructed
	 */
	<C extends RequestablePage> RequestablePage newPage(final Class<C> pageClass,
		final PageParameters parameters);
}
