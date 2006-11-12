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
package wicket.model;


/**
 * Adds a default implementation of onSetObject() which throws an
 * UnsupportedOperationException to indicate that the subclass is a read-only
 * model.
 * 
 * @param <T>
 *            The type
 * 
 * @author Jonathan Locke
 */
public abstract class AbstractReadOnlyDetachableModel<T> extends AbstractDetachableModel<T>
{
	/**
	 * This default implementation of onSetObject unconditionally throws an
	 * UnsupportedOperationException. Since the method is final, any subclass is
	 * effectively a read-only model.
	 * @param object
	 *            The object to set into the model
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	protected final void onSetObject(final T object)
	{
		throw new UnsupportedOperationException("Model " + getClass()
				+ " does not support setObject(Object)");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString();
	}
}
