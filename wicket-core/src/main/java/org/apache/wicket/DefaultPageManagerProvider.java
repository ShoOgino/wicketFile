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

import java.io.File;

import org.apache.wicket.page.IPageManager;
import org.apache.wicket.page.IPageManagerContext;
import org.apache.wicket.page.PersistentPageManager;
import org.apache.wicket.pageStore.DefaultPageStore;
import org.apache.wicket.pageStore.DiskDataStore;
import org.apache.wicket.pageStore.IDataStore;
import org.apache.wicket.pageStore.IPageStore;
import org.apache.wicket.serialize.ISerializer;
import org.apache.wicket.settings.IStoreSettings;
import org.apache.wicket.util.lang.Bytes;

/**
 * {@link IPageManagerProvider} implementation that creates new instance of {@link IPageManager}
 * that persists the pages in {@link DiskDataStore}
 */
public class DefaultPageManagerProvider implements IPageManagerProvider
{
	protected final Application application;

	/**
	 * Construct.
	 * 
	 * @param application
	 */
	public DefaultPageManagerProvider(Application application)
	{
		this.application = application;
	}

	public IPageManager get(IPageManagerContext pageManagerContext)
	{
		IDataStore dataStore = newDataStore();
		IPageStore pageStore = newPageStore(dataStore);
		return new PersistentPageManager(application.getName(), pageStore, pageManagerContext);

	}

	protected IPageStore newPageStore(IDataStore dataStore)
	{
		int inmemoryCacheSize = getStoreSettings().getInmemoryCacheSize();
		ISerializer pageSerializer = application.getFrameworkSettings().getSerializer();
		return new DefaultPageStore(pageSerializer, dataStore, inmemoryCacheSize);
	}

	protected IDataStore newDataStore()
	{
		IStoreSettings storeSettings = getStoreSettings();
		Bytes maxSizePerSession = storeSettings.getMaxSizePerSession();
		int fileChannelPoolCapacity = storeSettings.getFileChannelPoolCapacity();
		File fileStoreFolder = storeSettings.getFileStoreFolder();

		return new DiskDataStore(application.getName(), fileStoreFolder, maxSizePerSession,
			fileChannelPoolCapacity);
	}

	IStoreSettings getStoreSettings()
	{
		return Application.get().getStoreSettings();
	}
}