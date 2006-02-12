/*
 * $Id: FormSubmitInterfaceRequestTarget.java,v 1.5 2006/01/17 20:35:34
 * jonathanlocke Exp $ $Revision$ $Date$
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
package wicket.request.target;

import java.lang.reflect.Method;

import wicket.Component;
import wicket.Page;
import wicket.RequestCycle;

/**
 * Interface request target for forms.
 * 
 * @author jcompagner
 */
public class FormSubmitInterfaceRequestTarget extends AbstractListenerInterfaceRequestTarget
{
	/**
	 * Construct.
	 * 
	 * @param page
	 *            The Page
	 * @param component
	 *            The component
	 * @param listenerMethod
	 *            The listener method to invoke
	 */
	public FormSubmitInterfaceRequestTarget(Page page, Component component, Method listenerMethod)
	{
		super(page, component, listenerMethod);
	}

	/**
	 * @see wicket.request.target.IEventProcessor#processEvents(wicket.RequestCycle)
	 */
	public void processEvents(RequestCycle requestCycle)
	{
		invokeInterface(getTarget(), getListenerMethod(), getPage());
	}
}
