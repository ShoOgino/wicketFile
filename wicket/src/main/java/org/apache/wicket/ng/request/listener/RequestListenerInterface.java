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
package org.apache.wicket.ng.request.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.ng.WicketRuntimeException;
import org.apache.wicket.ng.request.component.IRequestableComponent;
import org.apache.wicket.ng.request.cycle.RequestHandlerStack.ReplaceHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base class for request listener interfaces.
 * 
 * @author Jonathan Locke
 */
public class RequestListenerInterface
{
	/** Map from name to request listener interface */
	private static final Map<String, RequestListenerInterface> interfaces = Collections.synchronizedMap(new HashMap<String, RequestListenerInterface>());

	/** Log. */
	private static final Logger log = LoggerFactory.getLogger(RequestListenerInterface.class);

	/**
	 * Looks up a request interface listener by name.
	 * 
	 * @param interfaceName
	 *            The interface name
	 * @return The RequestListenerInterface object, or null if none is found
	 * 
	 */
	public static final RequestListenerInterface forName(final String interfaceName)
	{
		return interfaces.get(interfaceName);
	}

	/** The listener interface method */
	private Method method;

	/** The name of this listener interface */
	private final String name;

	/**
	 * Constructor.
	 * 
	 * @param listenerInterfaceClass
	 *            The interface class, which must extend IRequestListener.
	 */
	public RequestListenerInterface(final Class<? extends IRequestListener> listenerInterfaceClass)
	{
		// Ensure that it extends IRequestListener
		if (!IRequestListener.class.isAssignableFrom(listenerInterfaceClass))
		{
			throw new IllegalArgumentException("Class " + listenerInterfaceClass +
				" must extend IRequestListener");
		}


		// Get interface methods
		final Method[] methods = listenerInterfaceClass.getMethods();

		// If there is only one method
		if (methods.length == 1)
		{
			// and that method takes no parameters
			if (methods[0].getParameterTypes().length == 0)
			{
				method = methods[0];
			}
			else
			{
				throw new IllegalArgumentException("Method " + methods[0] + " in interface " +
					listenerInterfaceClass + " cannot take any arguments");
			}
		}
		else
		{
			throw new IllegalArgumentException("Interface " + listenerInterfaceClass +
				" can have only one method");
		}

		// Save short class name
		name = listenerInterfaceClass.getSimpleName();

		// Register this listener
		register();
	}

	/**
	 * @return The method for this request listener interface
	 */
	public final Method getMethod()
	{
		return method;
	}

	/**
	 * @return The name of this request listener interface
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * Invokes a given interface on a component.
	 * 
	 * @param component
	 *            The component
	 */
	public final void invoke(final IRequestableComponent component)
	{
		if (!component.canCallListenerInterface())
		{
			// just return so that we have a silent fail and just re-render the
			// page
			log.info("component not enabled or visible; ignoring call. Component: " + component);
			return;
		}

		try
		{
			// Invoke the interface method on the component
			method.invoke(component, new Object[] {});
		}
		catch (InvocationTargetException e)
		{
			// Honor redirect exception contract defined in IPageFactory
			// TODO: 
			if (/*e.getTargetException() instanceof AbstractRestartResponseException ||
				e.getTargetException() instanceof AuthorizationException ||*/
				e.getTargetException() instanceof ReplaceHandlerException ||
				e.getTargetException() instanceof WicketRuntimeException)
			{
				throw (RuntimeException)e.getTargetException();
			}
			throw new WicketRuntimeException("Method " + method.getName() + " of " +
				method.getDeclaringClass() + " targeted at component " + component +
				" threw an exception", e);
		}
		catch (Exception e)
		{
			throw new WicketRuntimeException("Method " + method.getName() + " of " +
				method.getDeclaringClass() + " targeted at component " + component +
				" threw an exception", e);
		}		
	}

	/**
	 * Invokes a given interface on a component's behavior.
	 * 
	 * @param component
	 *            The component
	 * @param behavior
	 */
	public final void invoke(final IRequestableComponent component, final IBehavior behavior)
	{
		if (!component.canCallListenerInterface())
		{
			// just return so that we have a silent fail and just re-render the
			// page
			log.info("component not enabled or visible; ignoring call. Component: " + component);
			return;
		}

		try
		{
			// Invoke the interface method on the component
			method.invoke(behavior, new Object[] {});
		}
		catch (InvocationTargetException e)
		{
			// Honor redirect exception contract defined in IPageFactory
			// TODO
			if (/*e.getTargetException() instanceof AbstractRestartResponseException ||
				e.getTargetException() instanceof AuthorizationException ||*/
				e.getTargetException() instanceof WicketRuntimeException)
			{
				throw (RuntimeException)e.getTargetException();
			}
			throw new WicketRuntimeException("Method " + method.getName() + " of " +
				method.getDeclaringClass() + " targeted at behavior " + behavior +
				" on component " + component + " threw an exception", e);
		}
		catch (Exception e)
		{
			throw new WicketRuntimeException("Method " + method.getName() + " of " +
				method.getDeclaringClass() + " targeted at behavior " + behavior +
				" on component " + component + " threw an exception", e);
		}		
	}


	/**
	 * Method to call to register this interface for use
	 */
	public void register()
	{
		// Register this listener interface
		registerRequestListenerInterface(this);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[RequestListenerInterface name=" + name + ", method=" + method + "]";
	}


	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT CALL IT.
	 * <p>
	 * In previous versions of Wicket, request listeners were manually registered by calling this
	 * method. Now there is a first class RequestListenerInterface object which should be
	 * constructed as a constant member of the interface to enable automatic interface registration.
	 * <p>
	 * Adds a request listener interface to the map of interfaces that can be invoked by outsiders.
	 * 
	 * @param requestListenerInterface
	 *            The request listener interface object
	 */
	private final void registerRequestListenerInterface(
		final RequestListenerInterface requestListenerInterface)
	{
		// Check that a different interface method with the same name has not
		// already been registered
		final RequestListenerInterface existingInterface = RequestListenerInterface.forName(requestListenerInterface.getName());
		if (existingInterface != null &&
			existingInterface.getMethod() != requestListenerInterface.getMethod())
		{
			throw new IllegalStateException("Cannot register listener interface " +
				requestListenerInterface +
				" because it conflicts with the already registered interface " + existingInterface);
		}

		// Save this interface method by the non-qualified class name
		interfaces.put(requestListenerInterface.getName(), requestListenerInterface);

		log.info("registered listener interface " + this);
	}
}
