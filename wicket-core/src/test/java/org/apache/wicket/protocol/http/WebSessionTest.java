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
package org.apache.wicket.protocol.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;

import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.Test;

/**
 * @author Timo Rantalaiho
 */
class WebSessionTest
{
	/**
	 * testReadsLocaleFromRequestOnConstruction()
	 */
	@Test
	void readsLocaleFromRequestOnConstruction()
	{
		final Locale locale = Locale.TRADITIONAL_CHINESE;
		MockWebRequest request = new MockWebRequest(Url.parse("/"))
		{
			@Override
			public Locale getLocale()
			{
				return locale;
			}
		};

		WebSession session = new WebSession(request);
		assertEquals(locale, session.getLocale());
	}

	@Test
	public void changeSessionId() throws Exception
	{
		WicketTester tester = new WicketTester(new MockApplication());
		MockHttpSession httpSession = (MockHttpSession)tester.getRequest().getSession();
		Session session = tester.getSession();

		httpSession.setTemporary(false);
		session.bind();

		String oldId = session.getId();
		assertNotNull(oldId);

		session.changeSessionId();
		String newId = session.getId();

		assertNotEquals(oldId, newId);
	}

	/**
	 * WICKET-6558
	 */
	@Test
	public void lockAfterDetach() throws Exception
	{
		WicketTester tester = new WicketTester(new MockApplication());

		Session session = tester.getSession();

		session.getPageManager();
		
		session.detach();

		try
		{
			session.getPageManager();
			fail();
		}
		catch (WicketRuntimeException ex)
		{
			assertEquals("The request has been processed. Access to pages is no longer allowed", ex.getMessage());
		}
	}
}
