/*
 * $Id: PagingNavigationIncrementLink.java,v 1.3 2005/02/17 06:15:27
 * jonathanlocke Exp $ $Revision$ $Date: 2006-02-25 16:24:23 -0800 (Sat,
 * 25 Feb 2006) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.ajax.markup.html.navigation.paging;

import wicket.MarkupContainer;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.IAjaxLink;
import wicket.markup.html.navigation.paging.IPageable;
import wicket.markup.html.navigation.paging.PagingNavigationIncrementLink;

/**
 * An incremental Ajaxian link to a page of a PageableListView. Assuming your
 * list view navigation looks like
 * 
 * <pre>
 *                   
 *                  	 [first / &lt;&lt; / &lt;] 1 | 2 | 3 [&gt; / &gt;&gt; /last]
 *                  	
 * </pre>
 * 
 * <p>
 * and "&lt;" meaning the previous and "&lt;&lt;" goto the "current page - 5",
 * than it is this kind of incremental page links which can easily be created.
 * 
 * This link will update the pageable and itself or the navigator the link is
 * part of using Ajax techniques, or perform a full refresh when ajax is not
 * available.
 * 
 * @since 1.2
 * 
 * @author Martijn Dashorst
 */
public class AjaxPagingNavigationIncrementLink extends PagingNavigationIncrementLink
		implements
			IAjaxLink
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this component
	 * 
	 * @param id
	 *            See Component
	 * @param pageable
	 *            The pageable component the page links are referring to
	 * @param increment
	 *            increment by
	 */
	public AjaxPagingNavigationIncrementLink(MarkupContainer parent, final String id,
			final IPageable pageable, final int increment)
	{
		super(parent, id, pageable, increment);
		add(new AjaxPagingNavigationBehavior(this, pageable, "onclick"));

		setOutputMarkupId(true);
	}

	/**
	 * Returns the javascript event handler for this component. This function is
	 * used to decorate the generated javascript handler.
	 * <p>
	 * NOTE: It is recommended that you only prepend additional javascript to
	 * the default handler because the default handler uses the return func()
	 * format so any appended javascript will not be evaluated by default.
	 * 
	 * @param defaultHandler
	 *            default javascript event handler generated by this link
	 * @return javascript event handler for this link
	 */
	protected String getEventHandler(String defaultHandler)
	{
		return defaultHandler;
	}


	/**
	 * Fallback event listener, will redisplay the current page.
	 * 
	 * @see wicket.markup.html.link.Link#onClick()
	 */
	@Override
	public void onClick()
	{
		onClick(null);

		// We do not need to redirect
		setRedirect(false);

		// Return the the current page.
		setResponsePage(getPage());
	}

	/**
	 * Performs the actual action of this component, performing a non-ajax
	 * fallback when there was no AjaxRequestTarget available.
	 * 
	 * @param target
	 *            the request target, when <code>null</code>, a full page
	 *            refresh will be generated
	 */
	public void onClick(AjaxRequestTarget target)
	{
		// Tell the PageableListView which page to print next
		pageable.setCurrentPage(getPageNumber());
	}
}
