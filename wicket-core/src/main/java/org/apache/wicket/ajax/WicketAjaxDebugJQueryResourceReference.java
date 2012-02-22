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
package org.apache.wicket.ajax;

import java.util.Collections;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author hoeve
 */
public class WicketAjaxDebugJQueryResourceReference extends JavaScriptResourceReference
{
	private static final long serialVersionUID = 1L;

	private static WicketAjaxDebugJQueryResourceReference instance = new WicketAjaxDebugJQueryResourceReference();

	/**
	 * @return the singleton instance
	 */
	public static WicketAjaxDebugJQueryResourceReference get()
	{
		return instance;
	}

	private WicketAjaxDebugJQueryResourceReference()
	{
		super(AbstractDefaultAjaxBehavior.class, "res/js/wicket-ajax-jquery-debug.js");
	}

	@Override
	public Iterable<? extends HeaderItem> getDependencies()
	{
		return Collections.singletonList(JavaScriptHeaderItem.forReference(WicketAjaxJQueryResourceReference.get()));
	}
}
