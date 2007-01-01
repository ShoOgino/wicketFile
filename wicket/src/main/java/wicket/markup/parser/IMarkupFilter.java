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
package wicket.markup.parser;

import java.text.ParseException;

import wicket.markup.MarkupElement;

/**
 * Wicket uses a streaming XML parser to read the markup. A chain of
 * IMarkupFilters is used e.g. to remove comments, allow for html typical markup
 * (some tags don't need to be closed explicitly), etc.
 * <p>
 * The streaming XML parser must implement IMarkupFilter itself and thus is
 * usually the first element of the chain.
 * 
 * @see wicket.markup.MarkupParser
 * @author Juergen Donnerstag
 */
public interface IMarkupFilter
{
	/** Default execution priority */
	final int DEFAULT_PRIORITY = 50;

	/**
	 * IMarkupFilters are usually chained with the last filter being an XML
	 * parser. The getParent() method returns the next filter in the chain.
	 * 
	 * @return The next filter in the chain, or null if the last one.
	 */
	IMarkupFilter getParent();

	/**
	 * Get the next MarkupElement from the parent MarkupFilter and handle it if
	 * the specific filter criteria are met. Depending on the filter, it may
	 * return the MarkupElement unchanged, modified or it remove by asking the
	 * parent handler for the next tag.
	 * 
	 * @return Return the next eligible MarkupElement
	 * @throws ParseException
	 */
	MarkupElement nextTag() throws ParseException;

	/**
	 * Set parent filter.
	 * 
	 * @param parent
	 *            The parent of this component The next element in the chain
	 */
	void setParent(final IMarkupFilter parent);

	/**
	 * Filters with a low priority numbers are executed before filters with
	 * higher numbers.
	 * <p>
	 * Note that the priority is only used while registering the filter with the
	 * MarkupParser to determine the filters position within the list of
	 * filters.
	 * 
	 * @param priority
	 * @return this
	 */
	IMarkupFilter setPriority(final int priority);

	/**
	 * Get the filters priority
	 * 
	 * @return execution prio
	 */
	int getPriority();
}
