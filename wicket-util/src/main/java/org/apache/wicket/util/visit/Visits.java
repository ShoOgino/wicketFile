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
package org.apache.wicket.util.visit;

import java.util.Collections;
import java.util.Iterator;

/**
 * Utility class that contains visitor/traversal related code
 */
public class Visits
{
	/** Constructor */
	private Visits()
	{
	}

	private static class SingletonIterable<T> implements Iterable<T>
	{
		private final T singleton;

		public SingletonIterable(T singleton)
		{
			this.singleton = singleton;
		}

		public Iterator<T> iterator()
		{
			return Collections.singleton(singleton).iterator();
		}
	}

	/**
	 * Visits container and its children pre-order (parent first). Children are determined by
	 * calling {@link Iterable#iterator()}.
	 * 
	 * @param <S>
	 *            the type of object that will be visited, notice that {@code container} is not
	 *            declared as {@code Iterable<S>} because it may return a generalization of
	 *            {@code S}
	 * @param <R>
	 *            the type of object that should be returned from the visitor, use {@link Void} if
	 *            no return value is needed
	 * @param container
	 *            object whose children will be visited
	 * @param visitor
	 *            the visitor
	 * @return return value from the {@code visitor} or {@code null} if none
	 */
	public static final <S, R> R visit(Iterable<? super S> container, final IVisitor<S, R> visitor)
	{
		return (R)visitChildren(new SingletonIterable(container), visitor, IVisitFilter.ANY);
	}

	/**
	 * Visits container and its children pre-order (parent first). Children are determined by
	 * calling {@link Iterable#iterator()}.
	 * 
	 * @param <S>
	 *            the type of object that will be visited, notice that {@code container} is not
	 *            declared as {@code Iterable<S>} because it may return a generalization of
	 *            {@code S}
	 * @param <R>
	 *            the type of object that should be returned from the visitor, use {@link Void} if
	 *            no return value is needed
	 * @param container
	 *            object whose children will be visited
	 * @param visitor
	 *            the visitor
	 * @param filter
	 *            filter used to limit the types of objects that will be visited
	 * @return return value from the {@code visitor} or {@code null} if none
	 */
	public static final <S, R> R visit(Iterable<? super S> container, final IVisitor<S, R> visitor,
		IVisitFilter filter)
	{
		return (R)visitChildren(new SingletonIterable(container), visitor, filter);
	}

	/**
	 * Visits children of the specified {@link Iterable} pre-order (parent first). Children are
	 * determined by calling {@link Iterable#iterator()}.
	 * 
	 * @param <S>
	 *            the type of object that will be visited, notice that {@code container} is not
	 *            declared as {@code Iterable<S>} because it may return a generalization of
	 *            {@code S}
	 * @param <R>
	 *            the type of object that should be returned from the visitor, use {@link Void} if
	 *            no return value is needed
	 * @param container
	 *            object whose children will be visited
	 * @param visitor
	 *            the visitor
	 * @param filter
	 *            filter used to limit the types of objects that will be visited
	 * @return return value from the {@code visitor} or {@code null} if none
	 */
	public static final <S, R> R visitChildren(Iterable<? super S> container,
		final IVisitor<S, R> visitor, IVisitFilter filter)
	{
		Visit<R> visit = new Visit<R>();
		visitChildren(container, visitor, filter, visit);
		return visit.getResult();
	}

	private static final <S, R> void visitChildren(Iterable<? super S> container,
		final IVisitor<S, R> visitor, IVisitFilter filter, Visit<R> visit)
	{
		if (visitor == null)
		{
			throw new IllegalArgumentException("argument visitor may not be null");
		}

		// Iterate through children of this container
		for (final Iterator<?> children = container.iterator(); children.hasNext();)
		{
			// Get next child component
			final Object child = children.next();

			// Is the child of the correct class (or was no class specified)?
			if (filter.visitObject(child))
			{
				Visit<R> childTraversal = new Visit<R>();

				// Call visitor
				@SuppressWarnings("unchecked")
				S s = (S)child;
				visitor.component(s, childTraversal);

				if (childTraversal.isStopped())
				{
					visit.stop(childTraversal.getResult());
					return;
				}
				else if (childTraversal.isDontGoDeeper())
				{
					continue;
				}
			}

			// If child is a container
			if (!visit.isDontGoDeeper() && (child instanceof Iterable<?>) &&
				filter.visitChildren(child))
			{
				// visit the children in the container
				visitChildren((Iterable<? super S>)child, visitor, filter, visit);

				if (visit.isStopped())
				{
					return;
				}
			}
		}

		return;
	}

	/**
	 * Visits children of the specified {@link Iterable} pre-order (parent first). Children are
	 * determined by calling {@link Iterable#iterator()}.
	 * 
	 * @param <S>
	 *            the type of object that will be visited, notice that {@code container} is not
	 *            declared as {@code Iterable<S>} because it may return a generalization of
	 *            {@code S}
	 * @param <R>
	 *            the type of object that should be returned from the visitor, use {@link Void} if
	 *            no return value is needed
	 * @param container
	 *            object whose children will be visited
	 * @param visitor
	 *            the visitor
	 * @return return value from the {@code visitor} or {@code null} if none
	 */
	public static final <S, R> R visitChildren(Iterable<? super S> container,
		final IVisitor<S, R> visitor)
	{
		return visitChildren(container, visitor, IVisitFilter.ANY);
	}

	/**
	 * Visits the specified object and any of its children using a post-order (child first)
	 * traversal. Children are determined by calling {@link Iterable#iterator()} if the object
	 * implements {@link Iterable}.
	 * 
	 * @param <S>
	 *            the type of object that will be visited, notice that {@code container} is not
	 *            declared as {@code Iterable<S>} because it may return a generalization of
	 *            {@code S}
	 * @param <R>
	 *            the type of object that should be returned from the visitor, use {@link Void} if
	 *            no return value is needed
	 * @param root
	 *            root object that will be visited
	 * @param visitor
	 *            the visitor
	 * @return return value from the {@code visitor} or {@code null} if none
	 */
	public static final <S, R> R visitPostOrder(S root,
		final org.apache.wicket.util.visit.IVisitor<S, R> visitor)
	{
		return visitPostOrder(root, visitor, IVisitFilter.ANY);
	}

	/**
	 * Visits the specified object and any of its children using a post-order (child first)
	 * traversal. Children are determined by calling {@link Iterable#iterator()} if the object
	 * implements {@link Iterable}.
	 * 
	 * @param <S>
	 *            the type of object that will be visited, notice that {@code container} is not
	 *            declared as {@code Iterable<S>} because it may return a generalization of
	 *            {@code S}
	 * @param <R>
	 *            the type of object that should be returned from the visitor, use {@link Void} if
	 *            no return value is needed
	 * @param root
	 *            root object that will be visited
	 * @param visitor
	 *            the visitor
	 * @param filter
	 *            filter used to limit the types of objects that will be visited
	 * @return return value from the {@code visitor} or {@code null} if none
	 */
	public static final <S, R> R visitPostOrder(Object root,
		final org.apache.wicket.util.visit.IVisitor<S, R> visitor, IVisitFilter filter)
	{
		if (visitor == null)
		{
			throw new IllegalArgumentException("Argument `visitor` cannot be null");
		}

		Visit<R> visit = new Visit<R>();
		visitPostOrderHelper(root, visitor, filter, visit);
		return visit.getResult();
	}

	private static final <S, R> void visitPostOrderHelper(Object component,
		final org.apache.wicket.util.visit.IVisitor<S, R> visitor, IVisitFilter filter,
		Visit<R> visit)
	{

		if (component instanceof Iterable<?>)
		{
			final Iterable<?> container = (Iterable<?>)component;
			if (filter.visitChildren(container))
			{
				Visit<R> childTraversal = new Visit<R>();
				for (final Iterator<?> iterator = ((Iterable<?>)component).iterator(); iterator.hasNext();)
				{
					final Object child = iterator.next();
					visitPostOrderHelper(child, visitor, filter, childTraversal);
					if (childTraversal.isStopped())
					{
						visit.stop(childTraversal.getResult());
						return;
					}
				}
			}
		}
		if (filter.visitObject(component))
		{
			visitor.component((S)component, visit);
		}
	}
}
