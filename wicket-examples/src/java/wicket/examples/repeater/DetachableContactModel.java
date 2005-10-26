package wicket.examples.repeater;

import wicket.Component;
import wicket.extensions.markup.html.repeater.pageable.IItemReuseStrategy;
import wicket.model.AbstractReadOnlyDetachableModel;
import wicket.model.IModel;

/**
 * detachable model for an instance of contact
 * 
 * @author igor
 * 
 */
public class DetachableContactModel extends AbstractReadOnlyDetachableModel
{
	private long id;
	private transient Contact contact;

	protected ContactsDatabase getContactsDB()
	{
		return DatabaseLocator.getDatabase();
	}

	/**
	 * @param c
	 */
	public DetachableContactModel(Contact c)
	{
		this(c.getId());
		contact = c;
	}

	/**
	 * @param id
	 */
	public DetachableContactModel(long id)
	{
		if (id == 0)
			throw new IllegalArgumentException();
		this.id = id;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#getNestedModel()
	 */
	public IModel getNestedModel()
	{
		return null;
	}

	protected void onAttach()
	{
		if (contact == null)
		{
			contact = getContactsDB().get(id);
		}
	}

	protected void onDetach()
	{
		contact = null;
	}

	protected Object onGetObject(Component component)
	{
		return contact;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return new Long(id).hashCode();
	}

	/**
	 * used for dataview with ReuseIfModelsEqualStrategy item reuse strategy
	 * 
	 * @see wicket.extensions.markup.html.repeater.pageable.AbstractPageableView#setItemReuseStrategy(IItemReuseStrategy)
	 * @see wicket.extensions.markup.html.repeater.pageable.ReuseIfModelsEqualStrategy
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof DetachableContactModel)
		{
			DetachableContactModel other = (DetachableContactModel)obj;
			return other.id == this.id;
		}
		return false;
	}
}
