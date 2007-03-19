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
package wicket.behavior;

import wicket.Component;
import wicket.markup.html.IHeaderContributor;
import wicket.markup.html.IHeaderResponse;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.util.lang.Objects;

/**
 * A simple header contributor that just spits out the string it is constructed
 * with as a header contribution.
 * 
 * @author Eelco Hillenius
 */
public class StringHeaderContributor extends AbstractHeaderContributor
{
	private static final long serialVersionUID = 1L;

	/** the contributor instance. */
	private final StringContributor contributor;

	/**
	 * Simply writes out the string it was constructed with whenever it is
	 * called for a header contribution.
	 */
	private static final class StringContributor implements IHeaderContributor
	{
		private static final long serialVersionUID = 1L;

		/** The contribution as a model that returns a plain string. */
		private final IModel contribution;

		/** Temp var for the actual contribution. */
		private transient String contributionAsString;

		/**
		 * Construct.
		 * 
		 * @param contribution
		 *            The contribution as a plain string
		 */
		public StringContributor(String contribution)
		{
			if (contribution == null)
			{
				throw new IllegalArgumentException("argument contribition must be not null");
			}

			this.contribution = new Model(contribution);
		}

		/**
		 * Construct.
		 * 
		 * @param contribution
		 *            The contribution as a model that returns a plain string
		 */
		public StringContributor(IModel contribution)
		{
			if (contribution == null)
			{
				throw new IllegalArgumentException("argument contribition must be not null");
			}

			this.contribution = contribution;
		}

		/**
		 * @see wicket.markup.html.IHeaderContributor#renderHead(wicket.Response)
		 */
		public void renderHead(IHeaderResponse response)
		{
			Object object = contribution.getObject();
			if (object != null)
			{
				response.getResponse().println(object.toString());
			}
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			Object object = contribution.getObject();
			return (object != null) ? object.hashCode() : 0;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			if (obj instanceof StringContributor)
			{
				Object thisContrib = contribution.getObject();
				Object thatContrib = ((StringContributor)obj).contribution.getObject();
				return Objects.equal(thisContrib, thatContrib);
			}
			return false;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return "StringContributor[contribution=" + contribution + "]";
		}
	}

	/**
	 * Construct.
	 * 
	 * @param contribution
	 *            header contribution as a plain string
	 */
	public StringHeaderContributor(String contribution)
	{
		contributor = new StringContributor(contribution);
	}

	/**
	 * Construct.
	 * 
	 * @param contribution
	 *            header contribution as a model that returns a plain string
	 */
	public StringHeaderContributor(IModel contribution)
	{
		contributor = new StringContributor(contribution);
	}

	/**
	 * @see wicket.behavior.AbstractHeaderContributor#getHeaderContributors()
	 */
	public final IHeaderContributor[] getHeaderContributors()
	{
		return new IHeaderContributor[] { contributor };
	}

	/**
	 * @see wicket.behavior.AbstractBehavior#detachModel(wicket.Component)
	 */
	public void detachModel(Component component)
	{
		contributor.contribution.detach();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "StringHeaderContributor[contribution=" + contributor.contribution.getObject()
				+ "]";
	}
}