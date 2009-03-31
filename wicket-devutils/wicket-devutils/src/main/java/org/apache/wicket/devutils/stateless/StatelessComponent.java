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
package org.apache.wicket.devutils.stateless;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for wicket components that you want to be sure remain stateless during the development
 * cycle. Can be attached to pages and components.
 * 
 * <b>Note:</b> this annotation <u><i>does not</i></u> make the component stateless or affect it's
 * statelessness in any way. This annotation should be used in combination with
 * <tt>StatelessChecker</tt> to give you runtime warning if you inadvertantly made a page or
 * component stateful.
 * 
 * @author Marat Radchenko
 * @see StatelessChecker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface StatelessComponent {

	/**
	 * Can be set to <code>false</code> to disable stateless (this is useful if stateful component
	 * inherits from stateless one).
	 */
	boolean enabled() default true;
}
