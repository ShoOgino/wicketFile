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
package org.apache.wicket.ng.request.cycle;

import org.apache.wicket.ng.request.Request;
import org.apache.wicket.ng.request.RequestMapper;
import org.apache.wicket.ng.request.response.Response;

/**
 * Represents the context for the request cycle. This class is mainly a grouping parameter for the
 * {@link RequestCycle} constructor. It is only necesary to future-proof the API by making sure
 * method signatures do not change if further parameters are introduced at a later time.
 * <p>
 * NOTE: Once a {@link RequestCycle} is instantiated using an instance of this class, the setters
 * will have no further effect on the request cycle.
 * </p>
 * 
 * @author igor.vaynberg
 */
public final class RequestCycleContext
{
	private Request request;
	private Response response;
	private RequestMapper requestMapper;
	private ExceptionMapper exceptionMapper;

	/**
	 * Construct.
	 * 
	 * @param request
	 * @param response
	 * @param requestMapper
	 * @param exceptionMapper
	 */
	public RequestCycleContext(Request request, Response response, RequestMapper requestMapper,
		ExceptionMapper exceptionMapper)
	{
		this.request = request;
		this.response = response;
		this.requestMapper = requestMapper;
		this.exceptionMapper = exceptionMapper;
	}

	/**
	 * @return request
	 */
	public Request getRequest()
	{
		return request;
	}

	/**
	 * @return response
	 */
	public Response getResponse()
	{
		return response;
	}

	/**
	 * @return requst mapper
	 */
	public RequestMapper getRequestMapper()
	{
		return requestMapper;
	}


	/**
	 * @return exception mapper
	 */
	public ExceptionMapper getExceptionMapper()
	{
		return exceptionMapper;
	}

	/**
	 * @param request
	 */
	public void setRequest(Request request)
	{
		this.request = request;
	}

	/**
	 * @param response
	 */
	public void setResponse(Response response)
	{
		this.response = response;
	}

	/**
	 * @param requestMapper
	 */
	public void setRequestMapper(RequestMapper requestMapper)
	{
		this.requestMapper = requestMapper;
	}

	/**
	 * @param exceptionMapper
	 */
	public void setExceptionMapper(ExceptionMapper exceptionMapper)
	{
		this.exceptionMapper = exceptionMapper;
	}


}
