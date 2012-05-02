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
package org.apache.wicket.extensions.markup.html.form.palette.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;


/**
 * Component to keep track of selections on the html side. Also used for encoding and decoding those
 * selections between html and java.
 * 
 * @param <T>
 *            Type of the palette
 * 
 * @author Igor Vaynberg ( ivaynberg )
 */
public class Recorder<T> extends HiddenField<Object>
{
	private static final long serialVersionUID = 1L;

	/**  set of selected ids */
	private final Set<String> ids;

	/** parent palette object */
	private final Palette<T> palette;

	private boolean attached = false;

	/**
	 * @return parent Palette object
	 */
	public Palette<T> getPalette()
	{
		return palette;
	}

	/**
	 * internal access to ids for subclasses
	 * 
	 * @return set of ids
	 */
	protected Set<String> getIds()
	{
		return ids;
	}

	/**
	 * @param id
	 *            component id
	 * @param palette
	 *            parent palette object
	 */
	public Recorder(final String id, final Palette<T> palette)
	{
		super(id);
		this.palette = palette;
		this.ids = new LinkedHashSet<String>(); // hash set with insertion order 
		setDefaultModel(new Model<Serializable>());
		setOutputMarkupId(true);
	}

	/**
	 * 
	 * @see org.apache.wicket.markup.html.form.AbstractTextComponent#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender()
	{
		super.onBeforeRender();

		if (!getForm().hasError())
		{
			initIds();
		}
		attached = true;
	}

	/**
	 * Synchronize ids collection from the palette's model
	 */
	private void initIds()
	{
		// construct the model string based on selection collection
		IChoiceRenderer<T> renderer = getPalette().getChoiceRenderer();
		StringBuilder modelStringBuffer = new StringBuilder();
		Collection<T> modelCollection = getPalette().getModelCollection();
		if (modelCollection == null)
		{
			throw new WicketRuntimeException(
				"Expected getPalette().getModelCollection() to return a non-null value."
					+ " Please make sure you have model object assigned to the palette");
		}
		Iterator<T> selection = modelCollection.iterator();

		int i = 0;
		while (selection.hasNext())
		{
			modelStringBuffer.append(renderer.getIdValue(selection.next(), i++));
			if (selection.hasNext())
			{
				modelStringBuffer.append(",");
			}
		}

		// set model and update ids array
		String modelString = modelStringBuffer.toString();
		setDefaultModel(new Model<String>(modelString));
		updateIds(modelString);
	}


	@Override
	protected void onValid()
	{
		super.onValid();
		if (attached)
		{
			updateIds();
		}
	}

	/**
	 * @return list over selected choices
	 */
	protected List<T> getSelectedList()
	{
		if (getIds().isEmpty())
		{
			return Collections.EMPTY_LIST;
		}

		final IChoiceRenderer<T> renderer = getPalette().getChoiceRenderer();
		final List<T> selected = new ArrayList<T>(getIds().size());
		final Collection<? extends T> choices = getPalette().getChoices();

		for (T choice : choices)
		{
			final String idValue = renderer.getIdValue(choice, 0);
			if (getIds().contains(idValue))
			{
				selected.add(choice);
			}
		}
		return selected;
	}

	/**
	 * @return iterator over selected choices
	 */
	public Iterator<T> getSelectedChoices()
	{
		return getSelectedList().iterator();
	}

	/**
	 * @return list over unselected choices
	 */
	protected List<T> getUnselectedList()
	{
		final Collection<? extends T> choices = getPalette().getChoices();

		if (choices.size() - getIds().size() == 0)
		{
			return Collections.<T>emptyList();
		}

		final IChoiceRenderer<T> renderer = getPalette().getChoiceRenderer();
		final List<T> unselected = new ArrayList<T>(Math.max(1, choices.size() - getIds().size()));

		for (T choice : choices)
		{
			final String idValue = renderer.getIdValue(choice, 0);
			if (!getIds().contains(idValue))
			{
				unselected.add(choice);
			}
		}
		return unselected;
	}

	/**
	 * @return iterator over unselected choices
	 */
	public Iterator<T> getUnselectedChoices()
	{
		return getUnselectedList().iterator();
	}

	@Override
	protected void onInvalid()
	{
		super.onInvalid();
		if (attached)
		{
			updateIds();
		}
	}

	private void updateIds()
	{
		updateIds(getValue());
	}

	protected void updateIds(final String value)
	{
		getIds().clear();

		for (String id : Strings.split(value, ','))
		{
			getIds().add(id);
		}
	}
}
