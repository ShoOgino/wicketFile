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
package wicket.examples.compref;

import java.io.Serializable;

import wicket.examples.WicketExamplePage;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextArea;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.CompoundPropertyModel;

/**
 * Page with examples on {@link wicket.markup.html.form.TextArea}.
 *
 * @author Eelco Hillenius
 */
public class TextAreaPage extends WicketExamplePage
{
	/**
	 * Constructor
	 */
	public TextAreaPage()
	{
		final Input input = new Input();
		setModel(new CompoundPropertyModel(input));

		// Add a FeedbackPanel for displaying our messages
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		add(feedbackPanel);

		// Add a form with an onSumbit implementation that sets a message
		Form form = new Form("form")
		{
			protected void onSubmit()
			{
				info("input: " + input);
			}
		};
		add(form);

		// add a text area component that uses Input's 'text' property.
		form.add(new TextArea("text"));
	}

	/** Simple data class that acts as a model for the input fields. */
	private static class Input implements Serializable
	{
		/** some plain text. */
		public String text = "line 1\nline 2\nline 3";

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return "text = '" + text + "'";
		}
	}
}