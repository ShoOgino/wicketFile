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
package org.apache.wicket.util.upload;

import java.io.IOException;

/**
 * Exception for errors encountered while processing the request.
 * 
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 */
public class FileUploadException extends IOException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>FileUploadException</code> without message.
	 */
	public FileUploadException()
	{
	}

	/**
	 * Constructs a new <code>FileUploadException</code> with specified detail message.
	 * 
	 * @param msg
	 *            the error message.
	 */
	public FileUploadException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructs a new <code>FileUploadException</code> with specified cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public FileUploadException(final Throwable cause)
	{
		super();
		initCause(cause);
	}

	/**
	 * Constructs a new <code>FileUploadException</code> with specified detail message and cause
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public FileUploadException(final String message, final Throwable cause)
	{
		super(message);
		initCause(cause);
	}
}
