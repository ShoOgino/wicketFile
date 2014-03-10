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
package org.apache.wicket.pageStore;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.wicket.page.IManageablePage;
import org.apache.wicket.serialize.ISerializer;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link IPageStore} that converts {@link IManageablePage} instances to {@link SerializedPage}s
 * before passing them to the {@link IDataStore} to store them and the same in the opposite
 * direction when loading {@link SerializedPage} from the data store.
 * 
 */
public class DefaultPageStore extends AbstractPageStore
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPageStore.class);

	private final SerializedPagesCache serializedPagesCache;

	/**
	 * Construct.
	 * 
	 * @param pageSerializer
	 *            the {@link ISerializer} that will be used to convert pages from/to byte arrays
	 * @param dataStore
	 *            the {@link IDataStore} that actually stores the pages
	 * @param cacheSize
	 *            the number of pages to cache in memory before passing them to
	 *            {@link IDataStore#storeData(String, int, byte[])}
	 */
	public DefaultPageStore(final ISerializer pageSerializer, final IDataStore dataStore,
		final int cacheSize)
	{
		super(pageSerializer, dataStore);
		serializedPagesCache = new SerializedPagesCache(cacheSize);
	}

	@Override
	public IManageablePage getPage(final String sessionId, final int id)
	{
		SerializedPage fromCache = serializedPagesCache.getPage(sessionId, id);
		if (fromCache != null && fromCache.data != null)
		{
			return deserializePage(fromCache.data);
		}

		byte[] data = getPageData(sessionId, id);
		if (data != null)
		{
			return deserializePage(data);
		}
		return null;
	}

	@Override
	public void removePage(final String sessionId, final int id)
	{
		serializedPagesCache.removePage(sessionId, id);
		removePageData(sessionId, id);
	}

	@Override
	public void storePage(final String sessionId, final IManageablePage page)
	{
		SerializedPage serialized = serializePage(sessionId, page);
		if (serialized != null)
		{
			serializedPagesCache.storePage(sessionId, page.getPageId(), serialized);
			storePageData(sessionId, serialized.getPageId(), serialized.getData());
		}
	}

	@Override
	public void unbind(final String sessionId)
	{
		removePageData(sessionId);
		serializedPagesCache.removePages(sessionId);
	}

	@Override
	public IManageablePage convertToPage(final Object object)
	{
		if (object == null)
		{
			return null;
		}
		else if (object instanceof IManageablePage)
		{
			return (IManageablePage)object;
		}
		else if (object instanceof SerializedPage)
		{
			SerializedPage page = (SerializedPage)object;
			byte data[] = page.getData();
			if (data == null)
			{
				data = getPageData(page.getSessionId(), page.getPageId());
			}
			if (data != null)
			{
				return deserializePage(data);
			}
			return null;
		}

		String type = object.getClass().getName();
		throw new IllegalArgumentException("Unknown object type " + type);
	}

	/**
	 * Reloads the {@link SerializedPage} from the backing {@link IDataStore} if the
	 * {@link SerializedPage#data} is stripped earlier
	 * 
	 * @param serializedPage
	 *            the {@link SerializedPage} with empty {@link SerializedPage#data} slot
	 * @return the fully functional {@link SerializedPage}
	 */
	private SerializedPage restoreStrippedSerializedPage(final SerializedPage serializedPage)
	{
		SerializedPage result = serializedPagesCache.getPage(serializedPage.getSessionId(),
			serializedPage.getPageId());
		if (result != null)
		{
			return result;
		}

		byte data[] = getPageData(serializedPage.getSessionId(), serializedPage.getPageId());
		return new SerializedPage(serializedPage.getSessionId(), serializedPage.getPageId(), data);
	}

	@Override
	public Serializable prepareForSerialization(final String sessionId, final Serializable page)
	{
		if (dataStore.isReplicated())
		{
			return null;
		}

		SerializedPage result = null;

		if (page instanceof IManageablePage)
		{
			IManageablePage _page = (IManageablePage)page;
			result = serializedPagesCache.getPage(sessionId, _page.getPageId());
			if (result == null)
			{
				result = serializePage(sessionId, _page);
				if (result != null)
				{
					serializedPagesCache.storePage(sessionId, _page.getPageId(), result);
				}
			}
		}
		else if (page instanceof SerializedPage)
		{
			SerializedPage _page = (SerializedPage)page;
			if (_page.getData() == null)
			{
				result = restoreStrippedSerializedPage(_page);
			}
			else
			{
				result = _page;
			}
		}

		if (result != null)
		{
			return result;
		}
		return page;
	}

	/**
	 * 
	 * @return Always true for this implementation
	 */
	protected boolean storeAfterSessionReplication()
	{
		return true;
	}

	@Override
	public Object restoreAfterSerialization(final Serializable serializable)
	{
		if (serializable == null)
		{
			return null;
		}
		else if (!storeAfterSessionReplication() || serializable instanceof IManageablePage)
		{
			return serializable;
		}
		else if (serializable instanceof SerializedPage)
		{
			SerializedPage page = (SerializedPage)serializable;
			if (page.getData() != null)
			{
				storePageData(page.getSessionId(), page.getPageId(), page.getData());
				return new SerializedPage(page.getSessionId(), page.getPageId(), null);
			}
			return page;
		}

		String type = serializable.getClass().getName();
		throw new IllegalArgumentException("Unknown object type " + type);
	}

	/**
	 * A representation of {@link IManageablePage} that knows additionally the id of the http
	 * session in which this {@link IManageablePage} instance is used. The {@link #sessionId} and
	 * {@link #pageId} are used for better clustering in the {@link IDataStore} structures.
	 */
	protected static class SerializedPage implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * The id of the serialized {@link IManageablePage}
		 */
		private final int pageId;

		/**
		 * The id of the http session in which the serialized {@link IManageablePage} is used.
		 */
		private final String sessionId;

		/**
		 * The serialized {@link IManageablePage}
		 */
		private final byte[] data;

		public SerializedPage(String sessionId, int pageId, byte[] data)
		{
			this.pageId = pageId;
			this.sessionId = sessionId;
			this.data = data;
		}

		public byte[] getData()
		{
			return data;
		}

		public int getPageId()
		{
			return pageId;
		}

		public String getSessionId()
		{
			return sessionId;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if ((obj instanceof SerializedPage) == false)
			{
				return false;
			}
			SerializedPage rhs = (SerializedPage)obj;
			return Objects.equal(getPageId(), rhs.getPageId()) &&
				Objects.equal(getSessionId(), rhs.getSessionId());
		}

		@Override
		public int hashCode()
		{
			return Objects.hashCode(getPageId(), getSessionId());
		}
	}

	/**
	 * 
	 * @param sessionId
	 * @param page
	 * @return the serialized page information
	 */
	protected SerializedPage serializePage(final String sessionId, final IManageablePage page)
	{
		Args.notNull(sessionId, "sessionId");
		Args.notNull(page, "page");

		SerializedPage serializedPage = null;

		byte[] data = serializePage(page);

		if (data != null)
		{
			serializedPage = new SerializedPage(sessionId, page.getPageId(), data);
		}
		else if (LOG.isWarnEnabled())
		{
			LOG.warn("Page {} cannot be serialized. See previous logs for possible reasons.", page);
		}
		return serializedPage;
	}

	/**
	 * Cache that stores serialized pages. This is important to make sure that a single page is not
	 * serialized twice or more when not necessary.
	 * <p>
	 * For example a page is serialized during request, but it might be also later serialized on
	 * session replication. The purpose of this cache is to make sure that the data obtained from
	 * first serialization is reused on second serialization.
	 * 
	 * @author Matej Knopp
	 */
	static class SerializedPagesCache implements SecondLevelPageCache<String, Integer, SerializedPage>
	{
		private final int size;

		private final ConcurrentLinkedDeque<SoftReference<SerializedPage>> cache;

		/**
		 * Construct.
		 * 
		 * @param size
		 */
		public SerializedPagesCache(final int size)
		{
			this.size = size;
			cache = new ConcurrentLinkedDeque<>();
		}

		/**
		 * 
		 * @param sessionId
		 * @param pageId
		 * @return the removed {@link SerializedPage} or <code>null</code> - otherwise
		 */
		@Override
		public SerializedPage removePage(final String sessionId, final Integer pageId)
		{
			if (size > 0)
			{
				Args.notNull(sessionId, "sessionId");
				Args.notNull(pageId, "pageId");

				for (Iterator<SoftReference<SerializedPage>> i = cache.iterator(); i.hasNext();)
				{
					SoftReference<SerializedPage> ref = i.next();
					SerializedPage entry = ref.get();
					if (entry != null && entry.getPageId() == pageId &&
						entry.getSessionId().equals(sessionId))
					{
						i.remove();
						return entry;
					}
				}
			}
			return null;
		}

		/**
		 * Removes all {@link SerializedPage}s for the session with <code>sessionId</code> from the
		 * cache.
		 * 
		 * @param sessionId
		 */
		@Override
		public void removePages(String sessionId)
		{
			if (size > 0)
			{
				Args.notNull(sessionId, "sessionId");

				for (Iterator<SoftReference<SerializedPage>> i = cache.iterator(); i.hasNext();)
				{
					SoftReference<SerializedPage> ref = i.next();
					SerializedPage entry = ref.get();
					if (entry != null && entry.getSessionId().equals(sessionId))
					{
						i.remove();
					}
				}
			}
		}

		/**
		 * Returns a {@link SerializedPage} by looking it up by <code>sessionId</code> and
		 * <code>pageId</code>. If there is a match then it is <i>touched</i>, i.e. it is moved at
		 * the top of the cache.
		 * 
		 * @param sessionId
		 * @param pageId
		 * @return the found serialized page or <code>null</code> when not found
		 */
		@Override
		public SerializedPage getPage(String sessionId, Integer pageId)
		{
			SerializedPage result = null;
			if (size > 0)
			{
				Args.notNull(sessionId, "sessionId");
				Args.notNull(pageId, "pageId");

				for (Iterator<SoftReference<SerializedPage>> i = cache.iterator(); i.hasNext();)
				{
					SoftReference<SerializedPage> ref = i.next();
					SerializedPage entry = ref.get();
					if (entry != null && entry.getPageId() == pageId &&
						entry.getSessionId().equals(sessionId))
					{
						i.remove();
						result = entry;
						break;
					}
				}

				if (result != null)
				{
					// move to top
					storePage(sessionId, pageId, result);
				}
			}
			return result;
		}

		/**
		 * Store the serialized page in cache
		 * 
		 * @param page
		 *      the data to serialize (page id, session id, bytes)
		 */
		@Override
		public void storePage(String sessionId, Integer pageId, SerializedPage page)
		{
			if (size > 0)
			{
				Args.notNull(sessionId, "sessionId");
				Args.notNull(pageId, "pageId");
				Args.notNull(page, "page");

				for (Iterator<SoftReference<SerializedPage>> i = cache.iterator(); i.hasNext();)
				{
					SoftReference<SerializedPage> r = i.next();
					SerializedPage entry = r.get();
					if (entry != null && entry.equals(page))
					{
						i.remove();
						break;
					}
				}

				cache.add(new SoftReference<>(page));
				while (cache.size() > size)
				{
					cache.remove(0);
				}
			}
		}
	}
}
