/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.examples.forminput;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wicket.FeedbackMessages;
import wicket.PageParameters;
import wicket.RequestCycle;
import wicket.Session;
import wicket.examples.util.NavigationPanel;
import wicket.markup.html.HtmlPage;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.IOnChangeListener;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.AbstractValidator;
import wicket.markup.html.form.validation.IValidationErrorHandler;
import wicket.markup.html.form.validation.RequiredValidator;
import wicket.markup.html.form.validation.ValidationErrorMessage;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.Model;
import wicket.model.PropertyModel;

/**
 * Example for form input.
 *
 * @author Eelco Hillenius
 */
public class FormInput extends HtmlPage
{
	/** the current locale. */
	private Locale currentLocale;

	/**
	 * All available locales.
	 */
	private static final List ALL_LOCALES = Arrays.asList(Locale.getAvailableLocales());

    /**
     * Constructor
     * @param parameters Page parameters
     */
    public FormInput(final PageParameters parameters)
    {
        add(new NavigationPanel("mainNavigation", "Form Input Example"));
	    FeedbackPanel feedback = new FeedbackPanel("feedback");
	    add(feedback);
        add(new InputForm("inputForm", feedback));
        currentLocale = RequestCycle.get().getSession().getLocale();
        add(new LocaleSelect("localeSelect", this, "currentLocale", ALL_LOCALES));
        add(new Link("defaultLocaleLink"){

			public void linkClicked(RequestCycle cycle)
			{
				FormInput.this.currentLocale = RequestCycle.get().getSession().getLocale();
			}
        	
        });
    }

	/**
	 * Gets currentLocale.
	 * @return currentLocale
	 */
	public Locale getCurrentLocale()
	{
		return currentLocale;
	}

	/**
	 * Sets currentLocale.
	 * @param currentLocale currentLocale
	 */
	public void setCurrentLocale(Locale currentLocale)
	{
		this.currentLocale = currentLocale;
	}

    /**
     * Temporarily set the currentLocale property at this session for rendering, and
     * re-set the former locale after rendering.
     * @see wicket.Container#handleRender(wicket.RequestCycle)
     */
    protected void handleRender(final RequestCycle cycle)
    {
        final Session session = cycle.getSession();
        // keep the current locale
        Locale userLocale = session.getLocale();
        // replace the locale for rendering
        session.setLocale(currentLocale);
        try
        {
            super.handleRender(cycle);
        }
        finally
        {
            // set the user's locale back again
            session.setLocale(userLocale);
        }
    }

    /** Form for input. */
    private static class InputForm extends Form
    {
    	/** object to apply input on. */
    	private TestInputObject input = new TestInputObject();

		/**
		 * Construct.
		 * @param name componentnaam
		 * @param validationErrorHandler error handler
		 */
		public InputForm(String name, IValidationErrorHandler validationErrorHandler)
		{
			super(name, validationErrorHandler);
			RequiredValidator requiredValidator = new RequiredValidator();

			TextField stringInput = new TextField("stringInput", input, "stringProperty");
			stringInput.add(requiredValidator);
			TextField integerInput = new TextField("integerInput", input, "integerProperty");
			integerInput.add(requiredValidator);
			TextField doubleInput = new TextField("doubleInput", input, "doubleProperty");
			doubleInput.add(requiredValidator);
			TextField dateInput = new TextField("dateInput", input, "dateProperty");
			dateInput.add(requiredValidator);
			add(stringInput);
			add(integerInput);
			add(doubleInput);
			add(dateInput);

			TextField integerInRangeInput =
				new TextField("integerInRangeInput", input, "integerInRangeProperty");
			integerInRangeInput.add(requiredValidator);
			// an example of how to create a custom validator; extends AbstractValidator
			// for the convenience error messages
			integerInRangeInput.add(new AbstractValidator(){

				public ValidationErrorMessage validate(
						Serializable input, FormComponent component)
				{
					int value = Integer.parseInt(input.toString());
					if((value < 0) || (value > 100))
					{
						Map vars = new HashMap();
						vars.put("input", input);
						vars.put("lower", "0");
						vars.put("upper", "100");
						return errorMessage("error.outOfRange", vars, input, component);
					}
					return ValidationErrorMessage.NO_MESSAGE; // same as null
				}
				
			});
			add(integerInRangeInput);
		}

		/**
		 * @see wicket.markup.html.form.Form#handleSubmit(wicket.RequestCycle)
		 */
		public void handleSubmit(RequestCycle cycle)
		{
			// everything went well; just display a message
			FeedbackMessages.info(this, "form saved");
		}
    }

    /**
     * Dropdown for selecting the locale.
     */
    private static class LocaleSelect extends DropDownChoice implements IOnChangeListener
    {
		/**
		 * Construct.
		 * @param name componentname
		 * @param object object
		 * @param expression ognl expression
		 * @param values list of values
		 */
		public LocaleSelect(String name, Serializable object, String expression,
				Collection values)
		{
			// construct a property model WITHOUT formatting
			super(name, new PropertyModel(new Model(object), expression, false), values);
		}

		/**
		 * @see wicket.markup.html.form.DropDownChoice#selectionChanged(wicket.RequestCycle, java.lang.Object)
		 */
		public void selectionChanged(RequestCycle cycle, Object newSelection)
		{
			// In a real life app, you would probably want to set the session's
			// current locale to the new selection. For this example, we just use
			// the currentLocale instance variable of this page for rendering.
			// As we used this page as the subject of the property model, and the
			// expression 'currentLocale', this property will allways have the selected
			// value; we do not have to do anything here.
		}
    }
}