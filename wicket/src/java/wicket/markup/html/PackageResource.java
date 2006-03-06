/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Pattern;

import wicket.Application;
import wicket.SharedResources;
import wicket.WicketRuntimeException;
import wicket.util.io.IOUtils;
import wicket.util.lang.PackageName;
import wicket.util.lang.Packages;
import wicket.util.resource.IResourceStream;
import wicket.util.string.Strings;

/**
 * Represents a localizable static resource.
 * <p>
 * Use like eg:
 * 
 * <pre>
 * private static final PackageResource IMG_UNKNOWN = PackageResource.get(EditPage.class,
 * 		&quot;questionmark.gif&quot;);
 * </pre>
 * 
 * where the static resource references image 'questionmark.gif' from the the
 * package that EditPage is in.
 * </p>
 * 
 * @author Jonathan Locke
 */
public class PackageResource extends WebResource
{
	private static final long serialVersionUID = 1L;

	/** The path to the resource */
	public final String absolutePath;

	/** The resource's locale */
	private Locale locale;

	/** The resource's style */
	final String style;

	/** The scoping class, used for class loading and to determine the package. */
	final Class scope;

	/**
	 * Binds a the resource to the given application object Will create the
	 * resource if not already in the shared resources of the application
	 * object.
	 * 
	 * @param application
	 *            The application to bind to.
	 * @param scope
	 *            The scope of the resource.
	 * @param name
	 *            The name of the resource.
	 */
	public static void bind(Application application, Class scope, String name)
	{
		bind(application, scope, name, null, null);
	}

	/**
	 * Binds a the resource to the given application object. Will create the
	 * resource if not already in the shared resources of the application
	 * object.
	 * 
	 * @param application
	 *            The application to bind to.
	 * @param scope
	 *            The scope of the resource.
	 * @param name
	 *            The name of the resource.
	 * @param locale
	 *            The locale of the resource.
	 */
	public static void bind(Application application, Class scope, String name, Locale locale)
	{
		bind(application, scope, name, locale, null);
	}

	/**
	 * Binds a the resource to the given application object. Will create the
	 * resource if not already in the shared resources of the application
	 * object.
	 * 
	 * @param application
	 *            The application to bind to.
	 * @param scope
	 *            The scope of the resource.
	 * @param name
	 *            The name of the resource.
	 * @param locale
	 *            The locale of the resource.
	 * @param style
	 *            The style of the resource.
	 */
	public static void bind(Application application, Class scope, String name, Locale locale,
			String style)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException("argument scope may not be null");
		}
		if (name == null)
		{
			throw new IllegalArgumentException("argument name may not be null");
		}

		// first check on a direct hit for efficiency
		if (exists(scope, name, locale, style))
		{
			// we have got a hit, so we may safely assume the name
			// argument is not a regular expression, and can thus
			// just add the resource and return
			get(scope, name, locale, style);
		}
		else
		{
			// interpret the name argument as a regexp; loop through
			// the resources in the package of the provided scope, and
			// add anything that matches
			Pattern pattern = Pattern.compile(name);
			String packageRef = Strings.replaceAll(PackageName.forClass(scope).getName(), ".", "/");
			ClassLoader loader = scope.getClassLoader();
			try
			{
				// loop through the resources of the package
				Enumeration packageResources = loader.getResources(packageRef);
				while (packageResources.hasMoreElements())
				{
					URL resource = (URL)packageResources.nextElement();
					BufferedReader reader = new BufferedReader(new InputStreamReader(resource
							.openStream()));
					String entry = null;
					try
					{
						while ((entry = reader.readLine()) != null)
						{
							// if the current entry matches the provided regexp
							if (pattern.matcher(entry).matches())
							{
								// we add the entry as a package resource
								get(scope, entry, locale, style);
							}
						}
					}
					finally
					{
						IOUtils.closeQuietly(reader);
					}

				}
			}
			catch (IOException e)
			{
				throw new WicketRuntimeException(e);
			}
		}
	}

	/**
	 * Gets a non-localized resource for a given set of criteria. Only one
	 * resource will be loaded for the same criteria.
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading
	 *            the package resource, and to determine what package it is in.
	 *            Typically this is the calling class/ the class in which you
	 *            call this method
	 * @param path
	 *            The path to the resource
	 * @return The resource
	 */
	public static PackageResource get(final Class scope, final String path)
	{
		return get(scope, path, null, null);
	}

	/**
	 * Gets the resource for a given set of criteria. Only one resource will be
	 * loaded for the same criteria.
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading
	 *            the package resource, and to determine what package it is in.
	 *            Typically this is the class in which you call this method
	 * @param path
	 *            The path to the resource
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The style of the resource (see {@link wicket.Session})
	 * @return The resource
	 */
	public static PackageResource get(final Class scope, final String path, final Locale locale,
			final String style)
	{
		final SharedResources sharedResources = Application.get().getSharedResources();
		PackageResource resource = (PackageResource)sharedResources.get(scope, path, locale, style,
				true);
		if (resource == null)
		{
			resource = new PackageResource(scope, path, locale, style);
			sharedResources.add(scope, path, locale, style, resource);
		}
		return resource;
	}

	/**
	 * Gets whether a resource for a given set of criteria exists.
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading
	 *            the package resource, and to determine what package it is in.
	 *            Typically this is the class in which you call this method
	 * @param path
	 *            The path to the resource
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The style of the resource (see {@link wicket.Session})
	 * @return true if a resource could be loaded, false otherwise
	 */
	public static boolean exists(final Class scope, final String path, final Locale locale,
			final String style)
	{
		String absolutePath = Packages.absolutePath(scope, path);
		return Application.get().getResourceSettings().getResourceStreamLocator().locate(scope,
				absolutePath, style, locale, null) != null;
	}

	/**
	 * Hidden constructor.
	 * 
	 * @param scope
	 *            This argument will be used to get the class loader for loading
	 *            the package resource, and to determine what package it is in
	 * @param path
	 *            The path to the resource
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The style of the resource
	 */
	private PackageResource(final Class scope, final String path, final Locale locale,
			final String style)
	{
		// Convert resource path to absolute path relative to base package
		this.absolutePath = Packages.absolutePath(scope, path);
		this.scope = scope;
		this.locale = locale;
		this.style = style;

		if (locale != null)
		{
			// Get the resource stream so that the real locale that could be
			// resolved is set.
			getResourceStream();

			// Invalidate it again so that it won't hold up resources
			invalidate();
		}
	}

	/**
	 * @return Gets the resource for the component.
	 */
	public IResourceStream getResourceStream()
	{
		// Locate resource
		IResourceStream resourceStream = Application.get().getResourceSettings()
				.getResourceStreamLocator().locate(scope, absolutePath, style, locale, null);

		// Check that resource was found
		if (resourceStream == null)
		{
			throw new WicketRuntimeException("Unable to find package resource [path = "
					+ absolutePath + ", style = " + style + ", locale = " + locale + "]");
		}
		this.locale = resourceStream.getLocale();
		return resourceStream;
	}

	/**
	 * @return The Locale of this package resource
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * Get the absolute path of the resource
	 * 
	 * @return the resource path
	 */
	public String getAbsolutePath()
	{
		return absolutePath;
	}
}
