/*
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
package wicket.protocol.http.portlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Page;
import wicket.PageMap;
import wicket.PageParameters;
import wicket.model.IModel;

/**
 * Base class for portlet pages.
 * 
 * @author Janne Hietam&auml;ki
 * 
 * @see WebPage
 * @see Page
 */
public class PortletPage extends Page
{
	private static final long serialVersionUID = 1L;

	/** log. */
	private static final Log log = LogFactory.getLog(PortletPage.class);

	/**
	 * Constructor. Having this constructor public means that your page is
	 * 'bookmarkable' and hence can be called/ created from anywhere.
	 */
	protected PortletPage()
	{
	}

	/**
	 * @see Page#Page(IModel)
	 */
	protected PortletPage(final IModel model)
	{
		super(model);
	}

	/**
	 * @see Page#Page(PageMap)
	 */
	protected PortletPage(final PageMap pageMap)
	{
		super(pageMap);
	}

	/**
	 * @see Page#Page(PageMap, IModel)
	 */
	protected PortletPage(final PageMap pageMap, final IModel model)
	{
		super(pageMap, model);
	}

	/**
	 * Constructor which receives wrapped query string parameters for a request.
	 * Having this constructor public means that your page is 'bookmarkable' and
	 * hence can be called/ created from anywhere. For bookmarkable pages (as
	 * opposed to when you construct page instances yourself, this constructor
	 * will be used in preference to a no-arg constructor, if both exist. Note
	 * that nothing is done with the page parameters argument. This constructor
	 * is provided so that tools such as IDEs will include it their list of
	 * suggested constructors for derived classes.
	 * 
	 * @param parameters
	 *            Wrapped query string parameters.
	 */
	protected PortletPage(final PageParameters parameters)
	{
		this((IModel)null);
	}

	/**
	 * Markup type for portlets is always html
	 * 
	 * @return Markup type
	 */
	public final String getMarkupType()
	{
		return "html";
	}

	/**
	 * @return The PortletRequestCycle for this PortletPage.
	 */
	protected final PortletRequestCycle getPortletRequestCycle()
	{
		return (PortletRequestCycle)getRequestCycle();
	}
}
