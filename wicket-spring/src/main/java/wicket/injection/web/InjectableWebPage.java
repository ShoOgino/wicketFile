/*
 * $Id$
 * $Revision$
 * $Date$
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
package wicket.injection.web;

import wicket.PageParameters;
import wicket.injection.ConfigurableInjector;
import wicket.markup.html.WebPage;
import wicket.model.IModel;

/**
 * Subclass of WebPage that is injected via an injector in the InjectorHolder
 * 
 * Be careful not to initialize fields for your dependencies to null, otherwise
 * the initialization of the subclass will replace the field assigned by the
 * injector
 * 
 * For Example:
 * 
 * <pre>
 * // this is ok
 * @SpringBean
 * IDependency dependency;
 * 
 * // this is not ok, proxy generated by injector will be replaced
 * // with null when subclass initializes the field
 * @SpringBean
 * IDependency2 dependency2 = null;
 * </pre>
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class InjectableWebPage extends WebPage
{

	/**
	 * Constructor
	 * 
	 * @param model
	 *            page model
	 */
	public InjectableWebPage(IModel model)
	{
		super(model);
		getInjector().inject(this);
	}

	/**
	 * Constructor
	 * 
	 * @param pageParameters
	 *            page parameters
	 */
	public InjectableWebPage(PageParameters pageParameters)
	{
		super(pageParameters);
		getInjector().inject(this);
	}

	/**
	 * Constructor
	 */
	public InjectableWebPage()
	{
		getInjector().inject(this);
	}

	private ConfigurableInjector getInjector()
	{
		ConfigurableInjector injector = InjectorHolder.getInjector();
		if (injector == null)
		{
			throw new RuntimeException("injector not set in InjectorHolder");
		}
		return injector;
	}
}
