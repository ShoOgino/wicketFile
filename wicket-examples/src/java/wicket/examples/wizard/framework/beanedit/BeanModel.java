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
package wicket.examples.wizard.framework.beanedit;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

import wicket.Component;
import wicket.WicketRuntimeException;
import wicket.model.IModel;

/**
 * Model for JavaBeans.
 *
 * @author Eelco Hillenius
 */
public abstract class BeanModel implements IModel
{
	/** the nested model that provides the java bean. */
	private final IModel nestedModel;

	/**
	 * Construct.
	 * @param nestedModel model that provides the java bean
	 */
	public BeanModel(IModel nestedModel)
	{
		this.nestedModel = nestedModel;
	}

	/**
	 * Gets the {@link BeanInfo} object of the model object.
	 * @param component
	 * @return BeanInfo object for the model object, or null if the model object is null
	 */
	protected final BeanInfo getBeanInfo(Component component)
	{
		Object bean = getBean(component);
		if(bean != null)
		{
			Class objectClass = bean.getClass();
			try
			{
				return Introspector.getBeanInfo(objectClass);
			}
			catch (IntrospectionException e)
			{
				throw new WicketRuntimeException(e);
			}	
		}
		return null;
	}

	/**
	 * Gets the java bean proper.
	 * @param component
	 * @return the java bean proper, possibly null
	 */
	protected final Object getBean(Component component)
	{
		return ((IModel)getNestedModel()).getObject(component);
	}

	/**
	 * Gets the class java bean proper.
	 * @param component
	 * @return the class of the java bean proper, or null if the model object is null
	 */
	protected final Class getBeanClass(Component component)
	{
		Object bean = getBean(component);
		return (bean != null) ? bean.getClass() : null;
	}

	/**
	 * @see wicket.model.IModel#getNestedModel()
	 */
	public IModel getNestedModel()
	{
		return nestedModel;
	}

	/**
	 * @see wicket.model.IDetachable#detach()
	 */
	public void detach()
	{
	}
}
