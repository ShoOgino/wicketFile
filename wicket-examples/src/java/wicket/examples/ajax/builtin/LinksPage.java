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
package wicket.examples.ajax.builtin;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxFallbackLink;
import wicket.ajax.markup.html.AjaxLink;
import wicket.behavior.MarkupIdSetter;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.model.PropertyModel;
import wicket.version.undo.Change;

/**
 * Ajax links demo.
 * 
 * @author ivaynberg
 */
public class LinksPage extends BasePage
{
	private int counter1 = 0;
	private int counter2 = 0;

	/**
	 * @return Value of counter1
	 */
	public int getCounter1()
	{
		return counter1;
	}

	/**
	 * @param counter1
	 *            New value for counter1
	 */
	public void setCounter1(int counter1)
	{
		this.counter1 = counter1;
	}

	/**
	 * @return Value for counter2
	 */
	public int getCounter2()
	{
		return counter2;
	}

	/**
	 * @param counter2
	 *            New value for counter2
	 */
	public void setCounter2(int counter2)
	{
		this.counter2 = counter2;
	}

	/**
	 * Constructor
	 */
	public LinksPage()
	{
		final Label c1 = new Label("c1", new PropertyModel(this, "counter1"));
		add(c1.add(MarkupIdSetter.INSTANCE));

		final Label c2 = new Label("c2", new PropertyModel(this, "counter2"));
		add(c2.add(MarkupIdSetter.INSTANCE));

		add(new AjaxLink("c1-link")
		{

			protected void onClick(AjaxRequestTarget target)
			{
				counter1++;
				addStateChange(new Change()
				{

					public void undo()
					{
						// TODO Auto-generated method stub

					}

				});
				target.addComponent(c1);
			}

		});

		add(new AjaxFallbackLink("c2-link")
		{

			protected void onClick(AjaxRequestTarget target)
			{
				counter2++;
				if (target != null)
				{
					target.addComponent(c2);
				}
			}

		});

		add(new Link("c3-link")
		{

			public void onClick()
			{
				System.out.println("hello");

			}

		});
	}
}