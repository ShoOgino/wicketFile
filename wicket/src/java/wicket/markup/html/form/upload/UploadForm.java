/*
 * $Id$ $Revision$
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
package wicket.markup.html.form.upload;

import javax.servlet.http.HttpServletRequest;

import wicket.IFeedback;
import wicket.RequestCycle;
import wicket.markup.ComponentTag;
import wicket.markup.html.form.Form;
import wicket.protocol.http.WebRequest;

/**
 * Form for handling (file) uploads with multipart requests. Use this with
 * {@link wicket.markup.html.form.upload.FileUploadField}components. You can attach
 * mutliple FileInput fields for muliple file uploads.
 * 
 * @author Eelco Hillenius
 * @author Jonathan Locke
 */
public abstract class UploadForm extends Form
{
	/**
	 * @see wicket.Component#Component(String)
	 */
	public UploadForm(String name)
	{
		super(name);
	}

	/**
	 * @see Form#Form(String, IFeedback)
	 */
	public UploadForm(String name, IFeedback validationFeedback)
	{
		super(name, validationFeedback);
	}

	/**
	 * Wraps the servlet request in a multipart request and sets it as the
	 * current request.
	 * 
	 * @see wicket.markup.html.form.Form#onFormSubmitted()
	 */
	public void onFormSubmitted()
	{
		// Change the request to a multipart web request so parameters are
		// parsed out correctly
		final HttpServletRequest request = ((WebRequest)getRequest()).getHttpServletRequest();
		final MultipartWebRequest multipartWebRequest = new MultipartWebRequest(request);
		RequestCycle.get().setRequest(multipartWebRequest);

		// Now do normal form submit validation processing
		super.onFormSubmitted();
	}

	/**
	 * @see wicket.Component#onComponentTag(ComponentTag)
	 */
	protected final void onComponentTag(final ComponentTag tag)
	{
		super.onComponentTag(tag);
		tag.put("enctype", "multipart/form-data");
	}

	/**
	 * @see Form#onSubmit()
	 */
	protected abstract void onSubmit();
}
