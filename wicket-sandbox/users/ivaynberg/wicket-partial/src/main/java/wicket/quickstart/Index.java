package wicket.quickstart;

import java.util.Date;

import wicket.Component;
import wicket.PageParameters;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.model.AbstractReadOnlyModel;
import wicket.model.PropertyModel;
import wicket.quickstart.partial.AjaxIdSetter;
import wicket.quickstart.partial.AjaxLink;
import wicket.quickstart.partial.AjaxRequestTarget;
import wicket.quickstart.partial.IndicatingAjaxLink;
import wicket.quickstart.partial.timer.AjaxSelfUpdatingTimerBehavior;
import wicket.quickstart.partial.timer.AjaxTimerBehavior;

/**
 * Basic bookmarkable index page.
 * 
 * NOTE: You can get session properties from QuickStartSession via
 * getQuickStartSession()
 */
public class Index extends QuickStartPage
{
	int counter = 0;

	int counter2 = 100;

	public int getCounter()
	{
		return counter;
	}

	public int getCounter2()
	{
		return counter2;
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public Index(final PageParameters parameters)
	{
		final Label counterLabel;
		final Label counterLabel2;

		counterLabel = new Label("counter-label", new PropertyModel(this, "counter"));
		counterLabel.add(AjaxIdSetter.INSTANCE);

		counterLabel2 = new Label("counter-label2", new PropertyModel(this, "counter2"));
		counterLabel2.add(AjaxIdSetter.INSTANCE);

		add(counterLabel);
		add(counterLabel2);

		add(new IndicatingAjaxLink("ajax-link")
		{
			public void onClick(AjaxRequestTarget target)
			{
				counter++;
				counter2++;

				target.addComponent(counterLabel);
				target.addComponent(counterLabel2);

				String js = "alert('counters " + counter + " " + counter2 + "')";
				target.addJavascript(js);

			}
		});

		Label label = new Label("clock", new DateModel());
		label.add(AjaxIdSetter.INSTANCE); // roll this into abstract ajax handler?
		label.add(new AjaxSelfUpdatingTimerBehavior(1000));
		add(label);
	}

	private static final class DateModel extends AbstractReadOnlyModel
	{

		public Object getObject(Component component)
		{
			return new Date().toString();
		}

	}
}
