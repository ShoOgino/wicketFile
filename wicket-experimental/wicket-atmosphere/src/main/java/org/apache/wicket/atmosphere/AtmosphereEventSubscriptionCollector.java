package org.apache.wicket.atmosphere;

import java.lang.reflect.Method;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.application.IComponentOnBeforeRenderListener;

public class AtmosphereEventSubscriptionCollector implements IComponentOnBeforeRenderListener
{
	private EventBus eventBus;

	public AtmosphereEventSubscriptionCollector(EventBus eventBus)
	{
		this.eventBus = eventBus;
	}

	@Override
	public void onBeforeRender(Component component)
	{
		for (Method curMethod : component.getClass().getMethods())
		{
			if (curMethod.isAnnotationPresent(Subscribe.class))
			{
				Class< ? >[] params = curMethod.getParameterTypes();
				if (params.length != 2 || !params[0].equals(AjaxRequestTarget.class))
					throw new WicketRuntimeException("@Subscribe can only be used on "
						+ "methods with 2 params, of which the first is AjaxRequestTarget. "
						+ curMethod + " does conform to this signature.");
				subscribeComponent(component, curMethod);
			}
		}
	}

	private void subscribeComponent(Component component, Method method)
	{
		EventSubscription subscription = new EventSubscription(component, method);
		Page page = component.getPage();
		eventBus.register(page, subscription);
		if (page.getBehaviors(AtmosphereBehavior.class).isEmpty())
			page.add(new AtmosphereBehavior());
	}
}
