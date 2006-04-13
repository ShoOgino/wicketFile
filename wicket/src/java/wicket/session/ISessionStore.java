/*
 * $Id: ISessionStore.java 3777 2006-01-14 14:49:01 -0800 (Sat, 14 Jan 2006)
 * jonathanlocke $ $Revision: 3777 $ $Date: 2006-01-14 14:49:01 -0800 (Sat, 14
 * Jan 2006) $
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
package wicket.session;

import java.util.List;

import wicket.Request;
import wicket.Session;

/**
 * The actual store that is used by {@link wicket.Session} to store its
 * attributes.
 * 
 * @author Eelco Hillenius
 * @author Johan Compagner
 */
public interface ISessionStore
{
	/**
	 * Gets the attribute value with the given name
	 * 
	 * @param request
	 *            the current request
	 * @param name
	 *            The name of the attribute to store
	 * @return The value of the attribute
	 */
	Object getAttribute(Request request, final String name);

	/**
	 * @param request
	 *            the current request
	 * 
	 * @return List of attributes for this session
	 */
	List getAttributeNames(Request request);


	/**
	 * Invalidates the session.
	 * 
	 * @param request
	 *            the current request
	 */
	void invalidate(Request request);

	/**
	 * Removes the attribute with the given name.
	 * 
	 * @param request
	 *            the current request
	 * @param name
	 *            the name of the attribute to remove
	 */
	void removeAttribute(Request request, String name);

	/**
	 * Adds or replaces the attribute with the given name and value.
	 * 
	 * @param request
	 *            the current request
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the value of the attribute
	 */
	void setAttribute(Request request, String name, Object value);

	/**
	 * Get the session id for the provided request.
	 * 
	 * @param request
	 *            The request
	 * @return The session id for the provided request
	 */
	String getSessionId(Request request);

	/**
	 * Retrieves the session for the provided request from this facade.
	 * <p>
	 * This method should return null if it is not bound yet, so that Wicket can
	 * recognize that it should create a session and call
	 * {@link #bind(Request, Session)} right after that.
	 * </p>
	 * 
	 * @param request
	 *            The current request
	 * @return The session for the provided request or null if the session was
	 *         not bound
	 */
	Session lookup(Request request);

	/**
	 * Adds the provided new session to this facade using the provided request.
	 * 
	 * @param request
	 *            The request that triggered making a new sesion
	 * @param newSession
	 *            The new session
	 */
	void bind(Request request, Session newSession);

	/**
	 * Adds the provided new session to this facade using the provided request.
	 * 
	 * @param application
	 *            The application object that gets the unbind.
	 * 
	 * @param sessionId
	 *            The SessionId that must be unbinded.
	 */
	void unbind(String sessionId);
}
