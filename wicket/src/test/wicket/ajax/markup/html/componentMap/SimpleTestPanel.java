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
package wicket.ajax.markup.html.componentMap;

import wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import wicket.markup.html.basic.Label;
import wicket.model.PropertyModel;
import wicket.util.time.Duration;

/**
 * 
 */
public class SimpleTestPanel extends SimpleTestPanelBase
{
	private static final long serialVersionUID = 1L;

	private int count = 0;

	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public SimpleTestPanel(String name)
	{
		super(name);

		Label ajaxLabel = new Label("linja1", new PropertyModel(this, "count"));
		AjaxSelfUpdatingTimerBehavior timer = new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2));

		ajaxLabel.add(timer);
		baseSpan.add(ajaxLabel);
	}

	/**
	 * @return Count
	 */
	public int getCount()
	{
		return count++;
	}
}
