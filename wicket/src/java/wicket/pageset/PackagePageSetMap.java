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
package wicket.pageset;

import java.util.HashMap;
import java.util.Map;

import wicket.Page;
import wicket.PageSet;
import wicket.PageSetMap;
import wicket.util.lang.Packages;

/**
 * Associates page classes with PageSets using packages.
 * 
 * @author Jonathan Locke
 */
public class PackagePageSetMap extends PageSetMap
{
	/** Map from Package to PageSet */
	private final Map packageToPageSet = new HashMap();

	/**
	 * Adds a mapping from a given class to a given PageSet
	 * 
	 * @param pageClass
	 *            The page class
	 * @param pageSet
	 *            The PageSet to associate this class with
	 */
	public final void add(final Class pageClass, final PageSet pageSet)
	{
		checkPageClass(pageClass);
		packageToPageSet.put(pageClass.getPackage(), pageSet);
	}

	/**
	 * @see wicket.PageSetMap#pageSet(wicket.Page)
	 */
	public PageSet pageSet(final Page page)
	{
		for (Package p = page.getClass().getPackage(); p != null; p = Packages.parent(p))
		{
			final PageSet pageSet = (PageSet)packageToPageSet.get(p);
			if (pageSet != null)
			{
				return pageSet;
			}
		}
		return null;
	}
}
