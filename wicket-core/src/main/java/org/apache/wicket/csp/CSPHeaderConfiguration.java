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

import static org.apache.wicket.csp.CSPDirective.CHILD_SRC;
import static org.apache.wicket.csp.CSPDirective.CONNECT_SRC;
import static org.apache.wicket.csp.CSPDirective.DEFAULT_SRC;
import static org.apache.wicket.csp.CSPDirective.FONT_SRC;
import static org.apache.wicket.csp.CSPDirective.IMG_SRC;
import static org.apache.wicket.csp.CSPDirective.MANIFEST_SRC;
import static org.apache.wicket.csp.CSPDirective.SCRIPT_SRC;
import static org.apache.wicket.csp.CSPDirective.STYLE_SRC;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.NONCE;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.NONE;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.SELF;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.STRICT_DYNAMIC;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.UNSAFE_EVAL;
import static org.apache.wicket.csp.CSPDirectiveSrcValue.UNSAFE_INLINE;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wicket.request.cycle.RequestCycle;

/**
 * {@code CSPHeaderConfiguration} contains the configuration for a Content-Security-Policy header.
 * This configuration is constructed using the available {@link CSPDirective}s. An number of default
 * profiles is provided. These profiles can be used as a basis for a specific CSP. Extra directives
 * can be added or exising directives modified.
 * 
 * @author papegaaij
 */
public class CSPHeaderConfiguration
{
	private Map<CSPDirective, List<CSPRenderable>> directives = new EnumMap<>(CSPDirective.class);

	private boolean addLegacyHeaders = false;
	
	private boolean nonceEnabled = false;

	public CSPHeaderConfiguration()
	{
	}

	/**
	 * Removes all directives from the CSP, returning an empty configuration.
	 * 
	 * @return {@code this} for chaining.
	 */
	public CSPHeaderConfiguration disabled()
	{
		return clear();
	}
	
	/**
	 * Builds a CSP configuration with the following directives: {@code default-src 'none';}
	 * {@code script-src 'self' 'unsafe-inline' 'unsafe-eval';}
	 * {@code style-src 'self' 'unsafe-inline';} {@code img-src 'self';} {@code connect-src 'self';}
	 * {@code font-src 'self';} {@code manifest-src 'self';} {@code child-src 'self';}
	 * {@code frame-src 'self'}. This will allow resources to be loaded from {@code 'self'} (the
	 * current host). In addition, unsafe inline Javascript, {@code eval()} and inline CSS is
	 * allowed.
	 * 
	 * It is recommended to not allow {@code unsafe-inline} or {@code unsafe-eval}, because those
	 * can be used to trigger XSS attacks in your application (often in combination with another
	 * bug). Because older application often rely on inline scripting and styling, this CSP can be
	 * used as a stepping stone for older Wicket applications, before switching to {@link #strict}.
	 * Using a CSP with unsafe directives is still more secure than using no CSP at all.
	 * 
	 * @return {@code this} for chaining.
	 */
	public CSPHeaderConfiguration unsafeInline()
	{
		return clear().addDirective(DEFAULT_SRC, NONE)
			.addDirective(SCRIPT_SRC, SELF, UNSAFE_INLINE, UNSAFE_EVAL)
			.addDirective(STYLE_SRC, SELF, UNSAFE_INLINE)
			.addDirective(IMG_SRC, SELF)
			.addDirective(CONNECT_SRC, SELF)
			.addDirective(FONT_SRC, SELF)
			.addDirective(MANIFEST_SRC, SELF)
			.addDirective(CHILD_SRC, SELF);
	}

	/**
	 * Builds a strict, very secure CSP configuration with the following directives:
	 * {@code default-src 'none';} {@code script-src 'strict-dynamic' 'nonce-XYZ';}
	 * {@code style-src 'nonce-XYZ';} {@code img-src 'self';} {@code connect-src 'self';}
	 * {@code font-src 'self';} {@code manifest-src 'self';} {@code child-src 'self';}
	 * {@code frame-src 'self'}. This will allow most resources to be loaded from {@code 'self'}
	 * (the current host). Scripts and styles are only allowed when rendered with the correct nonce.
	 * Wicket will automatically add the nonces to the {@code script} and {@code link} (CSS)
	 * elements and to the headers.
	 * 
	 * @return {@code this} for chaining.
	 */
	public CSPHeaderConfiguration strict()
	{
		return clear().addDirective(DEFAULT_SRC, NONE)
				.addDirective(SCRIPT_SRC, STRICT_DYNAMIC, NONCE)
			.addDirective(STYLE_SRC, NONCE)
			.addDirective(IMG_SRC, SELF)
			.addDirective(CONNECT_SRC, SELF)
			.addDirective(FONT_SRC, SELF)
			.addDirective(MANIFEST_SRC, SELF)
			.addDirective(CHILD_SRC, SELF)
			;
	}

	/**
	 * True when the {@link CSPDirectiveSrcValue#NONCE} is used in one of the directives.
	 * 
	 * @return When any of the directives contains a nonce.
	 */
	public boolean isNonceEnabled()
	{
		return nonceEnabled;
	}

	/**
	 * True when legacy headers should be added.
	 * 
	 * @return True when legacy headers should be added.
	 */
	public boolean isAddLegacyHeaders()
	{
		return addLegacyHeaders;
	}

	/**
	 * Enable legacy {@code X-Content-Security-Policy} headers for older browsers, such as IE.
	 * 
	 * @param addLegacyHeaders
	 *            True when the legacy headers should be added.
	 * @return {@code this} for chaining
	 */
	public CSPHeaderConfiguration setAddLegacyHeaders(boolean addLegacyHeaders)
	{
		this.addLegacyHeaders = addLegacyHeaders;
		return this;
	}

	/**
	 * Adds the given values to the CSP directive on this configuraiton.
	 * 
	 * @param directive
	 *            The directive to add the values to.
	 * @param values
	 *            The values to add.
	 */
	public CSPHeaderConfiguration addDirective(CSPDirective directive, CSPRenderable... values)
	{
		for (CSPRenderable value : values)
		{
			doAddDirective(directive, value);
		}
		return this;
	}

	/**
	 * Adds a free-form value to a directive for the CSP header. This is primarily meant to used for
	 * URIs.
	 * 
	 * @param directive
	 *            The directive to add the values to.
	 * @param values
	 *            The values to add.
	 */
	public CSPHeaderConfiguration addDirective(CSPDirective directive, String... values)
	{
		for (String value : values)
		{
			doAddDirective(directive, new FixedCSPDirective(value));
		}
		return this;
	}

	/**
	 * @return true if this {@code CSPHeaderConfiguration} has any directives configured.
	 */
	public boolean isSet()
	{
		return !directives.isEmpty();
	}

	/**
	 * Removes all CSP directives from the configuration.
	 * 
	 * @return {@code this} for chaining.
	 */
	public CSPHeaderConfiguration clear()
	{
		directives.clear();
		nonceEnabled = false;
		return this;
	}

	@SuppressWarnings("deprecation")
	private CSPHeaderConfiguration doAddDirective(CSPDirective directive, CSPRenderable value)
	{
		// Add backwards compatible frame-src
		// see http://caniuse.com/#feat=contentsecuritypolicy2
		if (CSPDirective.CHILD_SRC.equals(directive))
		{
			doAddDirective(CSPDirective.FRAME_SRC, value);
		}
		List<CSPRenderable> values = directives.computeIfAbsent(directive, x -> new ArrayList<>());
		directive.checkValueForDirective(value, values);
		values.add(value);
		nonceEnabled |= CSPDirectiveSrcValue.NONCE == value;
		return this;
	}

	/**
	 * Renders this {@code CSPHeaderConfiguration} into an HTTP header. The returned String will be
	 * in the form {@code "key1 value1a value1b; key2 value2a; key3 value3a value3b value3c"}.
	 * 
	 * @param listener
	 *            The {@link ContentSecurityPolicyEnforcer} that renders the header.
	 * @param cycle
	 *            The current {@link RequestCycle}.
	 * @return the rendered header.
	 */
	public String renderHeaderValue(ContentSecurityPolicyEnforcer listener, RequestCycle cycle)
	{
		return directives.entrySet()
			.stream()
			.map(e -> e.getKey().getValue() + " "
				+ e.getValue()
					.stream()
					.map(r -> r.render(listener, cycle))
					.collect(Collectors.joining(" ")))
			.collect(Collectors.joining("; "));
	}
}
