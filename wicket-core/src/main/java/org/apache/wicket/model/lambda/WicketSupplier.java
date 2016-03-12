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
package org.apache.wicket.model.lambda;

import java.io.Serializable;
import java.util.function.Supplier;

import org.apache.wicket.util.io.IClusterable;

/**
 * A {@link Serializable} {@link Supplier}.
 *
 * @param <T>
 *            - the type of results supplied by this supplier
 */
public interface WicketSupplier<T> extends Supplier<T>, IClusterable
{
}
