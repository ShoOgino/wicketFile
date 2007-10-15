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
package org.apache.wicket.markup;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.wicket.Application;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.loader.DefaultMarkupLoader;
import org.apache.wicket.markup.loader.IMarkupLoader;
import org.apache.wicket.settings.IMarkupSettings;
import org.apache.wicket.util.concurrent.ConcurrentHashMap;
import org.apache.wicket.util.listener.IChangeListener;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.watch.IModifiable;
import org.apache.wicket.util.watch.ModificationWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is Wicket's default IMarkupCache implementation. It will load the markup and cache it for
 * fast retrieval.
 * <p>
 * If the application is in development mode and a markup file changes, it'll automatically be
 * removed from the cache and reloaded when needed.
 * <p>
 * MarkupCache is registered with {@link IMarkupSettings} and thus can be replaced with a subclassed
 * version.
 *
 * @see IMarkupSettings
 *
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 */
public class MarkupCache implements IMarkupCache
{
	/** Log for reporting. */
	private static final Logger log = LoggerFactory.getLogger(MarkupCache.class);

	/** Map of markup tags by class (exactly what is in the file). */
	private final ICache markupCache;

	/** The markup cache key provider used by MarkupCache */
	private IMarkupCacheKeyProvider markupCacheKeyProvider;

	/** The markup resource stream provider used by MarkupCache */
	private IMarkupResourceStreamProvider markupResourceStreamProvider;

	/** The markup loader used by MarkupCache */
	private IMarkupLoader markupLoader;

	/** The application object */
	private final Application application;

	/**
	 * Constructor.
	 *
	 * @param application
	 */
	public MarkupCache(Application application)
	{
		this.application = application;

		markupCache = newCacheImplementation();
		if (markupCache == null)
		{
			throw new WicketRuntimeException("The map used to cache markup must not be null");
		}
	}

	/**
	 * @see org.apache.wicket.markup.IMarkupCache#clear()
	 */
	public final void clear()
	{
		markupCache.clear();
	}

	/**
	 *
	 * @see org.apache.wicket.markup.IMarkupCache#shutdown()
	 */
	public void shutdown()
	{
		markupCache.shutdown();
	}

	/**
	 * @see org.apache.wicket.markup.IMarkupCache#removeMarkup(java.lang.String)
	 */
	public final Markup removeMarkup(final String cacheKey)
	{
		if (cacheKey == null)
		{
			throw new IllegalArgumentException("Parameter 'cacheKey' must not be null");
		}

		if (log.isDebugEnabled())
		{
			log.debug("Remove from cache: cacheKey=" + cacheKey);
		}

		// Remove the markup and any other markup which depends on it
		// (inheritance)
		Markup markup = (Markup)markupCache.get(cacheKey);
		if (markup != null)
		{
			markupCache.remove(cacheKey);

			// In practice markup inheritance has probably not more than 3 or 4
			// levels. And since markup reloading is only enabled in development
			// mode, this max 4 iterations of the outer loop shouldn't be a
			// problem.
			int count;
			do
			{
				count = 0;

				// If a base markup file has been removed from the cache, than
				// the derived markup should be removed as well.
				Iterator iter = markupCache.getKeys().iterator();
				while (iter.hasNext())
				{
					Markup cacheMarkup = (Markup)markupCache.get(iter.next());
					MarkupResourceData resourceData = cacheMarkup.getMarkupResourceData()
							.getBaseMarkupResourceData();
					if (resourceData != null)
					{
						String baseCacheKey = resourceData.getResource().getCacheKey();
						if (markupCache.get(baseCacheKey) == null)
						{
							if (log.isDebugEnabled())
							{
								log.debug("Remove from cache: cacheKey=" +
										cacheMarkup.getMarkupResourceData().getResource()
												.getCacheKey());
							}

							iter.remove();
							count++;
						}
					}
				}
			}
			while (count > 0);

			// And now remove all watcher entries associated with markup
			// resources no longer in the cache. Note that you can not use
			// Application.get() since removeMarkup() will be call from a
			// ModificationWatcher thread which has no associated Application.
			final ModificationWatcher watcher = application.getResourceSettings()
					.getResourceWatcher(true);
			if (watcher != null)
			{
				Iterator iter = watcher.getEntries().iterator();
				while (iter.hasNext())
				{
					IModifiable modifiable = (IModifiable)iter.next();
					if (modifiable instanceof MarkupResourceStream)
					{
						MarkupResourceStream resourceStream = (MarkupResourceStream)modifiable;
						String resourceCacheKey = resourceStream.getCacheKey();
						if (markupCache.containsKey(resourceCacheKey) == false)
						{
							iter.remove();
						}
					}
				}
			}
		}
		return markup;
	}

	/**
	 * @see org.apache.wicket.markup.IMarkupCache#getMarkupStream(org.apache.wicket.MarkupContainer,
	 *      boolean, boolean)
	 */
	public final MarkupStream getMarkupStream(final MarkupContainer container,
			final boolean enforceReload, final boolean throwException)
	{
		if (container == null)
		{
			throw new IllegalArgumentException("Parameter 'container' must not be 'null'.");
		}

		// Look for associated markup
		final Markup markup = getMarkup(container, container.getClass(), false);

		// If we found markup for this container
		if (markup != Markup.NO_MARKUP)
		{
			return new MarkupStream(markup);
		}

		if (throwException == true)
		{
			// throw exception since there is no associated markup
			throw new MarkupNotFoundException("Markup not found. Component class: " +
					container.getClass().getName() +
					" Enable debug messages for org.apache.wicket.util.resource to get a list of all filenames tried");
		}

		return null;
	}

	/**
	 * @see org.apache.wicket.markup.IMarkupCache#hasAssociatedMarkup(org.apache.wicket.MarkupContainer)
	 */
	public final boolean hasAssociatedMarkup(final MarkupContainer container)
	{
		return getMarkup(container, container.getClass(), false) != Markup.NO_MARKUP;
	}

	/**
	 * @see org.apache.wicket.markup.IMarkupCache#size()
	 */
	public final int size()
	{
		return markupCache.size();
	}

	/**
	 * Get a unmodifiable map which contains the cached data. The map key is of type String and the
	 * value is of type Markup.
	 *
	 * @return
	 */
	protected final ICache getMarkupCache()
	{
		return markupCache;
	}

	/**
	 * THIS IS NOT PART OF WICKET'S PUBLIC API. DO NOT USE IT.
	 *
	 * I still don't like this method being part of the API but I didn't find a suitable other
	 * solution.
	 *
	 * @see org.apache.wicket.markup.IMarkupCache#getMarkup(org.apache.wicket.MarkupContainer,
	 *      java.lang.Class, boolean)
	 */
	public final Markup getMarkup(final MarkupContainer container, final Class clazz,
			final boolean enforceReload)
	{
		Class containerClass = clazz;
		if (clazz == null)
		{
			containerClass = container.getClass();
		}
		else if (!clazz.isAssignableFrom(container.getClass()))
		{
			throw new WicketRuntimeException("Parameter clazz must be an instance of " +
					container.getClass().getName() + ", but is a " + clazz.getName());
		}

		// Get the cache key to be associated with the markup resource stream
		final String cacheKey = getMarkupCacheKeyProvider(container).getCacheKey(container,
				containerClass);

		// Is the markup already in the cache?
		Markup markup = (enforceReload == false ? getMarkupFromCache(cacheKey, container) : null);
		if (markup == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Load markup: cacheKey=" + cacheKey);
			}

			// Who is going to provide the markup resource stream?
			// And ask the provider to locate the markup resource stream
			final IResourceStream resourceStream = getMarkupResourceStreamProvider(container)
					.getMarkupResourceStream(container, containerClass);

			// Found markup?
			if (resourceStream != null)
			{
				final MarkupResourceStream markupResourceStream;
				if (resourceStream instanceof MarkupResourceStream)
				{
					markupResourceStream = (MarkupResourceStream)resourceStream;
				}
				else
				{
					markupResourceStream = new MarkupResourceStream(resourceStream,
							new ContainerInfo(container), containerClass);
				}

				markupResourceStream.setCacheKey(cacheKey);

				// load the markup and watch for changes
				markup = loadMarkupAndWatchForChanges(container, markupResourceStream,
						enforceReload);
			}
			else
			{
				markup = onMarkupNotFound(cacheKey, container);
			}
		}
		return markup;
	}

	/**
	 * Will be called if the markup was not in the cache yet but could not be found either.
	 * <p>
	 * Subclasses may change the default implementation. E.g. they might choose not update the cache
	 * to enforce reloading of any markup not found. This might be useful in very dynamic
	 * environments.
	 *
	 * @param cacheKey
	 * @param container
	 * @return Markup.NO_MARKUP
	 */
	protected Markup onMarkupNotFound(final String cacheKey, final MarkupContainer container)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Markup not found: " + cacheKey);
		}

		// flag markup as non-existent
		return putIntoCache(cacheKey, Markup.NO_MARKUP);
	}

	/**
	 * Put the markup into the cache if cacheKey is not null and the cache does not yet contain the
	 * cacheKey. Return the markup stored in the cache if cacheKey is present already.
	 *
	 * @param cacheKey
	 *            If null, than ignore the cache
	 * @param markup
	 * @return markup The markup provided, except if the cacheKey already existed in the cache, than
	 *         the markup from the cache is provided.
	 */
	protected Markup putIntoCache(final String cacheKey, Markup markup)
	{
		if (cacheKey != null)
		{
			if (markupCache.containsKey(cacheKey) == false)
			{
				markupCache.put(cacheKey, markup);
			}
			else
			{
				// We don't lock the cache while loading a markup. Thus it may
				// happen that the very same markup gets loaded twice (the first
				// markup being loaded, but not yet in the cache, and another
				// request requesting the very same markup). Since markup
				// loading in avg takes less than 100ms, it is not really an
				// issue. For consistency reasons however, we should always use
				// the markup loaded first which is why it gets returned.
				markup = (Markup)markupCache.get(cacheKey);
			}
		}
		return markup;
	}

	/**
	 * Wicket's default implementation just uses the cacheKey to retrieve the markup from the cache.
	 * More sophisticated implementations may call a container method to e.g. ignore the cached
	 * markup under certain situations.
	 *
	 * @param cacheKey
	 *            If null, than the cache will be ignored
	 * @param container
	 * @return null, if not found or to enforce reloading the markup
	 */
	protected Markup getMarkupFromCache(final CharSequence cacheKey, final MarkupContainer container)
	{
		if (cacheKey != null)
		{
			return (Markup)markupCache.get(cacheKey);
		}
		return null;
	}

	/**
	 * Loads markup from a resource stream.
	 *
	 * @param container
	 *            The original requesting markup container
	 * @param markupResourceStream
	 *            The markup resource stream to load
	 * @param enforceReload
	 *            The cache will be ignored and all, including inherited markup files, will be
	 *            reloaded. Whatever is in the cache, it will be ignored
	 * @return The markup
	 */
	private final Markup loadMarkup(final MarkupContainer container,
			final MarkupResourceStream markupResourceStream, final boolean enforceReload)
	{
		String cacheKey = markupResourceStream.getCacheKey();
		try
		{
			Markup markup = getMarkupLoader().loadMarkup(container, markupResourceStream, null,
					enforceReload);

			// add the markup to the cache.
			return putIntoCache(cacheKey, markup);
		}
		catch (ResourceStreamNotFoundException e)
		{
			log.error("Unable to find markup from " + markupResourceStream, e);
		}
		catch (IOException e)
		{
			log.error("Unable to read markup from " + markupResourceStream, e);
		}

		// In case of an error, remove the cache entry
		if (cacheKey != null)
		{
			removeMarkup(cacheKey);
		}

		return Markup.NO_MARKUP;
	}

	/**
	 * Load markup from an IResourceStream and add an {@link IChangeListener}to the
	 * {@link ModificationWatcher} so that if the resource changes, we can remove it from the cache
	 * automatically and subsequently reload when needed.
	 *
	 * @param container
	 *            The original requesting markup container
	 * @param markupResourceStream
	 *            The markup stream to load and begin to watch
	 * @param enforceReload
	 *            The cache will be ignored and all, including inherited markup files, will be
	 *            reloaded. Whatever is in the cache, it will be ignored
	 * @return The markup in the stream
	 */
	private final Markup loadMarkupAndWatchForChanges(final MarkupContainer container,
			final MarkupResourceStream markupResourceStream, final boolean enforceReload)
	{
		final String cacheKey = markupResourceStream.getCacheKey();
		if (cacheKey != null)
		{
			// Watch file in the future
			final ModificationWatcher watcher = Application.get().getResourceSettings()
					.getResourceWatcher(true);
			if (watcher != null)
			{
				watcher.add(markupResourceStream, new IChangeListener()
				{
					public void onChange()
					{
						if (log.isDebugEnabled())
						{
							log.debug("Remove markup from cache: " + markupResourceStream);
						}

						// Remove the markup from the cache. It will be reloaded
						// next time when the markup is requested.
						watcher.remove(markupResourceStream);
						removeMarkup(cacheKey);
					}
				});
			}
		}

		if (log.isDebugEnabled())
		{
			log.debug("Loading markup from " + markupResourceStream);
		}
		return loadMarkup(container, markupResourceStream, enforceReload);
	}

	/**
	 * Get the markup cache key provider to be used
	 *
	 * @param container
	 *            The MarkupContainer requesting the markup resource stream
	 * @return IMarkupResourceStreamProvider
	 */
	public IMarkupCacheKeyProvider getMarkupCacheKeyProvider(final MarkupContainer container)
	{
		if (container instanceof IMarkupCacheKeyProvider)
		{
			return (IMarkupCacheKeyProvider)container;
		}

		if (markupCacheKeyProvider == null)
		{
			markupCacheKeyProvider = new DefaultMarkupCacheKeyProvider();
		}
		return markupCacheKeyProvider;
	}

	/**
	 * Get the markup resource stream provider to be used
	 *
	 * @param container
	 *            The MarkupContainer requesting the markup resource stream
	 * @return IMarkupResourceStreamProvider
	 */
	protected IMarkupResourceStreamProvider getMarkupResourceStreamProvider(
			final MarkupContainer container)
	{
		if (container instanceof IMarkupResourceStreamProvider)
		{
			return (IMarkupResourceStreamProvider)container;
		}

		if (markupResourceStreamProvider == null)
		{
			markupResourceStreamProvider = new DefaultMarkupResourceStreamProvider();
		}
		return markupResourceStreamProvider;
	}

	/**
	 * In case there is a need to extend the default chain of MarkupLoaders
	 *
	 * @return MarkupLoader
	 */
	protected IMarkupLoader getMarkupLoader()
	{
		if (markupLoader == null)
		{
			markupLoader = new DefaultMarkupLoader();
		}
		return markupLoader;
	}

	/**
	 * Allows you to change the map implementation which will hold the cache data. By default it is
	 * a ConcurrentHashMap() in order to allow multiple thread to access the data in a secure way.
	 *
	 * @return
	 */
	protected ICache newCacheImplementation()
	{
		return new DefaultCacheImplementation();
	}

	/**
	 * MarkupCache allows you to implement you own cache implementation. ICache is the interface the
	 * implementation must comply with.
	 *
	 * @see MarkupCache
	 */
	public interface ICache
	{
		/**
		 * Clear the cache
		 */
		void clear();

		/**
		 * Remove an entry from the cache.
		 *
		 * @param key
		 * @return true, if found and removed
		 */
		boolean remove(Object key);

		/**
		 * Get the cache element associated with the key
		 *
		 * @param key
		 * @return
		 */
		Object get(Object key);

		/**
		 * Get all the keys referencing cache entries
		 *
		 * @return
		 */
		Collection getKeys();

		/**
		 * Check if key is in the cache
		 *
		 * @param key
		 * @return
		 */
		boolean containsKey(Object key);

		/**
		 * Get the number of cache entries
		 *
		 * @return
		 */
		int size();

		/**
		 * Put an entry into the cache
		 *
		 * @param key
		 *            The reference key to find the element
		 * @param value
		 *            The element to be cached
		 */
		void put(Object key, Object value);

		/**
		 * Cleanup and shutdown
		 */
		void shutdown();
	}

	/**
	 *
	 */
	public class DefaultCacheImplementation implements ICache
	{
		private static final long serialVersionUID = 1L;

		private final ConcurrentHashMap cache = new ConcurrentHashMap();

		/**
		 * Construct.
		 */
		public DefaultCacheImplementation()
		{
		}

		/**
		 * @see org.apache.wicket.markup.MarkupCache.ICache#clear()
		 */
		public void clear()
		{
			cache.clear();
		}

		/**
		 * @see org.apache.wicket.markup.MarkupCache.ICache#containsKey(java.lang.Object)
		 */
		public boolean containsKey(Object key)
		{
			return cache.containsKey(key);
		}

		/**
		 * @see org.apache.wicket.markup.MarkupCache.ICache#get(java.lang.Object)
		 */
		public Object get(Object key)
		{
			return cache.get(key);
		}

		/**
		 * @see org.apache.wicket.markup.MarkupCache.ICache#getKeys()
		 */
		public Collection getKeys()
		{
			return cache.keySet();
		}

		/**
		 * @see org.apache.wicket.markup.MarkupCache.ICache#put(java.lang.Object, java.lang.Object)
		 */
		public void put(Object key, Object value)
		{
			cache.put(key, value);
		}

		/**
		 * @see org.apache.wicket.markup.MarkupCache.ICache#remove(java.lang.Object)
		 */
		public boolean remove(Object key)
		{
			return cache.remove(key) == null;
		}

		/**
		 * @see org.apache.wicket.markup.MarkupCache.ICache#size()
		 */
		public int size()
		{
			return cache.size();
		}

		/**
		 * @see org.apache.wicket.markup.MarkupCache.ICache#shutdown()
		 */
		public void shutdown()
		{
			clear();
		}
	}
}
