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
package wicket.application;

import wicket.WicketRuntimeException;
import wicket.util.concurrent.ConcurrentReaderHashMap;

/**
 * Resolves a class by using the classloader that loaded this class.
 * 
 * @see wicket.settings.IApplicationSettings#getClassResolver()
 * 
 * @author Juergen Donnerstag
 * @author Jonathan Locke
 */
public final class DefaultClassResolver implements IClassResolver
{
	private ConcurrentReaderHashMap classes = new ConcurrentReaderHashMap();
	/**
	 * @see wicket.application.IClassResolver#resolveClass(java.lang.String)
	 */
	public final Class resolveClass(final String classname)
	{
		
		try
		{
			Class clz = (Class)classes.get(classname);
			if(clz == null)
			{
				synchronized (classes)
				{
					clz = DefaultClassResolver.class.getClassLoader().loadClass(classname);
					classes.put(classname, clz);
				}
			}
			return clz;
		}
		catch (ClassNotFoundException ex)
		{
			throw new WicketRuntimeException("Unable to load class with name: " + classname);
		}
	}
}

