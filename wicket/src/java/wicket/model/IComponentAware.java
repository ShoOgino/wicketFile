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
package wicket.model;

import wicket.Component;

/**
 * Interface that can be implemented by instances of {@link wicket.model.IModel}
 * to indicate that they want to have the component that the model is set on
 * being set.
 */
public interface IComponentAware
{
	/**
	 * Sets the component that uses this model.
	 * @param component the component that uses this model
	 */
	void setComponent(Component component);
}