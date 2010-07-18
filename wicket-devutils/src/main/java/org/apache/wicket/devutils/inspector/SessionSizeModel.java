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
package org.apache.wicket.devutils.inspector;

import org.apache.wicket.Session;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.lang.WicketObjects;

public class SessionSizeModel extends LoadableDetachableModel<Bytes> {

	private static final long serialVersionUID = 1L;

	private Session session;

	public SessionSizeModel(Session session) {
		this.session = session;
	}

	@Override
	protected Bytes load() {
		return Bytes.bytes(WicketObjects.sizeof(session));
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		this.session = null;
	}
}
