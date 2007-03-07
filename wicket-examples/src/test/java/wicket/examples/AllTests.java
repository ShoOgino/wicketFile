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
package wicket.examples;

import junit.framework.Test;
import junit.framework.TestSuite;
import wicket.examples.ajax.prototype.AjaxTest;
import wicket.examples.compref.ComprefTest;
import wicket.examples.encodings.EncodingTest;
import wicket.examples.forminput.FormInputTest;
import wicket.examples.guestbook.GuestbookTest;
import wicket.examples.hangman.HangManTest;
import wicket.examples.hangman.WordGeneratorTest;
import wicket.examples.helloworld.HelloWorldTest;
import wicket.examples.images.ImagesTest;
import wicket.examples.library.LibraryTest;
import wicket.examples.linkomatic.LinkomaticTest;
import wicket.examples.niceurl.NiceUrlTest;
import wicket.examples.panels.signin.CookieTest;
import wicket.examples.repeater.RepeaterTest;
import wicket.examples.signin2.Signin2Test;
import wicket.examples.template.TemplateTest;

import com.meterware.httpunit.HttpUnitOptions;

/**
 * All tests in the project; used by Maven.
 */
public final class AllTests extends TestSuite
{
	/**
	 * Construct.
	 */
	public AllTests()
	{
		super();
	}

	/**
	 * Construct.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public AllTests(Class arg0, String arg1)
	{
		super(arg0, arg1);
	}

	/**
	 * Construct.
	 * 
	 * @param arg0
	 */
	public AllTests(Class arg0)
	{
		super(arg0);
	}

	/**
	 * Construct.
	 * 
	 * @param arg0
	 */
	public AllTests(String arg0)
	{
		super(arg0);
	}

	/**
	 * Suite method.
	 * 
	 * @return test suite
	 */
	public static Test suite()
	{
		// The javascript 'history' variable is not supported by
		// httpunit and we don't want httpunit to throw an
		// exception just because they can not handle it.
		HttpUnitOptions.setExceptionsThrownOnScriptError(false);

		TestSuite suite = new TestSuite();
		suite.addTestSuite(HangManTest.class);
		suite.addTestSuite(WordGeneratorTest.class);
		suite.addTestSuite(HelloWorldTest.class);
		suite.addTestSuite(GuestbookTest.class);
		suite.addTestSuite(FormInputTest.class);
		suite.addTestSuite(LinkomaticTest.class);
		suite.addTestSuite(Signin2Test.class);
		suite.addTestSuite(CookieTest.class);
		suite.addTestSuite(AjaxTest.class);
		suite.addTestSuite(ComprefTest.class);
		suite.addTestSuite(EncodingTest.class);
		suite.addTestSuite(NiceUrlTest.class);
		suite.addTestSuite(TemplateTest.class);
		suite.addTestSuite(RepeaterTest.class);
		suite.addTestSuite(ImagesTest.class);
		suite.addTestSuite(LibraryTest.class);
		JettyTestCaseDecorator deco = new JettyTestCaseDecorator(suite);
		return deco;
	}
}
