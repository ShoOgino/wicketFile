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
package wicket.markup.html.form.validation;

import java.io.Serializable;

import wicket.FeedbackMessages;
import wicket.markup.html.form.Form;

/**
 * Delegate for form validation. Implementors of this interface provide the actual
 * validation checking.
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public interface IFormValidationDelegate extends Serializable
{
	/**
	 * Validates the form and return the collected feeback messages.
	 * @param form the form that the validation is applied to
	 * @return the collected feedback messages
	 */
	public FeedbackMessages validate(Form form);
}