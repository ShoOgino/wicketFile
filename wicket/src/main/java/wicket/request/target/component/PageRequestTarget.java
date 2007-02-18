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
package wicket.request.target.component;

import wicket.Page;
import wicket.RequestCycle;

/**
 * Default implementation of {@link IPageRequestTarget}. Target that denotes a
 * page instance.
 * 
 * @author Eelco Hillenius
 */
public class PageRequestTarget implements IPageRequestTarget
			
{
	/** the page instance. */
	private final Page page;

	/**
	 * Construct.
	 * 
	 * @param page
	 *            the page instance
	 */
	public PageRequestTarget(Page page)
	{
		if (page == null)
		{
			throw new IllegalArgumentException("Argument page must be not null");
		}

		this.page = page;
	}

	/**
	 * @see wicket.IRequestTarget#respond(wicket.RequestCycle)
	 */
	public void respond(RequestCycle requestCycle)
	{
		// Should page be redirected to?
		if (requestCycle.getRedirect())
		{
			// Redirect to the page
			requestCycle.redirectTo(page);
		}
		else
		{
			requestCycle.setUpdateSession(true);

			// Let page render itself
			page.renderPage();
		}
	}

	/**
	 * @see wicket.request.target.component.IPageRequestTarget#getPage()
	 */
	public final Page getPage()
	{
		return page;
	}

	/**
	 * @see wicket.IRequestTarget#detach(wicket.RequestCycle)
	 */
	public void detach(RequestCycle requestCycle)
	{
		page.internalDetach();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof PageRequestTarget)
		{
			PageRequestTarget that = (PageRequestTarget)obj;
			return page.equals(that.page);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		int result = "PageRequestTarget".hashCode();
		result += page.hashCode();
		return 17 * result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[PageRequestTarget@" + hashCode() + " page=" + page + "]";
	}
}