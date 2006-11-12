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
package wicket.markup.html.basic;



/**
 * Mock page for testing.
 */
public class SimplePage_3 extends SimplePage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public SimplePage_3()
	{
		// This has changed with wicket 2.0 and the constructor
		// change. The markup now MUST contain the markup for
		// all component even if set invisible. setVisible()
		// is called after the constructor
		get("myLabel").setVisible(false);
		get("test").setVisible(false);
		get("myPanel").setVisible(false);
		get("myBorder").setVisible(false);
		get("myBorder2").setVisible(false);
	}
}
