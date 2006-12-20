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
package wicket.injection;

import java.lang.reflect.Field;

/**
 * Implementation of field value factory that ignores all fields
 * 
 * @author Igor Vaynberg (ivaynberg)
 *
 */
public class NoopFieldValueFactory implements IFieldValueFactory
{

	/**
	 * @see wicket.injection.IFieldValueFactory#getFieldValue(java.lang.reflect.Field, java.lang.Object)
	 */
	public Object getFieldValue(Field field, Object fieldOwner)
	{
		return null;
	}

	/**
	 * @see wicket.injection.IFieldValueFactory#supportsField(java.lang.reflect.Field)
	 */
	public boolean supportsField(Field field)
	{
		return false;
	}

}
