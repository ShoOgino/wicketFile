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
package org.apache.wicket.markup.html;

import org.apache.wicket.request.resource.JavascriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * Resource reference to wicket-event.js which is used to allow events via javascript
 * 
 * @author ivaynberg
 */
public class WicketEventReference extends JavascriptResourceReference
{
	private static final long serialVersionUID = 1L;

	/**
	 * Singleton instance of this reference
	 */
	public static final ResourceReference INSTANCE = new WicketEventReference();

	private WicketEventReference()
	{
		super(WicketEventReference.class, "wicket-event.js");
	}


}
