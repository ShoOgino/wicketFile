package org.apache.wicket.markup.html.form;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;

/**
 * Restores the rendering behavior of CheckBoxMultipleChoice until Wicket 6.x.
 * That means:
 * <ul>
 *     <li>Uses <xmp><br/></xmp> as a suffix</li>
 *     <li>renders the label after the checkbox</li>
 * </ul>
 *
 * @deprecated
 */
@Deprecated
public class CheckBoxMultipleChoiceWicket6Listener implements IComponentInstantiationListener
{
	@Override
	public void onInstantiation(Component component)
	{
		if (component instanceof CheckBoxMultipleChoice<?>)
		{
			CheckBoxMultipleChoice<?> checkBoxMultipleChoice = (CheckBoxMultipleChoice<?>) component;
			checkBoxMultipleChoice.setSuffix("<br/>\n");
			checkBoxMultipleChoice.setLabelPosition(AbstractChoice.LabelPosition.AFTER);
		}
	}
}
