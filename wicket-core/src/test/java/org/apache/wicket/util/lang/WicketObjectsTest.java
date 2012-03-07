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
package org.apache.wicket.util.lang;

import java.io.Serializable;

import org.apache.wicket.WicketTestCase;
import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.junit.Test;


/**
 * Tests the WicketObjects class.
 * 
 * @author Martijn Dashorst
 */
public class WicketObjectsTest extends WicketTestCase
{

	/**
	 * Test method for 'org.apache.wicket.util.lang.Objects.clone(Object)'
	 */
	@Test
	public void cloneNull()
	{
		Object clone = WicketObjects.cloneModel(null);
		assertEquals(null, clone);
	}

	/**
	 * Test method for 'org.apache.wicket.util.lang.Objects.clone(Object)'
	 */
	@Test
	public void cloneString()
	{
		String cloneMe = "Mini-me";

		Object clone = WicketObjects.cloneModel(cloneMe);
		assertEquals(cloneMe, clone);
		assertNotSame(cloneMe, clone);
	}

	/**
	 * Test method for 'org.apache.wicket.util.lang.Objects.clone(Object)'
	 */
	@Test
	public void cloneObject()
	{
		Object cloneMe = new Object();

		try
		{
			WicketObjects.cloneModel(cloneMe);
			fail("Exception expected");
		}
		catch (RuntimeException e)
		{
			assertTrue(true);
		}
	}

	/**
	 * Test method for component cloning
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void componentClone()
	{
		PropertyModel<String> pm = new PropertyModel<String>(new TextField<String>("test",
			new Model<String>("test")), "modelObject");
		PropertyModel<String> pm2 = (PropertyModel<String>)WicketObjects.cloneModel(pm);
		assertTrue(pm.getObject() == pm2.getObject());
	}

	/**
	 * Test method for 'org.apache.wicket.util.lang.Objects.clone(Object)'
	 */
	@Test
	public void cloneCloneObject()
	{
		CloneObject cloneMe = new CloneObject();
		cloneMe.nr = 1;

		Object clone = WicketObjects.cloneModel(cloneMe);
		assertEquals(cloneMe, clone);
		assertNotSame(cloneMe, clone);
	}

	/**
	 * Used for testing the clone function.
	 */
	private static final class CloneObject implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * int for testing equality.
		 */
		private int nr;

		/**
		 * @see Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o)
		{
			CloneObject other = (CloneObject)o;
			return other.nr == nr;
		}
	}
}
