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
package wicket.markup.html.form;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Component;
import wicket.IModel;
import wicket.Model;
import wicket.Page;
import wicket.PropertyModel;
import wicket.RenderException;
import wicket.RequestCycle;
import wicket.FeedbackMessages;
import wicket.markup.ComponentTag;
import wicket.markup.html.HtmlContainer;
import wicket.markup.html.form.validation.IValidationErrorHandler;
import wicket.markup.html.form.validation.ValidationErrorMessage;
import wicket.markup.html.form.validation.ValidationErrorModelDecorator;
import wicket.protocol.http.HttpRequestCycle;


/**
 * Base class for HTML forms.
 * TODO elaborate documentation (validation, cookies, etc...)
 *
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 * @author Eelco Hillenius
 */
public abstract class Form extends HtmlContainer implements IFormSubmitListener
{
    /** Log. */
    private static Log log = LogFactory.getLog(Form.class);

    static
    {
        // Allow use of IFormSubmitListener interface
        RequestCycle.registerListenerInterface(IFormSubmitListener.class);
    }

    /** The validation error handling delegate. */
    private final IValidationErrorHandler validationErrorHandler;

    /** Manager responsible to persist and retrieve FormComponent data. */
	private IFormComponentPersistenceManager persister = null;

    /**
     * Constructor.
     * @param name Name of this form component
     * @param validationErrorHandler Interface to a component that can handle/display
     *            validation errors
     */
    public Form(final String name, final IValidationErrorHandler validationErrorHandler)
    {
        super(name);
        this.validationErrorHandler = validationErrorHandler;
    }

    /**
     * Constructor that uses the provided {@link IModel} as its model. All components have
     * names. A component's name cannot be null.
     * @param name The non-null name of this component
     * @param model the model
     * @param validationErrorHandler Interface to a component that can handle/display
     *            validation errors
     * @throws RenderException Thrown if the component has been given a null name.
     */
    public Form(String name, IModel model, final IValidationErrorHandler validationErrorHandler)
    {
        super(name, model);
        this.validationErrorHandler = validationErrorHandler;
    }

    /**
     * Constructor that uses the provided instance of {@link IModel} as a dynamic model.
     * This model will be wrapped in an instance of {@link PropertyModel} using the
     * provided expression. Thus, using this constructor is a short-hand for:
     * 
     * <pre>
     * new MyComponent(name, new PropertyModel(myIModel, expression));
     * </pre>
     * 
     * All components have names. A component's name cannot be null.
     * @param name The non-null name of this component
     * @param model the instance of {@link IModel} from which the model object will be
     *            used as the subject for the given expression
     * @param expression the OGNL expression that works on the given object
     * @param validationErrorHandler Interface to a component that can handle/display
     *            validation errors
     * @throws RenderException Thrown if the component has been given a null name.
     */
    public Form(String name, IModel model, String expression,
            final IValidationErrorHandler validationErrorHandler)
    {
        super(name, model, expression);
        this.validationErrorHandler = validationErrorHandler;
    }

    /**
     * Constructor that uses the provided object as a simple model. This object will be
     * wrapped in an instance of {@link Model}. All components have names. A component's
     * name cannot be null.
     * @param name The non-null name of this component
     * @param object the object that will be used as a simple model
     * @param validationErrorHandler Interface to a component that can handle/display
     *            validation errors
     * @throws RenderException Thrown if the component has been given a null name.
     */
    public Form(String name, Serializable object,
            final IValidationErrorHandler validationErrorHandler)
    {
        super(name, object);
        this.validationErrorHandler = validationErrorHandler;
    }

    /**
     * Constructor that uses the provided object as a dynamic model. This object will be
     * wrapped in an instance of {@link Model} that will be wrapped in an instance of
     * {@link PropertyModel}using the provided expression. Thus, using this constructor
     * is a short-hand for:
     * 
     * <pre>
     * new MyComponent(name, new PropertyModel(new Model(object), expression));
     * </pre>
     * 
     * All components have names. A component's name cannot be null.
     * @param name The non-null name of this component
     * @param object the object that will be used as the subject for the given expression
     * @param expression the OGNL expression that works on the given object
     * @param validationErrorHandler Interface to a component that can handle/display
     *            validation errors
     * @throws RenderException Thrown if the component has been given a null name.
     */
    public Form(String name, Serializable object, String expression,
            final IValidationErrorHandler validationErrorHandler)
    {
        super(name, object, expression);
        this.validationErrorHandler = validationErrorHandler;
    }

    /**
     * Implemented by subclasses to deal with form submits.
     * @param cycle The request cycle
     */
    public abstract void handleSubmit(RequestCycle cycle);

    /**
     * Sets error messages for form. First all childs (form components) are asked to
     * do their part of error handling, and after that, the registered (if any)
     * error handler of this form is called.
     * @param errors the recorded errors
     */
    public final void handleErrors(final FeedbackMessages errors)
    {
        // call the validation handlers of the child components of this form
        // Traverse parents from this component up to the containing form
        // component calling any IValidationErrorHandler.validationError methods found
        // along the way.
        visitChildren(IValidationErrorHandler.class, new IVisitor()
        {
            public Object component(final Component component)
            {
                // call validation error handler
                ((IValidationErrorHandler) component).validationError(errors);
                // Stop at Form
                return (component instanceof Form) ? STOP_TRAVERSAL : CONTINUE_TRAVERSAL;
            }
        });

        // call the validation handler that is registered with this form, if any
        if (validationErrorHandler != null)
        {
            validationErrorHandler.validationError(errors);
        }
    }

    /**
     * Convenience method in case there is one known (validation) error that is to be
     * registered with the form directly.
     * @param message the message
     */
    public final void handleError(final ValidationErrorMessage message)
    {
        handleErrors(FeedbackMessages.get().add(message));
    }

    /**
     * Handles form submissions.
     * @param cycle The request cycle
     */
    public final void formSubmitted(final RequestCycle cycle)
    {
        // Redirect back to result to avoid postback warnings. But we turn
        // redirecting on as the first thing because the user's handleSubmit
        // implementation may wish to redirect somewhere else. In that case,
        // they can simply call setRedirect(false) in handleSubmit.
        cycle.setRedirect(true);

        // Validate model
        final FeedbackMessages messages = validate();

        // Update model using form data
        updateFormComponentModels(cycle);

        // persist FormComponents if requested
        persistFormComponentData(cycle);

        if (messages.hasErrorMessages())
        {
            // handle any validation error
            handleErrors(messages.getErrorMessages());
        }
        else
        {
            handleSubmit(cycle);
        }
    }

    /**
     * Update the model of all form components.
     * @param cycle The request cycle
     * @see wicket.markup.html.form.FormComponent#updateModel(wicket.RequestCycle)
     */
    private void updateFormComponentModels(final RequestCycle cycle)
    {
        visitChildren(FormComponent.class, new IVisitor()
        {
            public Object component(final Component component)
            {
                // Update model of form component
                final FormComponent formComponent = (FormComponent) component;

                formComponent.updateModel(cycle);

                return CONTINUE_TRAVERSAL;
            }
        });
    }

    /**
     * Validates all children of this form, recording all messages that are returned by the
     * validators.
     * @return the list of validation messages that were recorded during validation
     */
    private FeedbackMessages validate()
    {
        final FeedbackMessages messages = FeedbackMessages.get();
        visitChildren(FormComponent.class, new IVisitor()
        {
            public Object component(final Component component)
            {
                ValidationErrorMessage message = ((FormComponent) component).validate();
                if(message != ValidationErrorMessage.NO_MESSAGE)
                {
                    if(log.isDebugEnabled())
                    {
                        log.debug("validation error: " + message);
                    }
                    messages.add(message);
                    // replace the model
                    ValidationErrorModelDecorator deco =
                        new ValidationErrorModelDecorator(component, message.getInput());
                    component.setModel(deco);
                }
                return IVisitor.CONTINUE_TRAVERSAL; // continue until the end
            }
        });
        return messages;
    }
    
    /**
     * Gets the form component persistence manager; it is lazy loaded.
     * @param cycle the request cycle
     * @return the form component persistence manager
     */
    private IFormComponentPersistenceManager getFormComponentPersistenceManager(
            final RequestCycle cycle) 
    {
    	if (persister == null) 
    	{
    		persister = new FormComponentPersistenceManager();
    	}
    	return persister;
    }

    /**
     * Sets the FormComponentPersistenceManager.
     * @param persister the FormComponentPersistenceManager
     */
    protected void setFormComponentPersistenceManager(
            IFormComponentPersistenceManager persister)
    {
        this.persister = persister;
    }

    /**
     * Persist (e.g. Cookie) FormComponent data to be reloaded and re-assigned
     * to the FormComponent automatically when the page is visited by the 
     * user next time. 
     * 
     * @param cycle The request cycle
     * @see wicket.markup.html.form.FormComponent#updateModel(wicket.RequestCycle)
     */
    private void persistFormComponentData(final RequestCycle cycle)
    {
        // Cannot add cookies to request cycle unless it accepts them
        // We could conceivably be HTML over some other protocol!
        if (cycle instanceof HttpRequestCycle)
        {
            // The persistence manager responsible to persist and retrieve FormComponent data
            final IFormComponentPersistenceManager persister =
                getFormComponentPersistenceManager(cycle);

            // Search for FormComponent children. Ignore all other
            visitChildren(FormComponent.ICookieValue.class, new IVisitor()
            {
                public Object component(final Component component)
                {
                    // Can only a FormComponent
                    final FormComponent formComponent = (FormComponent) component;

                    // If peristence is switched on for that FormComponent ...
                    if (formComponent.isPersistenceEnabled())
                    {
                        // Save component's data (e.g. in a cookie)
                        persister.save(formComponent.getPageRelativePath(),
                                ((FormComponent.ICookieValue) formComponent).getCookieValue());
                    }
                    else
                    {
                        // Remove component's data (e.g. cookie)
                        persister.remove(formComponent.getPageRelativePath());

                    }
                    return CONTINUE_TRAVERSAL;
                }
            });
        }
    }

    /**
     * Retrieves FormComponent values related to the page using the persister
     * and assign the values to the FormComponent. Thus initializing them.
     * NOTE: THIS METHOD IS FOR INTERNAL USE ONLY AND IS NOT MEANT TO BE USED BY
     * FRAMEWORK CLIENTS. IT MAY BE REMOVED IN THE FUTURE.
     * 
     * @param cycle the request cycle
     */
    final public void setFormComponentValuesFromPersister(final RequestCycle cycle)
    {
		// Visit all FormComponent contained in the page
        visitChildren(FormComponent.class, new Component.IVisitor()
        {
            // For each FormComponent found on the Page (not Form)
            public Object component(final Component component)
            {
                // Component must implement persister interface and
                // persistence for that component must be enabled.
                // Else ignore the persisted value. It'll be deleted
                // once the user submits the Form containing that FormComponent.
                // Note: if that is true, values may remain persisted longer
                // than really necessary
                if (component instanceof FormComponent.ICookieValue
                        && ((FormComponent)component).isPersistenceEnabled())
                {
                    // The persistence manager responsible to persist and retrieve
                    // FormComponent data
                    final IFormComponentPersistenceManager persister =
                        getFormComponentPersistenceManager(cycle);

                    // Retrieve persisted value
                    final String persistedValue =
                        persister.retrieveValue(component.getPageRelativePath());
                    if (persistedValue != null)
                    {
                        // Assign the retrieved/persisted value to the component
                        ((FormComponent.ICookieValue) component).setCookieValue(persistedValue);
                    }
                }
                return CONTINUE_TRAVERSAL;
            }
        });
    }
	
	/**
	 * Removes already persisted data for all FormComponent childs and disable
	 * persistence for the same components. 
	 *
	 * @see Page#removePersistedFormData(RequestCycle, Class, boolean)
	 *  
	 * @param cycle Current RequestCycle (may belong to another page though)
	 * @param disablePersistence if true, disable persistence for all
	 * FormComponents on that page. If false, it will remain unchanged. 
	 */
	public void removePersistedFormComponentData(
	        final RequestCycle cycle, final boolean disablePersistence)
	{
		// The persistence manager responsible to persist and retrieve FormComponent data
    	final IFormComponentPersistenceManager persister =
    	    getFormComponentPersistenceManager(cycle);

		// Search for FormComponents like TextField etc.
		visitChildren(FormComponent.class, new IVisitor()
        {
            public Object component(final Component component)
            {
        		// remove the FormComponents persisted data
            	FormComponent formComponent = (FormComponent)component;
           		persister.remove(formComponent.getPageRelativePath());
           		
           		// Disable persistence if requested. Leave unchanged otherwise.
            	if (formComponent.isPersistenceEnabled() && disablePersistence)
            	{
            		formComponent.setPersistenceEnabled(false);
            	}
                return CONTINUE_TRAVERSAL;
            }
        });
	}

    /**
     * @see wicket.Component#handleComponentTag(RequestCycle, ComponentTag)
     */
    protected void handleComponentTag(final RequestCycle cycle, final ComponentTag tag)
    {
        checkTag(tag, "form");
        super.handleComponentTag(cycle, tag);
        tag.put("method", "POST");
        tag.put("action", cycle.urlFor(Form.this, IFormSubmitListener.class));
    }

}
