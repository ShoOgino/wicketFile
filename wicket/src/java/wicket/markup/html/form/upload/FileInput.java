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
package wicket.markup.html.form.upload;

import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;

import wicket.markup.ComponentTag;
import wicket.markup.html.form.FormComponent;

/**
 * Form component that corresponds to a &lt;input type=&quot;file&quot;&gt;.
 * When a FileInput component is nested in a
 * {@link wicket.markup.html.form.upload.UploadForm}, its model is updated
 * with the uploaded file for this component.
 *
 * @author Eelco Hillenius
 */
public class FileInput extends FormComponent
{
	/**
	 * Construct.
	 * @param name name of the component
	 */
	public FileInput(String name)
	{
		super(name);
	}

	/**
	 * Construct.
	 * @param name name of the component
	 * @param object the model
	 */
	public FileInput(String name, Serializable object)
	{
		super(name, object);
	}

	/**
	 * Construct.
	 * @param name name of the component
	 * @param object the model
	 * @param expression ognl expression that works on the model
	 */
	public FileInput(String name, Serializable object, String expression)
	{
		super(name, object, expression);
	}

	/**
	 * FileInputs cannot be persisted; returns false.
	 *
	 * @see wicket.markup.html.form.FormComponent#supportsPersistence()
	 */
	protected boolean supportsPersistence()
	{
		return false;
	}

	/**
	 * @see wicket.markup.html.form.FormComponent#updateModel()
	 */
	protected void updateModel()
	{
		MultipartWebRequest multipartWebRequest = ((MultipartWebRequest)getRequest());
		FileItem item = multipartWebRequest.getFile(getPath());

		// only update the model when there is a file (larger than zero bytes
		if (item != null && item.getSize() > 0)
		{
			setModelObject(item);
		}
	}

	/**
	 * @see wicket.Component#onComponentTag(wicket.markup.ComponentTag)
	 */
	protected void onComponentTag(ComponentTag tag)
	{
		// must be attached to an input tag
		checkComponentTag(tag, "input");

		// check for file type
		checkComponentTagAttribute(tag, "type", "file");

		// Default handling for component tag
		super.onComponentTag(tag);
	}
}