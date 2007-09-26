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
package org.apache.wicket.version.undo;

import org.apache.wicket.IClusterable;

/**
 * A <code>Change</code> contains undo information for a change to the
 * <code>Page</code>.
 * 
 * @author Jonathan Locke
 * @since 1.2.6
 */
public abstract class Change implements IClusterable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Undoes the given change.
	 */
	public abstract void undo();
}
