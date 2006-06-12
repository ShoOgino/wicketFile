package wicket.extensions.ajax.markup.html.repeater.data.sort;

import wicket.ajax.AjaxEventBehavior;
import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.IAjaxCallDecorator;
import wicket.ajax.calldecorator.CancelEventIfNoAjaxDecorator;
import wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import wicket.extensions.markup.html.repeater.data.sort.OrderByLink;

/**
 * Ajaxified {@link OrderByLink}
 * 
 * @see OrderByLink
 * 
 * @since 1.2.1
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class AjaxFallbackOrderByLink extends OrderByLink
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param property
	 * @param stateLocator
	 * @param cssProvider
	 */
	public AjaxFallbackOrderByLink(String id, String property, ISortStateLocator stateLocator,
			ICssProvider cssProvider)
	{
		this(id, property, stateLocator, cssProvider, null);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param property
	 * @param stateLocator
	 */
	public AjaxFallbackOrderByLink(String id, String property, ISortStateLocator stateLocator)
	{
		this(id, property, stateLocator, DefaultCssProvider.getInstance(), null);
	}


	/**
	 * Constructor
	 * 
	 * @param id
	 * @param property
	 * @param stateLocator
	 * @param decorator
	 */
	public AjaxFallbackOrderByLink(String id, String property, ISortStateLocator stateLocator,
			final IAjaxCallDecorator decorator)
	{
		this(id, property, stateLocator, DefaultCssProvider.getInstance(), decorator);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param property
	 * @param stateLocator
	 * @param cssProvider
	 * @param decorator
	 */
	public AjaxFallbackOrderByLink(String id, String property, ISortStateLocator stateLocator,
			ICssProvider cssProvider, final IAjaxCallDecorator decorator)
	{
		super(id, property, stateLocator, cssProvider);

		add(new AjaxEventBehavior("onclick")
		{
			private static final long serialVersionUID = 1L;

			protected void onEvent(AjaxRequestTarget target)
			{
				onClick();
				onAjaxClick(target);
			}

			protected IAjaxCallDecorator getAjaxCallDecorator()
			{
				return new CancelEventIfNoAjaxDecorator(decorator);
			}

		});

	}

	/**
	 * Callback method when an ajax click occurs. All the behavior of changing
	 * the sort, etc is already performed bfore this is called so this method
	 * should primarily be used to configure the target.
	 * 
	 * @param target
	 */
	protected abstract void onAjaxClick(AjaxRequestTarget target);


}
