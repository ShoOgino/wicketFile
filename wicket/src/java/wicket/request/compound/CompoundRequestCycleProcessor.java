/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.request.compound;

/**
 * Default implementation of {@link
 * wicket.request.compound.AbstractCompoundRequestCycleProcessor} that expects
 * the delegate strategies to be set once at construction time.
 * 
 * @author Eelco Hillenius
 */
public class CompoundRequestCycleProcessor extends AbstractCompoundRequestCycleProcessor
{
	/** the strategy for the target resolver method. */
	private final IRequestTargetResolverStrategy requestTargetResolverStrategy;

	/** the strategy for the event processor method. */
	private final IEventProcessorStrategy eventProcessorStrategy;

	/** the strategy for the response method. */
	private final IResponseStrategy responseStrategy;

	/** the strategy for the exception response method. */
	private final IExceptionResponseStrategy exceptionResponseStrategy;

	/**
	 * Construct using the given strategies and
	 * {@link DefaultResponseProcessorStrategy} and
	 * {@link DefaultExceptionResponseProcessorStrategy}.
	 * 
	 * @param requestTargetResolverStrategy
	 *            the strategy for the target resolver method
	 * @param eventProcessorStrategy
	 *            the strategy for the event processor method
	 */
	public CompoundRequestCycleProcessor(
			IRequestTargetResolverStrategy requestTargetResolverStrategy,
			IEventProcessorStrategy eventProcessorStrategy)
	{
		this(requestTargetResolverStrategy, eventProcessorStrategy,
				new DefaultResponseProcessorStrategy(),
				new DefaultExceptionResponseProcessorStrategy());
	}

	/**
	 * Construct using the given strategies.
	 * 
	 * @param requestTargetResolverStrategy
	 *            the strategy for the target resolver method
	 * @param eventProcessorStrategy
	 *            the strategy for the event processor method
	 * @param responseStrategy
	 *            the strategy for the response method
	 * @param exceptionResponseStrategy
	 *            the strategy for the exception response method
	 */
	public CompoundRequestCycleProcessor(
			IRequestTargetResolverStrategy requestTargetResolverStrategy,
			IEventProcessorStrategy eventProcessorStrategy, IResponseStrategy responseStrategy,
			IExceptionResponseStrategy exceptionResponseStrategy)
	{
		this.requestTargetResolverStrategy = requestTargetResolverStrategy;
		this.eventProcessorStrategy = eventProcessorStrategy;
		this.responseStrategy = responseStrategy;
		this.exceptionResponseStrategy = exceptionResponseStrategy;
	}

	/**
	 * @see wicket.request.compound.AbstractCompoundRequestCycleProcessor#getRequestTargetResolverStrategy()
	 */
	protected final IRequestTargetResolverStrategy getRequestTargetResolverStrategy()
	{
		return requestTargetResolverStrategy;
	}

	/**
	 * @see wicket.request.compound.AbstractCompoundRequestCycleProcessor#getEventProcessorStrategy()
	 */
	protected final IEventProcessorStrategy getEventProcessorStrategy()
	{
		return eventProcessorStrategy;
	}

	/**
	 * @see wicket.request.compound.AbstractCompoundRequestCycleProcessor#getResponseStrategy()
	 */
	protected final IResponseStrategy getResponseStrategy()
	{
		return responseStrategy;
	}

	/**
	 * @see wicket.request.compound.AbstractCompoundRequestCycleProcessor#getExceptionResponseStrategy()
	 */
	protected final IExceptionResponseStrategy getExceptionResponseStrategy()
	{
		return exceptionResponseStrategy;
	}
}
