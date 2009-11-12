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
package org.apache.wicket.ng.resource;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.util.lang.Checks;
import org.apache.wicket.util.lang.Objects;

/**
 * Allows to register and lookup {@link ResourceReference}s.
 * 
 * @author Matej Knopp
 */
public class ResourceReferenceRegistry
{
	private static class Key
	{
		private final String scope;
		private final String name;
		private final Locale locale;
		private final String style;
		private final String variation;

		public Key(String scope, String name, Locale locale, String style, String variation)
		{
			Checks.argumentNotNull(scope, "scope");
			Checks.argumentNotNull(name, "name");

			this.scope = scope;
			this.name = name;
			this.locale = locale;
			this.style = style;
			this.variation = variation;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj instanceof Key == false)
			{
				return false;
			}
			Key that = (Key)obj;
			return Objects.equal(scope, that.scope) && //
				Objects.equal(name, that.name) && //
				Objects.equal(locale, that.locale) && //
				Objects.equal(style, that.style) && //
				Objects.equal(variation, that.variation);
		}

		@Override
		public int hashCode()
		{
			return Objects.hashCode(scope, name, locale, style, variation);
		}
	};

	private final Map<Key, ResourceReference> map = new ConcurrentHashMap<Key, ResourceReference>();

	/**
	 * Registers the given {@link ResourceReference}.
	 * 
	 * @param reference
	 */
	public void registerResourceReference(ResourceReference reference)
	{
		Checks.argumentNotNull(reference, "reference");

		Key key = new Key(reference.getScope().getName(), reference.getName(),
			reference.getLocale(), reference.getStyle(), reference.getVariation());
		map.put(key, reference);
	}

	/**
	 * Unregisters the given {@link ResourceReference}.
	 * 
	 * @param reference
	 */
	public void unregisterResourceReference(ResourceReference reference)
	{
		Checks.argumentNotNull(reference, "reference");

		Key key = new Key(reference.getScope().getName(), reference.getName(),
			reference.getLocale(), reference.getStyle(), reference.getVariation());
		map.remove(key);
	}

	protected ResourceReference getResourceReference(Class<?> scope, String name, Locale locale,
		String style, String variation, boolean strict, boolean createIfNotFound)
		{
		Key key = new Key(scope.getName(), name, locale, style, variation);
			ResourceReference res = map.get(key);
			if (strict || res != null)
			{
				return res;
			}
			else
			{
			res = getResourceReference(scope, name, locale, style, null, true, false);
				if (res == null)
				{
				res = getResourceReference(scope, name, locale, null, variation, true, false);
				}
			if (res == null)
			{
				res = getResourceReference(scope, name, locale, null, null, true, false);
			}
			if (res == null)
			{
				res = getResourceReference(scope, name, null, style, variation, true, false);
			}
			if (res == null)
			{
				res = getResourceReference(scope, name, null, style, null, true, false);
			}
			if (res == null)
			{
				res = getResourceReference(scope, name, null, null, variation, true, false);
			}
			if (res == null)
			{
				res = getResourceReference(scope, name, null, null, null, true, false);
			}
				if (res == null && createIfNotFound)
				{
				res = createDefaultResourceReference(scope, name, locale, style, variation);
				}
				return res;
			}
		}
	
	/**
	 * Looks up resource reference with specified attributes. If the reference is not found and
	 * <code>strict</code> is set to <code>false</code>, result of
	 * {@link #createDefaultResourceReference(Class, String, Locale, String)} is returned.
	 * 
	 * @param scope
	 *            mandatory parameter
	 * @param name
	 *            mandatory parameter
	 * @param locale
	 * @param style
	 * @param variation
	 * @param strict
	 *            if <code>strict</code> is <code>true</code> only resources that match exactly are
	 *            returned. Otherwise if there is no resource registered that is an exact match,
	 *            also resources with <code>null</code> style and locale are tried. If still no
	 *            resource is found, result of
	 *            {@link #createDefaultResourceReference(Class, String, Locale, String)} is
	 *            returned.
	 * @return {@link ResourceReference} or <code>null</code>
	 */
	public ResourceReference getResourceReference(Class<?> scope, String name, Locale locale,
		String style, String variation, boolean strict)
	{
		ResourceReference reference = getResourceReference(scope, name, locale, style, variation,
			strict, false);
		if (reference == null)
		{
			// TODO: Check the class static member for ResourceReferences and register those 
		}
		return reference;
	}

	protected ResourceReference createDefaultResourceReference(Class<?> scope, String name,
		Locale locale, String style, String variation)
	{
		// override in superclass to e.g. return PackageResourceReference if there is one
		return null;
	}
}
