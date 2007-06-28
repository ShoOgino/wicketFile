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
package org.apache.wicket.validation.validator;

/**
 * This class will be removed post 1.3.
 * 
 * @deprecated Use {@link EmailAddressValidator} instead. Don't forget to update your resource keys!
 */
public class EmailAddressPatternValidator extends EmailAddressValidator
{
	private static final long serialVersionUID = 1L;
	private static final EmailAddressPatternValidator INSTANCE = new EmailAddressPatternValidator();
	
	/**
	 * @deprecated Use {@link EmailAddressValidator} instead. Don't forget to update your resource keys!
	 * @return
	 */
	public static EmailAddressValidator getInstance() {
		// We have to have our own instance because otherwise we break the resource keys.
		return INSTANCE;
	}

}
