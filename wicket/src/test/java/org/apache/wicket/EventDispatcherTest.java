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
package org.apache.wicket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.event.IEventSink;
import org.apache.wicket.markup.html.WebComponent;

/**
 * @author Pedro Santos
 */
public class EventDispatcherTest extends WicketTestCase
{

	/**
	 * Testing DispatchToAnnotatedMethod event dispatchers in frameworksettings. This dispatcher
	 * invoke the methods annotated with @EvenCallback
	 * */
	public void testDispatchToAnnotatedMethod()
	{
		tester.getApplication().getFrameworkSettings().add(new DispatchToAnnotatedMethod());
		MockPageWithOneComponent page = new MockPageWithOneComponent();
		TestComponent testComponent = new TestComponent(MockPageWithOneComponent.COMPONENT_ID);
		page.add(testComponent);
		page.send(page, Broadcast.DEPTH, null);
		assertTrue(testComponent.callbackInvoked);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface EvenCallback {

	}

	/** */
	public static class DispatchToAnnotatedMethod implements IEventDispatcher
	{
		public void dispatchEvent(IEventSink sink, IEvent<?> event)
		{
			Method[] sinkMethods = sink.getClass().getMethods();
			for (int i = 0; i < sinkMethods.length; i++)
			{
				if (sinkMethods[i].isAnnotationPresent(EvenCallback.class))
				{
					try
					{
						sinkMethods[i].invoke(sink);
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	/** */
	public static class TestComponent extends WebComponent
	{
		private static final long serialVersionUID = 1L;
		boolean callbackInvoked;

		/**
		 * @param id
		 */
		public TestComponent(String id)
		{
			super(id);
		}

		/** */
		@EvenCallback
		public void testCallback()
		{
			callbackInvoked = true;
		}
	}

}
