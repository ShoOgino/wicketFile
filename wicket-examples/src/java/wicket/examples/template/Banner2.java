/*
 * $Id$ $Revision$ $Date$
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
package wicket.examples.template;

import wicket.MarkupContainer;
import wicket.model.IModel;

/**
 * A banner.
 * 
 * @author Eelco Hillenius
 */
public class Banner2 extends Banner
{
	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public Banner2(MarkupContainer parent, String id)
	{
		super(parent,id);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 */
	public Banner2(MarkupContainer parent,final String id, IModel model)
	{
		super(parent,id, model);
	}
}
