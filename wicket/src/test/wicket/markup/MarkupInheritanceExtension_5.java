/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup;

import wicket.PageParameters;
import wicket.markup.html.basic.Label;


/**
 */
public class MarkupInheritanceExtension_5 extends MarkupInheritanceBase_5 
{
	/**
	 * Construct.
	 * @param parameters
	 */
	public MarkupInheritanceExtension_5(final PageParameters parameters) 
	{
	    super(parameters);
	    
	    add(new Label("label3", "extension label"));
    }
}
