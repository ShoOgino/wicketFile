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

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.AbortException;
import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.SharedResources;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.settings.IResourceSettings;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.util.lang.Packages;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Time;
import org.apache.wicket.util.watch.IModifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a localizable static resource.
 * <p>
 * Use like eg:
 * 
 * <pre>
 * PackageResource IMG_UNKNOWN = PackageResource.get(EditPage.class, &quot;questionmark.gif&quot;);
 * </pre>
 * 
 * where the static resource references image 'questionmark.gif' from the the package that EditPage
 * is in to get a package resource.
 * </p>
 * 
 * Access to resources can be granted or denied via a {@link IPackageResourceGuard}. Please see
 * {@link IResourceSettings#getPackageResourceGuard()} as well.
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 * @author Juergen Donnerstag
 */
public class PackageResource extends WebResource implements IModifiable, IPackageResourceGuard
{
	/**
	 * Exception thrown when the creation of a package resource is not allowed.
	 */
	public static final class PackageResourceBlockedException extends WicketRuntimeException
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param message
		 */
		public PackageResourceBlockedException(String message)
		{
			super(message);
		}
	}

	/** log. */
	private static final Logger log = LoggerFactory.getLogger(PackageResource.class);

	private static final long serialVersionUID = 1L;

	/**
	 * Binds a resource to the given application object. Will create the resource if not already in
	 * the shared resources of the application object.
	 * 
	 * @param application
	 *            The application to bind to.
	 * @param scope
	 *            The scope of the resource.
	 * @param name
	 *            The name of the resource (like &quot;myfile.js&quot;)
	 * @throw IllegalArgumentException when the requested package resource was not found
	 */
	public static void bind(Application application, Class<?> scope, String name)
	{
		bind(application, scope, name, null, null, null);
	}

	/**
	 * Binds a resource to the given application object. Will create the resource if not already in
	 * the shared resources of the application object.
	 * 
	 * @param application
	 *            The application to bind to.
	 * @param scope
	 *            The scope of the resource.
	 * @param name
	 *            The name of the resource (like &quot;myfile.js&quot;)
	 * @param locale
	 *            The locale of the resource.
	 * @throw IllegalArgumentException when the requested package resource was not found
	 */
	public static void bind(Application application, Class<?> scope, String name, Locale locale)
	{
		bind(application, scope, name, locale, null, null);
	}

	/**
	 * Binds a resource to the given application object. Will create the resource if not already in
	 * the shared resources of the application object.
	 * 
	 * @param application
	 *            The application to bind to.
	 * @param scope
	 *            The scope of the resource.
	 * @param name
	 *            The name of the resource (like &quot;myfile.js&quot;)
	 * @param locale
	 *            The locale of the resource.
	 * @param style
	 *            The style of the resource.
	 * @param variation
	 *            The component's variation (of the style)
	 * @throw IllegalArgumentException when the requested package resource was not found
	 */
	public static void bind(Application application, Class<?> scope, String name, Locale locale,
		String style, final String variation)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("argument name may not be null");
		}

		// first check on a direct hit for efficiency
		if (exists(scope, name, locale, style, variation))
		{
			// we have got a hit, so we may safely assume the name
			// argument is not a regular expression, and can thus
			// just add the resource and return
			PackageResource packageResource = get(scope, name, locale, style, variation);
			SharedResources sharedResources = Application.get().getSharedResources();
			sharedResources.add(scope, name, locale, style, variation, packageResource);
		}
		else
		{
			throw new IllegalArgumentException("no package resource was found for scope " + scope +
				", name " + name + ", locale " + locale + ", style " + style + ", variation " +
				variation);
		}
	}

	/**
	 * Gets whether a resource for a given set of criteria exists.
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading the package
	 *            resource, and to determine what package it is in. Typically this is the class in
	 *            which you call this method
	 * @param path
	 *            The path to the resource
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The style of the resource (see {@link org.apache.wicket.Session})
	 * @param variation
	 *            The component's variation (of the style)
	 * @return true if a resource could be loaded, false otherwise
	 */
	public static boolean exists(final Class<?> scope, final String path, final Locale locale,
		final String style, final String variation)
	{
		String absolutePath = Packages.absolutePath(scope, path);
		return Application.get().getResourceSettings().getResourceStreamLocator().locate(scope,
			absolutePath, style, variation, locale, null) != null;
	}

	/**
	 * Gets a non-localized resource for a given set of criteria. Only one resource will be loaded
	 * for the same criteria.
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading the package
	 *            resource, and to determine what package it is in. Typically this is the calling
	 *            class/ the class in which you call this method
	 * @param path
	 *            The path to the resource
	 * @return The resource
	 */
	public static PackageResource get(final Class<?> scope, final String path)
	{
		return get(scope, path, null, null, null);
	}

	/**
	 * Gets the resource for a given set of criteria. Only one resource will be loaded for the same
	 * criteria.
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading the package
	 *            resource, and to determine what package it is in. Typically this is the class in
	 *            which you call this method
	 * @param path
	 *            The path to the resource
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The style of the resource (see {@link org.apache.wicket.Session})
	 * @param variation
	 *            The compoent's variation (of the style)
	 * @return The resource
	 */
	public static PackageResource get(final Class<?> scope, final String path, final Locale locale,
		final String style, final String variation)
	{
		final SharedResources sharedResources = Application.get().getSharedResources();
		PackageResource resource = (PackageResource)sharedResources.get(scope, path, locale, style,
			variation, true);
		if (resource == null)
		{
			resource = newPackageResource(scope, path, locale, style, variation);
			Application.get().getSharedResources().add(scope, path, locale, style, variation,
				resource);
		}
		return resource;
	}

	/**
	 * Create a new PackageResource
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading the package
	 *            resource, and to determine what package it is in. Typically this is the class in
	 *            which you call this method
	 * @param path
	 *            The path to the resource
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The style of the resource (see {@link org.apache.wicket.Session})
	 * @param variation
	 *            The compoent's variation (of the style)
	 * @return The resource
	 */
	protected static PackageResource newPackageResource(final Class<?> scope, final String path,
		final Locale locale, final String style, final String variation)
	{
		return new PackageResource(scope, path, locale, style, variation);
	}

	/* removed in 2.0 */
	private static void scanJarFile(Class<?> scope, Pattern pattern, boolean recurse,
		final List<PackageResource> resources, String packageRef, JarFile jf)
	{
		Enumeration<JarEntry> enumeration = jf.entries();
		while (enumeration.hasMoreElements())
		{
			JarEntry je = enumeration.nextElement();
			String name = je.getName();
			if (name.startsWith(packageRef))
			{
				name = name.substring(packageRef.length() + 1);
				if (pattern.matcher(name).matches() && (recurse || (name.indexOf('/') == -1)))
				{
					// we add the entry as a package resource
					resources.add(get(scope, name, null, null, null));
				}
			}
		}
	}

	/** The path to the resource */
	private final String absolutePath;

	/** The resource's locale */
	private Locale locale;

	/** The path this resource was created with. */
	private final String path;

	/** The scoping class, used for class loading and to determine the package. */
	private final String scopeName;

	/** The resource's style */
	private final String style;

	/** The component's variation (of the style) */
	private final String variation;

	/**
	 * Hidden constructor.
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading the package
	 *            resource, and to determine what package it is in
	 * @param path
	 *            The path to the resource
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The style of the resource
	 * @param variation
	 *            The component's variation (of the style)
	 */
	protected PackageResource(final Class<?> scope, final String path, final Locale locale,
		final String style, final String variation)
	{
		// Convert resource path to absolute path relative to base package
		absolutePath = Packages.absolutePath(scope, path);

		if (!accept(scope, path))
		{
			throw new PackageResourceBlockedException(
				"Access denied to (static) package resource " + absolutePath +
					". See IPackageResourceGuard");
		}

		scopeName = scope.getName();
		this.path = path;
		this.locale = locale;
		this.style = style;
		this.variation = variation;

		if (locale != null)
		{
			/*
			 * Get the resource stream so that the real locale that could be resolved is set.
			 * Silently ignore not resolvable resources as we are not serving the resource for now
			 */
			getResourceStream(false);

			// Invalidate it again so that it won't hold up resources
			invalidate();
		}
	}

	/**
	 * Gets the absolute path of the resource.
	 * 
	 * @return the absolute resource path
	 */
	public final String getAbsolutePath()
	{
		return absolutePath;
	}

	/**
	 * Gets the locale.
	 * 
	 * @return The Locale of this package resource
	 */
	public final Locale getLocale()
	{
		return locale;
	}

	/**
	 * Gets the path this resource was created with.
	 * 
	 * @return the path
	 */
	public final String getPath()
	{
		return path;
	}

	/**
	 * @return Gets the resource for the component.
	 */
	@Override
	public IResourceStream getResourceStream()
	{
		return getResourceStream(true);
	}

	/**
	 * @return Gets the resource for the component.
	 * @param failOnError
	 *            throw an AbortException when resource does not exist
	 */
	public IResourceStream getResourceStream(boolean failOnError)
	{
		// Locate resource
		IResourceStream resourceStream = Application.get()
			.getResourceSettings()
			.getResourceStreamLocator()
			.locate(getScope(), absolutePath, style, variation, locale, null);

		// Check that resource was found
		if (resourceStream == null)
		{
			if (!failOnError)
			{
				// Do not abort the request, as we are not yet serving the resource
				return null;
			}

			String msg = "Unable to find package resource [path = " + absolutePath + ", style = " +
				style + ", variation = " + variation + ", locale = " + locale + "]";
			log.warn(msg);

			if (RequestCycle.get() instanceof WebRequestCycle)
			{
				throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_NOT_FOUND, msg);
			}
			else
			{
				throw new AbortException();
			}
		}

		locale = resourceStream.getLocale();

		if (resourceStream != null)
		{
			lastModifiedTime = resourceStream.lastModifiedTime();
			lastModifiedTimeUpdate = System.currentTimeMillis();
		}

		return resourceStream;
	}

	/**
	 * Gets the scoping class, used for class loading and to determine the package.
	 * 
	 * @return the scoping class
	 */
	public final Class<?> getScope()
	{
		return Classes.resolveClass(scopeName);
	}

	/**
	 * Gets the style.
	 * 
	 * @return the style
	 */
	public final String getStyle()
	{
		return style;
	}

	private transient Time lastModifiedTime = null;
	private transient long lastModifiedTimeUpdate = 0;

	/**
	 * Returns the last modified time of resource
	 * 
	 * @return last modified time or null if the time can not be determined
	 */
	public Time lastModifiedTime()
	{
		if (lastModifiedTimeUpdate == 0 ||
			lastModifiedTimeUpdate < System.currentTimeMillis() - 5 * (1000 * 60))
		{
			lastModifiedTime = getResourceStream().lastModifiedTime();
			lastModifiedTimeUpdate = System.currentTimeMillis();
		}
		return lastModifiedTime;

	}

	/**
	 * @see org.apache.wicket.markup.html.IPackageResourceGuard#accept(java.lang.Class,
	 *      java.lang.String)
	 */
	public boolean accept(Class<?> scope, String path)
	{
		IPackageResourceGuard guard = Application.get()
			.getResourceSettings()
			.getPackageResourceGuard();

		return guard.accept(scope, path);
	}
}
