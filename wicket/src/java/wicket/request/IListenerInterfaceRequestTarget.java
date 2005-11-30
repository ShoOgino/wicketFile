/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.request;

import java.lang.reflect.Method;

import wicket.Component;
import wicket.RequestCycle;

/**
 * Target that denotes a page instance and a call to a component on that page
 * using an listener interface method.
 * 
 * @author Eelco Hillenius
 */
public interface IListenerInterfaceRequestTarget extends IPageRequestTarget, ISessionSynchronizable
{

	/**
	 * Gets the target component.
	 * 
	 * @return the target component
	 */
	Component getComponent();

	/**
	 * Gets listener method.
	 * 
	 * @return the listener method
	 */
	Method getListenerMethod();

	/**
	 * Gets the optional behaviour id in case this call points a dispatched
	 * method (i.e. a coupled behaviour such as an {@link wicket.AjaxHandler}).
	 * 
	 * @return the optional behaviour id
	 */
	String getBehaviourId();
	
	
	/**
	 * After a page is restored, this method is responsible for calling any
	 * event handling code based on the request.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 */
	void processEvents(final RequestCycle requestCycle);
}