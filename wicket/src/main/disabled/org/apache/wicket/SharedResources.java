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
package org.apache.wicket;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class which holds shared resources. Resources can be shared by name. An optional scope can be
 * given to prevent naming conflicts and a locale and/or style can be given as well.
 * 
 * <p>
 * Unlike component hosted resources, shared resources have stable URLs, which makes them suitable
 * for indexing by web crawlers and caching by web browsers. As they are also not synchronised on
 * the {@link Session}, they can be loaded asynchronously, which is important with images and
 * resources such as JavaScript and CSS.
 * 
 * @see Resource
 * @author Jonathan Locke
 * @author Johan Compagner
 * @author Gili Tzabari
 */
public class SharedResources
{
	/** Logger */
	private static final Logger log = LoggerFactory.getLogger(SharedResources.class);

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT CALL IT.
	 * 
	 * <p>
	 * Inserts _[locale] and _[style] into path just before any extension that might exist.
	 * 
	 * @param path
	 *            The resource path
	 * @param locale
	 *            The locale
	 * @param style
	 *            The style (see {@link org.apache.wicket.Session})
	 * @param variation
	 *            The component specific variation of the style
	 * @return The localized path
	 */
	public static String resourceKey(final String path, final Locale locale, final String style,
		final String variation)
	{
		// escape sequence for '..' (prevents crippled urls in browser)
		final CharSequence parentEscape = Application.get()
			.getResourceSettings()
			.getParentFolderPlaceholder();

		final String extension = Files.extension(path);
		String basePath = Files.basePath(path, extension);

		if (Strings.isEmpty(parentEscape) &&
			(Application.get().getConfigurationType() == Application.DEVELOPMENT) &&
			basePath.contains("../"))
		{
			log.error("----------------------------------------------------------------------------------------");
			log.error("Your path looks like: " + path);
			log.error("For security reasons moving up '../' is disabled by default. Please see");
			log.error("IResourceSettings.getParentFolderPlaceholder() and PackageResourceGuard for more details");
			log.error("----------------------------------------------------------------------------------------");
		}

		// get relative path to resource, replace '..' with escape sequence
		basePath = basePath.replace("../", parentEscape + "/");
		final AppendingStringBuffer buffer = new AppendingStringBuffer(basePath.length() + 16);
		buffer.append(basePath);

		// First style because locale can append later on.
		if (style != null)
		{
			buffer.append("_");
			buffer.append(style);
		}
		if (variation != null)
		{
			buffer.append("_");
			buffer.append(variation);
		}
		if (locale != null)
		{
			buffer.append("_");
			boolean l = locale.getLanguage().length() != 0;
			boolean c = locale.getCountry().length() != 0;
			boolean v = locale.getVariant().length() != 0;
			buffer.append(locale.getLanguage());
			if (c || (l && v))
			{
				buffer.append('_').append(locale.getCountry()); // This may just
				// append '_'
			}
			if (v && (l || c))
			{
				buffer.append('_').append(locale.getVariant());
			}
		}
		if (extension != null)
		{
			buffer.append('.');
			buffer.append(extension);
		}
		return buffer.toString();
	}

	/** Map of Class to alias String */
	private final Map<Class<?>, String> classAliasMap = new WeakHashMap<Class<?>, String>();

	/** Map of alias String to WeakReference(Class) */
	private final Map<String, WeakReference<Class<?>>> aliasClassMap = new HashMap<String, WeakReference<Class<?>>>();

	/** Map of shared resources states */
	private final ConcurrentHashMap<String, Resource> resourceMap = new ConcurrentHashMap<String, Resource>();

	/**
	 * Construct.
	 * 
	 * @param application
	 *            The application
	 */
	SharedResources(Application application)
	{
	}

	/**
	 * Adds a resource.
	 * 
	 * @param scope
	 *            Scope of resource
	 * @param name
	 *            Logical name of resource
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The resource style (see {@link org.apache.wicket.Session})
	 * @param variation
	 *            The component specific variation of the style
	 * @param resource
	 *            Resource to store
	 */
	public final void add(final Class<?> scope, final String name, final Locale locale,
		final String style, final String variation, final Resource resource)
	{
		// Store resource
		final String key = resourceKey(scope, name, locale, style, variation);
		if (resourceMap.putIfAbsent(key, resource) == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("added shared resource " + key);
			}
		}
	}

	/**
	 * Adds a resource.
	 * 
	 * @param name
	 *            Logical name of resource
	 * @param locale
	 *            The locale of the resource
	 * @param resource
	 *            Resource to store
	 */
	public final void add(final String name, final Locale locale, final Resource resource)
	{
		add(Application.class, name, locale, null, null, resource);
	}

	/**
	 * Adds a resource.
	 * 
	 * @param name
	 *            Logical name of resource
	 * @param resource
	 *            Resource to store
	 */
	public final void add(final String name, final Resource resource)
	{
		add(Application.class, name, null, null, null, resource);
	}

	/**
	 * @param scope
	 *            The resource's scope
	 * @param name
	 *            Name of resource to get
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The resource style (see {@link org.apache.wicket.Session})
	 * @param variation
	 *            The component specific variation of the style
	 * @param exact
	 *            If true then only return the resource that is registered for the given locale and
	 *            style.
	 * 
	 * @return The logical resource
	 */
	public final Resource get(final Class<?> scope, final String name, final Locale locale,
		final String style, final String variation, boolean exact)
	{
		if (exact)
		{
			final String resourceKey = resourceKey(scope, name, locale, style, variation);
			return get(resourceKey);
		}

		for (int i = 7; i >= 0; i--)
		{
			boolean testLocale = (i & 4) != 0;
			boolean testStyle = (i & 2) != 0;
			boolean testVariation = (i & 1) != 0;

			String resourceKey = resourceKey(scope, name, (testLocale ? locale : null), (testStyle
				? style : null), (testVariation ? variation : null));
			Resource resource = get(resourceKey);
			if (resource != null)
			{
				return resource;
			}
		}

		return null;
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT USE IT.
	 * 
	 * @param key
	 *            Shared resource key
	 * @return The resource
	 */
	public final Resource get(final String key)
	{
		return resourceMap.get(key);
	}

	/**
	 * Sets an alias for a class so that a resource url can look like: resources/images/Image.jpg
	 * instead of resources/org.apache.wicket.resources.ResourceClass/Image.jpg
	 * 
	 * @param clz
	 *            The class that has to be aliased.
	 * @param alias
	 *            The alias string.
	 */
	public final void putClassAlias(Class<?> clz, String alias)
	{
		classAliasMap.put(clz, alias);
		aliasClassMap.put(alias, new WeakReference<Class<?>>(clz));
	}

	/**
	 * Gets the class for a given resource alias.
	 * 
	 * @param alias
	 * @return The class this is an alias for.
	 * @see #putClassAlias(Class, String)
	 */
	public final Class<?> getAliasClass(String alias)
	{
		WeakReference<Class<?>> classRef = aliasClassMap.get(alias);
		if (classRef == null)
		{
			return null;
		}
		return classRef.get();
	}

	/**
	 * Removes a shared resource.
	 * 
	 * @param key
	 *            Shared resource key
	 */
	public final void remove(final String key)
	{
		resourceMap.remove(key);
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT CALL IT.
	 * 
	 * @param scope
	 *            The scope of the resource
	 * @param path
	 *            The resource path
	 * @param locale
	 *            The locale
	 * @param style
	 *            The style (see {@link org.apache.wicket.Session})
	 * @param variation
	 *            The component specific variation of style
	 * @return The localized path
	 */
	public String resourceKey(final Class<?> scope, final String path, final Locale locale,
		final String style, final String variation)
	{
		String alias = classAliasMap.get(scope);
		if (alias == null)
		{
			alias = scope.getName();
		}
		return alias + '/' + resourceKey(path, locale, style, variation);
	}
}
