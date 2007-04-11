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
package org.apache.wicket.examples.hangman;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Test case for the <code>WordGenerator</code> class.
 * 
 * @author Chris Turner
 * @version 1.0
 */
public class WordGeneratorTest extends TestCase
{

	private static final Log log = LogFactory.getLog(WordGeneratorTest.class);

	public WicketTester tester;
	
	/**
	 * Create the test case.
	 * 
	 * @param message
	 *            The test name
	 */
	public WordGeneratorTest(String message)
	{
		super(message);
	}
	
	protected void setUp() throws Exception
	{
		tester = new WicketTester();
	}

	protected void tearDown() throws Exception
	{
		tester.destroy();
	}

	/**
	 * Tests word generator
	 * 
	 * @throws Exception
	 */
	public void testWordGenerator() throws Exception
	{
		WordGenerator wg = new WordGenerator();
		int wordCount = wg.size();
		Set words = new HashSet();
		log.info("First iteration...");
		for (int i = 0; i < wordCount; i++)
		{
			Word word = wg.next();
			log.info("Word found: " + word);
			Assert.assertFalse("Word should not be returned twice", words.contains(word));
			words.add(word);
		}
		log.info("Second iteration...");
		for (int i = 0; i < wordCount; i++)
		{
			Word word = wg.next();
			log.info("Word found: " + word);
			Assert.assertTrue("Word " + word + " should have been returned only once", words
					.remove(word));
		}
		Assert.assertTrue("All words should have been returned twice", words.isEmpty());
	}

	/**
	 * Tests word generator
	 * 
	 * @throws Exception
	 */
	public void testSuppliedWordConstructor() throws Exception
	{
		WordGenerator wg = new WordGenerator(new String[] { "testing" });
		Assert.assertEquals("Word should be as expected", "testing", wg.next().asString());
	}
}
