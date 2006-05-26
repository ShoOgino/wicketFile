/*
 * $Id: AjaxLinkPage.java 5844 2006-05-24 20:53:56 +0000 (Wed, 24 May 2006)
 * joco01 $ $Revision$ $Date: 2006-05-24 20:53:56 +0000 (Wed, 24 May
 * 2006) $
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
package wicket.ajax.markup.html.ajaxLink;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxLink;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.model.PropertyModel;

/**
 * 
 */
public class AjaxLinkPage extends WebPage
{
	private static final long serialVersionUID = 1L;

	private String labelText = "UpdateMe";

	/**
	 * 
	 */
	public AjaxLinkPage()
	{
		final Label label = new Label(this, "ajaxLabel", new PropertyModel(this, "labelText"));
		label.setOutputMarkupId(true);
		new AjaxLink(this, "ajaxLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				labelText = "Updated!";
				target.addComponent(label);
			}
		};
	}

	/**
	 * 
	 * @return String
	 */
	public String getLabelText()
	{
		return labelText;
	}
}
