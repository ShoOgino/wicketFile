/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.examples.ajax.builtin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.examples.guestbook.Comment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;


/**
 * Ajax enabled example for the guestbook.
 * 
 * @author Martijn Dashorst
 */
public class GuestBook extends BasePage
{
	/** A global list of all comments from all users across all sessions */
	public static final List commentList = new ArrayList();

	/** The list view that shows comments */
	private final ListView commentListView;
	/** Container for the comments, used to update the listview.  */
	private WebMarkupContainer comments;
	
	/** The textarea for entering the comments, is updated in the ajax call. */
	private Component text;

	/**
	 * Constructor.
	 */
	public GuestBook()
	{
		// Add comment form
		CommentForm commentForm = new CommentForm("commentForm");
		add(commentForm);

		// the WebMarkupContainer is used to update the listview in an ajax call
		comments = new WebMarkupContainer("comments");
		add(comments.setOutputMarkupId(true));
		
		// Add commentListView of existing comments
		comments.add(commentListView = new ListView("comments", new PropertyModel(this,
				"commentList"))
		{
			public void populateItem(final ListItem listItem)
			{
				final Comment comment = (Comment)listItem.getModelObject();
				listItem.add(new Label("date", new Model(comment.getDate())));
				listItem.add(new MultiLineLabel("text", comment.getText()));
			}
		});
		
		// we need to cancel the standard submit of the form in the onsubmit handler,
		// otherwise we'll get double submits. To do so, we return false after the
		// ajax submit has occurred.
		
		// The AjaxFormSubmitBehavior already calls the onSubmit of the form, all
		// we need to do in the onSubmit(AjaxRequestTarget) handler is do our Ajax
		// specific stuff, like rendering our components.
		commentForm.add(new AjaxFormSubmitBehavior(commentForm, "onsubmit")
		{
			protected IAjaxCallDecorator getAjaxCallDecorator()
			{
				return new AjaxCallDecorator()
				{
					public CharSequence decorateScript(CharSequence script)
					{
						return script + "return false;";
					}
				};
			}

			protected void onSubmit(AjaxRequestTarget target)
			{
				// add the list of components that need to be updated
				target.addComponent(comments);
				target.addComponent(text);
				
				// focus the textarea again
				target.appendJavascript("document.getElementById('" + text.getMarkupId()
						+ "').focus();");
			}
		});
	}

	/**
	 * A form that allows a user to add a comment.
	 * 
	 * @author Jonathan Locke
	 */
	public final class CommentForm extends Form
	{
		/**
		 * Constructor
		 * 
		 * @param id
		 *            The name of this component
		 */
		public CommentForm(final String id)
		{
			// Construct form with no validation listener
			super(id, new CompoundPropertyModel(new Comment()));

			// Add text entry widget
			text = new TextArea("text").setOutputMarkupId(true);
			add(text);
		}

		/**
		 * Show the resulting valid edit
		 */
		public final void onSubmit()
		{
			// Construct a copy of the edited comment
			final Comment comment = (Comment)getModelObject();
			final Comment newComment = new Comment(comment);

			// Set date of comment to add
			newComment.setDate(new Date());

			// Add the component we edited to the list of comments
			commentList.add(0, newComment);

			// Clear out the text component
			comment.setText("");
		}
	}

	/**
	 * Clears the comments.
	 */
	public static void clear()
	{
		commentList.clear();
	}
}
