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
package wicket.spring.injection.util;

import wicket.spring.injection.annot.SpringBean;


/**
 * Mock for an object with some SpringBean annotations
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class Injectable
{
	private Bean nobean;

	@SpringBean
	private Bean beanByClass;

	@SpringBean(name = "somebean")
	private Bean2 beanByName;

	public Bean getBeanByClass() {
		return beanByClass;
	}

	public Bean2 getBeanByName() {
		return beanByName;
	}

	public Bean getNobean() {
		return nobean;
	}
	
	

}
