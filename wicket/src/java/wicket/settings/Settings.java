/*
 * $Id$ $Revision$
 * $Date$
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
package wicket.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wicket.Application;
import wicket.IPageFactory;
import wicket.IResourceFactory;
import wicket.IResponseFilter;
import wicket.Localizer;
import wicket.Page;
import wicket.application.DefaultClassResolver;
import wicket.application.IClassResolver;
import wicket.authorization.IAuthorizationStrategy;
import wicket.markup.IMarkupParserFactory;
import wicket.markup.MarkupParserFactory;
import wicket.markup.html.form.persistence.CookieValuePersisterSettings;
import wicket.markup.html.form.validation.DefaultValidatorResourceKeyFactory;
import wicket.markup.html.form.validation.IValidatorResourceKeyFactory;
import wicket.markup.resolver.AutoComponentResolver;
import wicket.markup.resolver.IComponentResolver;
import wicket.protocol.http.HttpSessionStoreFactory;
import wicket.resource.PropertiesFactory;
import wicket.resource.loader.ApplicationStringResourceLoader;
import wicket.resource.loader.ComponentStringResourceLoader;
import wicket.resource.loader.IStringResourceLoader;
import wicket.session.DefaultPageFactory;
import wicket.session.ISessionStoreFactory;
import wicket.session.pagemap.IPageMapEvictionStrategy;
import wicket.session.pagemap.LeastRecentlyAccessedEvictionStrategy;
import wicket.util.convert.ConverterFactory;
import wicket.util.convert.IConverterFactory;
import wicket.util.crypt.CachingSunJceCryptFactory;
import wicket.util.crypt.ICryptFactory;
import wicket.util.file.IResourceFinder;
import wicket.util.file.IResourcePath;
import wicket.util.file.Path;
import wicket.util.lang.EnumeratedType;
import wicket.util.resource.locator.DefaultResourceStreamLocator;
import wicket.util.resource.locator.ResourceStreamLocator;
import wicket.util.time.Duration;
import wicket.util.watch.ModificationWatcher;

/**
 * Contains settings exposed via IXXXSettings interfaces.
 * 
 * @author Jonathan Locke
 * @author Chris Turner
 * @author Eelco Hillenius
 * @author Juergen Donnerstag
 * @author Johan Compagner
 * @author Igor Vaynberg (ivaynberg)
 */
public final class Settings
		implements
			IApplicationSettings,
			IDebugSettings,
			IExceptionSettings,
			IMarkupSettings,
			IPageSettings,
			IRequestCycleSettings,
			IResourceSettings,
			ISecuritySettings,
			ISessionSettings
{
	/**
	 * If true, wicket tags ( <wicket: ..>) and wicket:id attributes we be
	 * removed from output
	 */
	boolean stripWicketTags = false;

	/** In order to remove <?xml?> from output as required by IE quirks mode */
	boolean stripXmlDeclarationFromOutput;

	/** Class of access denied page. */
	private Class accessDeniedPage;

	/** The application */
	private Application application;

	/** the authorization strategy. */
	private IAuthorizationStrategy authorizationStrategy = IAuthorizationStrategy.ALLOW_ALL;

	/** Application default for automatically resolving hrefs */
	private boolean automaticLinking = false;

	/** True if the response should be buffered */
	private boolean bufferResponse = true;

	/** class resolver to find classes */
	private IClassResolver classResolver = new DefaultClassResolver();

	/** List of (static) ComponentResolvers */
	private List componentResolvers = new ArrayList();

	/** True to check that each component on a page is used */
	private boolean componentUseCheck = true;

	/** True if multiple tabs/spaces should be compressed to a single space */
	private boolean compressWhitespace = false;

	/**
	 * Factory for the converter instance; default to the non localized factory
	 * {@link ConverterFactory}.
	 */
	private IConverterFactory converterFactory = new ConverterFactory();

	/** Default values for persistence of form data (by means of cookies) */
	private CookieValuePersisterSettings cookieValuePersisterSettings = new CookieValuePersisterSettings();

	/** facotry for creating crypt objects */
	private ICryptFactory cryptFactory;

	/** Default markup for after a disabled link */
	private String defaultAfterDisabledLink = "</em>";

	/** Default markup for before a disabled link */
	private String defaultBeforeDisabledLink = "<em>";

	/** The default locale to use */
	private Locale defaultLocale = Locale.getDefault();

	/** Default markup encoding. If null, the OS default will be used */
	private String defaultMarkupEncoding;

	/** Class of internal error page. */
	private Class internalErrorPage;

	/** I18N support */
	private Localizer localizer;

	/** factory for creating markup parsers */
	private IMarkupParserFactory markupParserFactory = new MarkupParserFactory(this);

	/** The maximum number of versions of a page to track */
	private int maxPageVersions = 10;

	/** Map to look up resource factories by name */
	private Map nameToResourceFactory = new HashMap();

	/** True if string resource loaders have been overridden */
	private boolean overriddenStringResourceLoaders = false;

	/** The error page displayed when an expired page is accessed. */
	private Class pageExpiredErrorPage;

	/** factory to create new Page objects */
	private IPageFactory pageFactory = new DefaultPageFactory();

	/** The eviction strategy. */
	private IPageMapEvictionStrategy pageMapEvictionStrategy = new LeastRecentlyAccessedEvictionStrategy(
			15);

	/** The factory to be used for the property files */
	private PropertiesFactory propertiesFactory;

	/**
	 * The render strategy, defaults to 'REDIRECT_TO_BUFFER'. This property
	 * influences the default way in how a logical request that consists of an
	 * 'action' and a 'render' part is handled, and is mainly used to have a
	 * means to circumvent the 'refresh' problem.
	 */
	private RenderStrategy renderStrategy = REDIRECT_TO_BUFFER;

	/** Filesystem Path to search for resources */
	private IResourceFinder resourceFinder = new Path();

	/** Frequency at which files should be polled */
	private Duration resourcePollFrequency = null;

	/** resource locator for this application */
	private ResourceStreamLocator resourceStreamLocator;

	/** ModificationWatcher to watch for changes in markup files */
	private ModificationWatcher resourceWatcher;


	/**
	 * List of {@link IResponseFilter}s.
	 */
	// TODO revisit... I don't think everyone agrees with
	// this (e.g. why not use servlet filters to acchieve the same)
	// and if we want to support this, it could more elegantly
	// be made part of e.g. request targets or a request processing
	// strategy
	private List responseFilters;

	/**
	 * In order to do proper form parameter decoding it is important that the
	 * response and the following request have the same encoding. see
	 * http://www.crazysquirrel.com/computing/general/form-encoding.jspx for
	 * additional information.
	 */
	private String responseRequestEncoding = "UTF-8";

	/** the session store factory. */
	private ISessionStoreFactory sessionStoreFactory = new HttpSessionStoreFactory();

	/** Chain of string resource loaders to use */
	private List stringResourceLoaders = new ArrayList(4);

	/** Should HTML comments be stripped during rendering? */
	private boolean stripComments = false;

	/** Flags used to determine how to behave if resources are not found */
	private boolean throwExceptionOnMissingResource = true;

	/** Type of handling for unexpected exceptions */
	private UnexpectedExceptionDisplay unexpectedExceptionDisplay = SHOW_EXCEPTION_PAGE;

	/** Determines behavior of string resource loading if string is missing */
	private boolean useDefaultOnMissingResource = true;

	/** Factory for producing validator error message resource keys */
	private IValidatorResourceKeyFactory validatorResourceKeyFactory = new DefaultValidatorResourceKeyFactory();

	/** Determines if pages should be managed by a version manager by default */
	private boolean versionPagesByDefault = true;

	/**
	 * Enumerated type for different ways of handling the render part of
	 * requests.
	 */
	public static class RenderStrategy extends EnumeratedType
	{
		private static final long serialVersionUID = 1L;

		RenderStrategy(final String name)
		{
			super(name);
		}
	}


	/**
	 * Create the application settings, carrying out any necessary
	 * initialisations.
	 * 
	 * @param application
	 *            The application that these settings are for
	 */
	public Settings(final Application application)
	{
		this.application = application;
		stringResourceLoaders.add(new ComponentStringResourceLoader(application));
		stringResourceLoaders.add(new ApplicationStringResourceLoader(application));
	}

	/**
	 * @see wicket.settings.IPageSettings#addComponentResolver(wicket.markup.resolver.IComponentResolver)
	 */
	public void addComponentResolver(IComponentResolver resolver)
	{
		componentResolvers.add(resolver);
	}

	/**
	 * @see wicket.settings.IResourceSettings#addResourceFactory(java.lang.String,
	 *      wicket.IResourceFactory)
	 */
	public void addResourceFactory(final String name, IResourceFactory resourceFactory)
	{
		nameToResourceFactory.put(name, resourceFactory);
	}

	/**
	 * @see wicket.settings.IResourceSettings#addResourceFolder(java.lang.String)
	 */
	public void addResourceFolder(final String resourceFolder)
	{
		// Get resource finder
		final IResourceFinder finder = getResourceFinder();

		// Make sure it's a path
		if (!(finder instanceof IResourcePath))
		{
			throw new IllegalArgumentException(
					"To add a resource folder, the application's resource finder must be an instance of IResourcePath");
		}

		// Cast to resource path and add folder
		final IResourcePath path = (IResourcePath)finder;
		path.add(resourceFolder);
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#addResponseFilter(wicket.IResponseFilter)
	 */
	public void addResponseFilter(IResponseFilter responseFilter)
	{
		if (responseFilters == null)
		{
			responseFilters = new ArrayList(3);
		}
		responseFilters.add(responseFilter);
	}

	/**
	 * @see wicket.settings.IResourceSettings#addStringResourceLoader(wicket.resource.loader.IStringResourceLoader)
	 */
	public void addStringResourceLoader(final IStringResourceLoader loader)
	{
		if (!overriddenStringResourceLoaders)
		{
			stringResourceLoaders.clear();
			overriddenStringResourceLoaders = true;
		}
		stringResourceLoaders.add(loader);
	}

	/**
	 * @see wicket.settings.IApplicationSettings#getAccessDeniedPage()
	 */
	public Class getAccessDeniedPage()
	{
		return accessDeniedPage;
	}

	/**
	 * @see wicket.settings.ISecuritySettings#getAuthorizationStrategy()
	 */
	public IAuthorizationStrategy getAuthorizationStrategy()
	{
		return authorizationStrategy;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getAutomaticLinking()
	 */
	public boolean getAutomaticLinking()
	{
		return automaticLinking;
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#getBufferResponse()
	 */
	public boolean getBufferResponse()
	{
		return bufferResponse;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#getClassResolver()
	 */
	public IClassResolver getClassResolver()
	{
		return classResolver;
	}

	/**
	 * Get the (modifiable) list of IComponentResolvers.
	 * 
	 * @see AutoComponentResolver for an example
	 * @return List of ComponentResolvers
	 */
	public List getComponentResolvers()
	{
		return componentResolvers;
	}

	/**
	 * @see wicket.settings.IDebugSettings#getComponentUseCheck()
	 */
	public boolean getComponentUseCheck()
	{
		return this.componentUseCheck;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getCompressWhitespace()
	 */
	public boolean getCompressWhitespace()
	{
		return compressWhitespace;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#getConverterFactory()
	 */
	public IConverterFactory getConverterFactory()
	{
		return converterFactory;
	}

	/**
	 * @see wicket.settings.ISecuritySettings#getCookieValuePersisterSettings()
	 */
	public CookieValuePersisterSettings getCookieValuePersisterSettings()
	{
		return cookieValuePersisterSettings;
	}

	/**
	 * @see wicket.settings.ISecuritySettings#getCryptFactory()
	 */
	public synchronized ICryptFactory getCryptFactory()
	{
		if (cryptFactory == null)
		{
			cryptFactory = new CachingSunJceCryptFactory(ISecuritySettings.DEFAULT_ENCRYPTION_KEY);
		}
		return cryptFactory;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getDefaultAfterDisabledLink()
	 */
	public String getDefaultAfterDisabledLink()
	{
		return defaultAfterDisabledLink;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getDefaultBeforeDisabledLink()
	 */
	public String getDefaultBeforeDisabledLink()
	{
		return defaultBeforeDisabledLink;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#getDefaultLocale()
	 */
	public Locale getDefaultLocale()
	{
		return defaultLocale;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getDefaultMarkupEncoding()
	 */
	public String getDefaultMarkupEncoding()
	{
		return defaultMarkupEncoding;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#getInternalErrorPage()
	 */
	public Class getInternalErrorPage()
	{
		return internalErrorPage;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getLocalizer()
	 */
	public Localizer getLocalizer()
	{
		if (localizer == null)
		{
			this.localizer = new Localizer(application);
		}
		return localizer;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getMarkupParserFactory()
	 */
	public IMarkupParserFactory getMarkupParserFactory()
	{
		return markupParserFactory;
	}

	/**
	 * @see wicket.settings.IPageSettings#getMaxPageVersions()
	 */
	public int getMaxPageVersions()
	{
		return maxPageVersions;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#getPageExpiredErrorPage()
	 */
	public Class getPageExpiredErrorPage()
	{
		return pageExpiredErrorPage;
	}

	/**
	 * @see wicket.settings.ISessionSettings#getPageFactory()
	 */
	public IPageFactory getPageFactory()
	{
		return pageFactory;
	}

	/**
	 * @see wicket.settings.ISessionSettings#getPageMapEvictionStrategy()
	 */
	public IPageMapEvictionStrategy getPageMapEvictionStrategy()
	{
		return pageMapEvictionStrategy;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getPropertiesFactory()
	 */
	public PropertiesFactory getPropertiesFactory()
	{
		if (propertiesFactory == null)
		{
			propertiesFactory = new PropertiesFactory();
		}
		return propertiesFactory;
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#getRenderStrategy()
	 */
	public RenderStrategy getRenderStrategy()
	{
		return renderStrategy;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getResourceFactory(java.lang.String)
	 */
	public IResourceFactory getResourceFactory(final String name)
	{
		return (IResourceFactory)nameToResourceFactory.get(name);
	}

	/**
	 * @see wicket.settings.IResourceSettings#getResourceFinder()
	 */
	public IResourceFinder getResourceFinder()
	{
		return resourceFinder;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getResourcePollFrequency()
	 */
	public Duration getResourcePollFrequency()
	{
		return resourcePollFrequency;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getResourceStreamLocator()
	 */
	public ResourceStreamLocator getResourceStreamLocator()
	{
		if (resourceStreamLocator == null)
		{
			// Create compound resource locator using source path from
			// application settings
			resourceStreamLocator = new DefaultResourceStreamLocator(getResourceFinder());
		}
		return resourceStreamLocator;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getResourceWatcher()
	 */
	public ModificationWatcher getResourceWatcher()
	{
		if (resourceWatcher == null)
		{
			final Duration pollFrequency = getResourcePollFrequency();
			if (pollFrequency != null)
			{
				resourceWatcher = new ModificationWatcher(pollFrequency);
			}
		}
		return resourceWatcher;
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#getResponseFilters()
	 */
	public List getResponseFilters()
	{
		if (responseFilters == null)
		{
			return null;
		}
		else
		{
			return Collections.unmodifiableList(responseFilters);
		}
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#getResponseRequestEncoding()
	 */
	public String getResponseRequestEncoding()
	{
		return responseRequestEncoding;
	}

	/**
	 * @see wicket.settings.ISessionSettings#getSessionStoreFactory()
	 */
	public ISessionStoreFactory getSessionStoreFactory()
	{
		return sessionStoreFactory;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getStringResourceLoaders()
	 */
	public List getStringResourceLoaders()
	{
		return Collections.unmodifiableList(stringResourceLoaders);
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getStripComments()
	 */
	public boolean getStripComments()
	{
		return stripComments;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getStripWicketTags()
	 */
	public boolean getStripWicketTags()
	{
		return this.stripWicketTags;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#getStripXmlDeclarationFromOutput()
	 */
	public boolean getStripXmlDeclarationFromOutput()
	{
		return this.stripXmlDeclarationFromOutput;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getThrowExceptionOnMissingResource()
	 */
	public boolean getThrowExceptionOnMissingResource()
	{
		return throwExceptionOnMissingResource;
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#getUnexpectedExceptionDisplay()
	 */
	public UnexpectedExceptionDisplay getUnexpectedExceptionDisplay()
	{
		return unexpectedExceptionDisplay;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getUseDefaultOnMissingResource()
	 */
	public boolean getUseDefaultOnMissingResource()
	{
		return useDefaultOnMissingResource;
	}

	/**
	 * @see wicket.settings.IResourceSettings#getValidatorResourceKeyFactory()
	 */
	public IValidatorResourceKeyFactory getValidatorResourceKeyFactory()
	{
		return validatorResourceKeyFactory;
	}

	/**
	 * @see wicket.settings.IPageSettings#getVersionPagesByDefault()
	 */
	public boolean getVersionPagesByDefault()
	{
		return versionPagesByDefault;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#setAccessDeniedPage(java.lang.Class)
	 */
	public void setAccessDeniedPage(Class accessDeniedPage)
	{
		if (accessDeniedPage == null)
		{
			throw new IllegalArgumentException("Argument accessDeniedPage may not be null");
		}
		checkPageClass(accessDeniedPage);

		this.accessDeniedPage = accessDeniedPage;
	}

	/**
	 * @see wicket.settings.ISecuritySettings#setAuthorizationStrategy(wicket.authorization.IAuthorizationStrategy)
	 */
	public void setAuthorizationStrategy(IAuthorizationStrategy strategy)
	{
		if (strategy == null)
		{
			throw new IllegalArgumentException("authorization strategy cannot be set to null");
		}
		this.authorizationStrategy = strategy;
	}


	/**
	 * @see wicket.settings.IMarkupSettings#setAutomaticLinking(boolean)
	 */
	public void setAutomaticLinking(boolean automaticLinking)
	{
		this.automaticLinking = automaticLinking;
	}


	/**
	 * @see wicket.settings.IRequestCycleSettings#setBufferResponse(boolean)
	 */
	public void setBufferResponse(boolean bufferResponse)
	{
		this.bufferResponse = bufferResponse;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#setClassResolver(wicket.application.IClassResolver)
	 */
	public IPageSettings setClassResolver(final IClassResolver defaultClassResolver)
	{
		this.classResolver = defaultClassResolver;
		return this;
	}

	/**
	 * @see wicket.settings.IDebugSettings#setComponentUseCheck(boolean)
	 */
	public void setComponentUseCheck(final boolean componentUseCheck)
	{
		this.componentUseCheck = componentUseCheck;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#setCompressWhitespace(boolean)
	 */
	public void setCompressWhitespace(final boolean compressWhitespace)
	{
		this.compressWhitespace = compressWhitespace;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#setConverterFactory(wicket.util.convert.IConverterFactory)
	 */
	public void setConverterFactory(IConverterFactory factory)
	{
		if (factory == null)
		{
			throw new IllegalArgumentException("converter factory cannot be set to null");
		}
		this.converterFactory = factory;
	}

	/**
	 * @see wicket.settings.ISecuritySettings#setCookieValuePersisterSettings(wicket.markup.html.form.persistence.CookieValuePersisterSettings)
	 */
	public void setCookieValuePersisterSettings(
			CookieValuePersisterSettings cookieValuePersisterSettings)
	{
		this.cookieValuePersisterSettings = cookieValuePersisterSettings;
	}

	/**
	 * @see wicket.settings.ISecuritySettings#setCryptFactory(wicket.util.crypt.ICryptFactory)
	 */
	public void setCryptFactory(ICryptFactory cryptFactory)
	{
		if (cryptFactory == null)
		{
			throw new IllegalArgumentException("cryptFactory cannot be null");
		}
		this.cryptFactory = cryptFactory;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#setDefaultAfterDisabledLink(java.lang.String)
	 */
	public void setDefaultAfterDisabledLink(final String defaultAfterDisabledLink)
	{
		this.defaultAfterDisabledLink = defaultAfterDisabledLink;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#setDefaultBeforeDisabledLink(java.lang.String)
	 */
	public void setDefaultBeforeDisabledLink(String defaultBeforeDisabledLink)
	{
		this.defaultBeforeDisabledLink = defaultBeforeDisabledLink;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#setDefaultLocale(java.util.Locale)
	 */
	public void setDefaultLocale(Locale defaultLocale)
	{
		this.defaultLocale = defaultLocale;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#setDefaultMarkupEncoding(java.lang.String)
	 */
	public void setDefaultMarkupEncoding(final String encoding)
	{
		this.defaultMarkupEncoding = encoding;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#setInternalErrorPage(java.lang.Class)
	 */
	public void setInternalErrorPage(final Class internalErrorPage)
	{
		if (internalErrorPage == null)
		{
			throw new IllegalArgumentException("Argument internalErrorPage may not be null");
		}
		checkPageClass(internalErrorPage);

		this.internalErrorPage = internalErrorPage;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#setMarkupParserFactory(wicket.markup.IMarkupParserFactory)
	 */
	public void setMarkupParserFactory(IMarkupParserFactory factory)
	{
		if (factory == null)
		{
			throw new IllegalArgumentException("markup parser factory cannot be null");
		}

		this.markupParserFactory = factory;
	}

	/**
	 * @see wicket.settings.IPageSettings#setMaxPageVersions(int)
	 */
	public void setMaxPageVersions(int maxPageVersions)
	{
		if (maxPageVersions < 0)
		{
			throw new IllegalArgumentException("Value for maxPageVersions must be >= 0");
		}
		this.maxPageVersions = maxPageVersions;
	}

	/**
	 * @see wicket.settings.IApplicationSettings#setPageExpiredErrorPage(java.lang.Class)
	 */
	public void setPageExpiredErrorPage(final Class pageExpiredErrorPage)
	{
		if (pageExpiredErrorPage == null)
		{
			throw new IllegalArgumentException("Argument pageExpiredErrorPage may not be null");
		}
		checkPageClass(pageExpiredErrorPage);

		this.pageExpiredErrorPage = pageExpiredErrorPage;
	}

	/**
	 * @see wicket.settings.ISessionSettings#setPageFactory(wicket.IPageFactory)
	 */
	public void setPageFactory(final IPageFactory defaultPageFactory)
	{
		this.pageFactory = defaultPageFactory;
	}

	/**
	 * @see wicket.settings.ISessionSettings#setPageMapEvictionStrategy(wicket.session.pagemap.IPageMapEvictionStrategy)
	 */
	public void setPageMapEvictionStrategy(IPageMapEvictionStrategy pageMapEvictionStrategy)
	{
		this.pageMapEvictionStrategy = pageMapEvictionStrategy;
	}

	/**
	 * @see wicket.settings.IResourceSettings#setPropertiesFactory(wicket.resource.PropertiesFactory)
	 */
	public void setPropertiesFactory(PropertiesFactory factory)
	{
		this.propertiesFactory = factory;
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#setRenderStrategy(wicket.settings.Settings.RenderStrategy)
	 */
	public void setRenderStrategy(RenderStrategy renderStrategy)
	{
		this.renderStrategy = renderStrategy;
	}

	/**
	 * @see wicket.settings.IResourceSettings#setResourceFinder(wicket.util.file.IResourceFinder)
	 */
	public void setResourceFinder(final IResourceFinder resourceFinder)
	{
		this.resourceFinder = resourceFinder;

		// Cause resource locator to get recreated
		this.resourceStreamLocator = null;
	}

	/**
	 * @see wicket.settings.IResourceSettings#setResourcePollFrequency(wicket.util.time.Duration)
	 */
	public void setResourcePollFrequency(final Duration resourcePollFrequency)
	{
		this.resourcePollFrequency = resourcePollFrequency;
	}

	/**
	 * @see wicket.settings.IResourceSettings#setResourceStreamLocator(wicket.util.resource.locator.ResourceStreamLocator)
	 */
	public void setResourceStreamLocator(ResourceStreamLocator resourceStreamLocator)
	{
		this.resourceStreamLocator = resourceStreamLocator;
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#setResponseRequestEncoding(java.lang.String)
	 */
	public void setResponseRequestEncoding(final String responseRequestEncoding)
	{
		this.responseRequestEncoding = responseRequestEncoding;
	}

	/**
	 * @see wicket.settings.ISessionSettings#setSessionStoreFactory(wicket.session.ISessionStoreFactory)
	 */
	public void setSessionStoreFactory(ISessionStoreFactory sessionStoreFactory)
	{
		this.sessionStoreFactory = sessionStoreFactory;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#setStripComments(boolean)
	 */
	public void setStripComments(boolean stripComments)
	{
		this.stripComments = stripComments;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#setStripWicketTags(boolean)
	 */
	public void setStripWicketTags(boolean stripWicketTags)
	{
		this.stripWicketTags = stripWicketTags;
	}

	/**
	 * @see wicket.settings.IMarkupSettings#setStripXmlDeclarationFromOutput(boolean)
	 */
	public void setStripXmlDeclarationFromOutput(final boolean strip)
	{
		this.stripXmlDeclarationFromOutput = strip;
	}

	/**
	 * @see wicket.settings.IResourceSettings#setThrowExceptionOnMissingResource(boolean)
	 */
	public void setThrowExceptionOnMissingResource(final boolean throwExceptionOnMissingResource)
	{
		this.throwExceptionOnMissingResource = throwExceptionOnMissingResource;
	}

	/**
	 * @see wicket.settings.IRequestCycleSettings#setUnexpectedExceptionDisplay(wicket.settings.Settings.UnexpectedExceptionDisplay)
	 */
	public void setUnexpectedExceptionDisplay(
			final UnexpectedExceptionDisplay unexpectedExceptionDisplay)
	{
		this.unexpectedExceptionDisplay = unexpectedExceptionDisplay;
	}

	/**
	 * @see wicket.settings.IResourceSettings#setUseDefaultOnMissingResource(boolean)
	 */
	public void setUseDefaultOnMissingResource(final boolean useDefaultOnMissingResource)
	{
		this.useDefaultOnMissingResource = useDefaultOnMissingResource;
	}

	/**
	 * @see wicket.settings.IResourceSettings#setValidatorResourceKeyFactory(wicket.markup.html.form.validation.IValidatorResourceKeyFactory)
	 */
	public void setValidatorResourceKeyFactory(IValidatorResourceKeyFactory factory)
	{
		if (factory == null)
		{
			throw new IllegalArgumentException("ValidatorResourceKeyFactory cannot be set to null");
		}
		this.validatorResourceKeyFactory = factory;
	}

	/**
	 * @see wicket.settings.IPageSettings#setVersionPagesByDefault(boolean)
	 */
	public void setVersionPagesByDefault(boolean pagesVersionedByDefault)
	{
		this.versionPagesByDefault = pagesVersionedByDefault;
	}

	/**
	 * Throws an IllegalArgumentException if the given class is not a subclass
	 * of Page.
	 * 
	 * @param pageClass
	 *            the page class to check
	 */
	private void checkPageClass(final Class pageClass)
	{
		// NOTE: we can't really check on whether it is a bookmarkable page
		// here, as - though the default is that a bookmarkable page must
		// either have a default constructor and/or a constructor with a
		// PageParameters object, this could be different for another
		// IPageFactory implementation
		if (!Page.class.isAssignableFrom(pageClass))
		{
			throw new IllegalArgumentException("argument " + pageClass
					+ " must be a subclass of Page");
		}
	}
}
