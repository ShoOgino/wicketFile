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
package org.apache.wicket.protocol.http.mock;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.wicket.util.value.ValueMap;


/**
 * Mock implementation of the <code>WebSession</code> interface for use by the test harnesses.
 * 
 * @author Chris Turner
 */
public class MockHttpSession implements HttpSession, Serializable
{
	private static final long serialVersionUID = 1L;

	private final ValueMap attributes = new ValueMap();

	private final ServletContext context;

	private final long creationTime = System.currentTimeMillis();

	private final String id = UUID.randomUUID().toString().replace(':', '_').replace('-', '_');

	private long lastAccessedTime = 0;

	private boolean temporary = true;

	/**
	 * Create the session.
	 * 
	 * @param context
	 */
	public MockHttpSession(final ServletContext context)
	{
		this.context = context;
	}

	/**
	 * Get the attribute with the given name.
	 * 
	 * @param name
	 *            The attribute name
	 * @return The value or null
	 */
	public Object getAttribute(final String name)
	{
		return attributes.get(name);
	}

	/**
	 * Get the names of the attributes in the session.
	 * 
	 * @return The attribute names
	 */
	public Enumeration<String> getAttributeNames()
	{
		return Collections.enumeration(attributes.keySet());

	}

	/**
	 * Get the creation time of the session.
	 * 
	 * @return The creation time
	 */
	public long getCreationTime()
	{
		return creationTime;
	}

	/**
	 * Return the id of this session.
	 * 
	 * @return The id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Get the time the session was last accessed.
	 * 
	 * @return The last accessed time
	 */
	public long getLastAccessedTime()
	{
		return lastAccessedTime;
	}

	/**
	 * NOT USED. Sessions never expire in the test harness.
	 * 
	 * @return Always returns 0
	 */
	public int getMaxInactiveInterval()
	{
		return 0;
	}

	/**
	 * Return the servlet context for the session.
	 * 
	 * @return The servlet context
	 */
	public ServletContext getServletContext()
	{
		return context;
	}

	/**
	 * NOT USED.
	 * 
	 * @return Always null
	 * @deprecated
	 */
	@Deprecated
	public javax.servlet.http.HttpSessionContext getSessionContext()
	{
		return null;
	}

	/**
	 * Get the value for the given name.
	 * 
	 * @param name
	 *            The name
	 * @return The value or null
	 * @deprecated use getAttribute(String) instead
	 */
	@Deprecated
	public Object getValue(final String name)
	{
		return getAttribute(name);
	}

	/**
	 * Get the names of the values in the session.
	 * 
	 * @return The names of the attributes
	 * @deprecated use getAttributeNames() instead
	 */
	@Deprecated
	public String[] getValueNames()
	{
		String[] result = new String[attributes.size()];
		return attributes.keySet().toArray(result);
	}

	/**
	 * Invalidate the session.
	 */
	public void invalidate()
	{
		attributes.clear();
	}

	/**
	 * Check if the session is new.
	 * 
	 * @return Always false
	 */
	public boolean isNew()
	{
		return false;
	}

	/**
	 * Set a value.
	 * 
	 * @param name
	 *            The name of the value
	 * @param o
	 *            The value
	 * @deprecated Use setAttribute(String, Object) instead
	 */
	@Deprecated
	public void putValue(final String name, final Object o)
	{
		setAttribute(name, o);
	}

	/**
	 * Remove an attribute.
	 * 
	 * @param name
	 *            The name of the attribute
	 */
	public void removeAttribute(final String name)
	{
		attributes.remove(name);
	}

	/**
	 * Remove a value.
	 * 
	 * @param name
	 *            The name of the value
	 * @deprecated Use removeAttribute(String) instead
	 */
	@Deprecated
	public void removeValue(String name)
	{
		removeAttribute(name);
	}

	/**
	 * Set an attribute.
	 * 
	 * @param name
	 *            The name of the attribute to set
	 * @param o
	 *            The value to set
	 */
	public void setAttribute(final String name, final Object o)
	{
		attributes.put(name, o);
	}

	/**
	 * NOT USED. Sessions never expire in the test harness.
	 * 
	 * @param i
	 *            The value
	 */
	public void setMaxInactiveInterval(final int i)
	{
	}

	/**
	 * Set the last accessed time for the session.
	 */
	public void timestamp()
	{
		lastAccessedTime = System.currentTimeMillis();
	}

	/**
	 * Indicates the state of the session. Temporary or persisted.
	 * 
	 * @return true if this is a temporary session, false otherwise
	 */
	public final boolean isTemporary()
	{
		return temporary;
	}

	/**
	 * Changes the state of this session. Temporary or persisted. Upon creation all sessions are
	 * temporary.
	 * 
	 * @param temporary
	 *            true, for a temporary session, false for a persisted session
	 */
	public final void setTemporary(boolean temporary)
	{
		this.temporary = temporary;
	}
}