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
 * Models who implement this interface will support chaining of IModels.
 *	getObject() of a IChainingModel should do something like:
 * <pre>
 * if ( object instanceof IModel) { return ((IModel)object).getObject()}
 * else return object;
 * </pre>
 * 
 * ChainingModels should also take care that the internal model detach is called
 * when detach is called on them.
 * 
 * @author jcompagner
 * @author Igor Vaynberg (ivaynberg)
 * 
 * @see CompoundPropertyModel
 * @see AbstractPropertyModel
 */
public interface IChainingModel extends IModel
{
	/**
	 * Sets the model that is chained inside this model.
	 * 
	 * @param model
	 */
	public void setChainingModel(IModel model);
	
	/**
	 * Returns the chained model if there is a chained model.
	 * 
	 * @return The chained model
	 */
	public IModel getChainingModel();
	
}
