/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
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
package wicket.version.undo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wicket.Component;

/**
 * A ChangeList is a sequence of changes that can be undone.
 * 
 * @author Jonathan Locke
 */
class ChangeList implements Serializable
{
	private List changes = new ArrayList();

	void componentAdded(Component component)
	{
		changes.add(new Add(component));
	}

	void componentModelChanging(Component component)
	{
		changes.add(new ModelChange(component));
	}

	void componentRemoved(Component component)
	{
		changes.add(new Remove(component));
	}

	void componentVisibilityChanged(Component component)
	{
		changes.add(new VisibilityChange(component));
	}

	void undo()
	{
		// Go through changes in reverse time order to undo
		for (int i = changes.size() - 1; i >= 0; i--)
		{
			((Change)changes.get(i)).undo();
		}
	}
}
