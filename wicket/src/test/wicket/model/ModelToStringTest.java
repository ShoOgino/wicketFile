package wicket.model;

import java.util.Properties;

import wicket.Component;
import wicket.MockPageWithOneComponent;
import wicket.WicketTestCase;
import wicket.markup.html.basic.Label;

/**
 * Tests the toString() method on the models in the wicket.model package.
 */
public class ModelToStringTest extends WicketTestCase
{
	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public ModelToStringTest(String name)
	{
		super(name);
	}

	/**
	 * Used for models in testing.
	 */
	private static class InnerPOJO
	{
	}

	/**
	 * Tests the Model.toString() method.
	 */
	public void testModel()
	{
		Model emptyModel = new Model<String>();
		String expected = "Model:classname=[wicket.model.Model]:nestedModel=[null]:object=[null]";
		assertEquals(expected, emptyModel.toString());

		Model stringModel = new Model<String>("foo");
		expected = "Model:classname=[wicket.model.Model]:nestedModel=[null]:object=[foo]";
		assertEquals(expected, stringModel.toString());
	}

	/**
	 * Tests the PropertyModel.toString() method.
	 */
	public void testPropertyModel()
	{
		PropertyModel emptyModel = new PropertyModel("", null);
		
		
		String expected = "Model:classname=[wicket.model.PropertyModel]:attached=false:nestedModel=[]:expression=[null]";
		assertEquals(expected, emptyModel.toString());

		Properties properties = new Properties();
		properties.put("name", "foo");
		PropertyModel stringProperty = new PropertyModel(properties, "name");

		expected = "Model:classname=[wicket.model.PropertyModel]:attached=false:nestedModel=[{name=foo}]:expression=[name]";
		assertEquals(expected, stringProperty.toString());

		stringProperty.attach();
		expected = "Model:classname=[wicket.model.PropertyModel]:attached=true:nestedModel=[{name=foo}]:expression=[name]";
		assertEquals(expected, stringProperty.toString());

		InnerPOJO innerPOJO = new InnerPOJO();
		PropertyModel<Integer> pojoProperty = new PropertyModel<Integer>(innerPOJO, "pojo");

		expected = "Model:classname=[wicket.model.PropertyModel]:attached=false:nestedModel=["
				+ innerPOJO + "]:expression=[pojo]";
		
		assertEquals(expected, pojoProperty.toString());
	}

	/**
	 * Tests the CompoundPropertyModel.toString() method.
	 */
	public void testCompoundPropertyModel()
	{
		CompoundPropertyModel emptyModel = new CompoundPropertyModel<String>("");
		String expected = "Model:classname=[wicket.model.CompoundPropertyModel]:attached=false:nestedModel=[]";
		assertEquals(expected, emptyModel.toString());

		Properties properties = new Properties();
		properties.put("name", "foo");
		CompoundPropertyModel stringProperty = new CompoundPropertyModel<Properties>(properties);

		expected = "Model:classname=[wicket.model.CompoundPropertyModel]:attached=false:nestedModel=[{name=foo}]";
		assertEquals(expected, stringProperty.toString());

		stringProperty.attach();
		expected = "Model:classname=[wicket.model.CompoundPropertyModel]:attached=true:nestedModel=[{name=foo}]";
		assertEquals(expected, stringProperty.toString());

		InnerPOJO innerPOJO = new InnerPOJO();
		CompoundPropertyModel pojoProperty = new CompoundPropertyModel<InnerPOJO>(innerPOJO);

		expected = "Model:classname=[wicket.model.CompoundPropertyModel]:attached=false:nestedModel=["
				+ innerPOJO + "]";
		assertEquals(expected, pojoProperty.toString());
	}

	/**
	 * Tests the BoundCompoundPropertyModel.toString() method.
	 */
	public void testBoundCompoundPropertyModel()
	{
		BoundCompoundPropertyModel emptyModel = new BoundCompoundPropertyModel<String>("");
		String expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:attached=false:nestedModel=[]:bindings=[]";
		assertEquals(expected, emptyModel.toString());

		Properties properties = new Properties();
		properties.put("name", "foo");
		BoundCompoundPropertyModel stringProperty = new BoundCompoundPropertyModel<Properties>(properties);

		expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:attached=false:nestedModel=[{name=foo}]:bindings=[]";
		assertEquals(expected, stringProperty.toString());

		stringProperty.attach();
		expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:attached=true:nestedModel=[{name=foo}]:bindings=[]";
		assertEquals(expected, stringProperty.toString());

		InnerPOJO innerPOJO = new InnerPOJO();
		BoundCompoundPropertyModel pojoProperty = new BoundCompoundPropertyModel<InnerPOJO>(innerPOJO);

		expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:attached=false:nestedModel=["
				+ innerPOJO + "]:bindings=[]";
		assertEquals(expected, pojoProperty.toString());

		Component component1 = pojoProperty
				.bind(new Label(new MockPageWithOneComponent(), "component"), Integer.class);
		expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:attached=false:nestedModel=["
				+ innerPOJO
				+ "]:bindings=[Binding(:component=["
				+ component1
				+ "]:expression=[component]:type=[class java.lang.Integer])]";
		assertEquals(expected, pojoProperty.toString());
	}

	/**
	 * Test stub for testing AbstractReadOnlyModel.toString()
	 */
	private static class MyAbstractReadOnlyModel extends AbstractReadOnlyModel
	{
		private static final long serialVersionUID = 1L;

		/**
		 * @see AbstractReadOnlyModel#getObject()
		 */
		@Override
		public Object getObject()
		{
			return "FOO";
		}
	}

	/**
	 * Tests AbstractReadOnlyModel.toString().
	 */
	public void testAbstractReadOnlyModel()
	{
		AbstractReadOnlyModel model = new MyAbstractReadOnlyModel();
		String expected = "Model:classname=[" + model.getClass().getName() + "]";
		assertEquals(expected, model.toString());
	}

	/**
	 * Test stub for testing AbstractReadOnlyDetachableModel.toString()
	 */
	private static class MyAbstractReadOnlyDetachableModel extends AbstractReadOnlyDetachableModel
	{
		private static final long serialVersionUID = 1L;

		/**
		 * @return <code>null</code>
		 * @see Model#getNestedModel()
		 */
		@Override
		public IModel getNestedModel()
		{
			return null;
		}

		@Override
		protected void onAttach()
		{
		}

		@Override
		protected void onDetach()
		{
		}

		@Override
		protected Object onGetObject()
		{
			return null;
		}
	}

	/**
	 * Tests AbstractReadOnlyModel.toString().
	 */
	public void testAbstractReadOnlyDetachableModel()
	{
		AbstractReadOnlyDetachableModel model = new MyAbstractReadOnlyDetachableModel();
		String expected = "Model:classname=[" + model.getClass().getName() + "]"
				+ ":attached=false";
		assertEquals(expected, model.toString());
	}

	private static final class MyLoadableDetachableModel extends LoadableDetachableModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected Object load()
		{
			return "foo";
		}
	}

	/**
	 * Tests LoadableDetachableModel.toString()
	 */
	public void testLoadableDetachableModel()
	{
		LoadableDetachableModel model = new MyLoadableDetachableModel();
		String expected = "Model:classname=[" + model.getClass().getName() + "]"
				+ ":attached=false" + ":tempModelObject=[null]";
		assertEquals(expected, model.toString());

		model.getObject();
		expected = "Model:classname=[" + model.getClass().getName() + "]" + ":attached=true"
				+ ":tempModelObject=[foo]";
		assertEquals(expected, model.toString());

		model.detach();
		expected = "Model:classname=[" + model.getClass().getName() + "]" + ":attached=false"
				+ ":tempModelObject=[null]";
		assertEquals(expected, model.toString());
	}
}
