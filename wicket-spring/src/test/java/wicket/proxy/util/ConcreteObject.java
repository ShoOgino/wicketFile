/*
 * $Id$
 * $Revision$
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
package wicket.proxy.util;

/**
 * Mock dependency that does not implement an interface
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class ConcreteObject
{
	private String message;

	/**
	 * Empty default constructor. It is required by cglib to create a proxy.
	 */
	public ConcreteObject()
	{

	}

	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public ConcreteObject(String message)
	{
		this.message = message;
	}

	/**
	 * @return message
	 */
	public String getMessage()
	{
		return message;
	}

}
