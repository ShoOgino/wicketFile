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
package org.apache.wicket.csp;

import org.apache.wicket.request.cycle.RequestCycle;

/**
 * {@code CSPRenderable} describes a directive that is part of a Content-Security-Policy (CSP in
 * short). Most directives are predefined in enums.
 * 
 * @author papegaaij
 * @see CSPDirectiveSrcValue
 * @see CSPDirectiveSandboxValue
 * @see FixedCSPDirective
 */
public interface CSPRenderable
{
	/**
	 * Renders the value that should be put in the CSP header.
	 * 
	 * @param listener
	 *            The {@link CSPSettingRequestCycleListener} that renders this value.
	 * @param cycle
	 *            The current {@link RequestCycle}.
	 * @return The rendered value.
	 */
	public String render(CSPSettingRequestCycleListener listener, RequestCycle cycle);
}