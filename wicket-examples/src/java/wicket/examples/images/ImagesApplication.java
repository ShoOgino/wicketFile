/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.examples.images;

import wicket.Application;
import wicket.examples.WicketExampleApplication;
import wicket.markup.html.image.resource.DefaultButtonImageResource;

/**
 * WicketServlet class for wicket.examples.linkomatic example.
 * 
 * @author Jonathan Locke
 */
public class ImagesApplication extends WicketExampleApplication
{
	/**
	 * Constructor
	 */
	public ImagesApplication()
	{
		getPages().setHomePage(Home.class);
        // insert an alias for the Home class so that all images don't have the full home package in there url. 
		getPages().putClassAlias(Home.class,"home");
        // insert an alias for the wicket.Application class so that all images don't have the wicket.Application in there url. 
		getPages().putClassAlias(Application.class,"application");
		getSharedResources().add("cancelButton", new DefaultButtonImageResource("Cancel"));
	}
}
