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
package wicket.markup;

import java.io.Serializable;

import wicket.AttributeModifier;
import wicket.model.Model;
import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * Test case for the component tag attribute modifer test.
 * @author Chris Turner
 */
public class ComponentTagAttributeModifierTest extends TestCase
{

	/**
	 * Create a test case instance.
	 * @param name The test name
	 */
	public ComponentTagAttributeModifierTest(final String name)
	{
		super(name);
	}

	/**
	 *
	 */
	public void testSerializable()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				new Model("model"));
		Assert.assertTrue(modifier instanceof Serializable);
	}

	/**
	 *
	 */
	public void testBadConstructor()
	{
		try
		{
			new AttributeModifier(null, new Model("model"));
			Assert.fail("IllegalArgumentException should be thrown on null attribute name");
		}
		catch (IllegalArgumentException e)
		{
			// Expected result
		}

		try
		{
			new AttributeModifier("test", null);
			Assert.fail("IllegalArgumentException should be thrown on null replace model");
		}
		catch (IllegalArgumentException e)
		{
			// Expected result
		}
	}

    // TODO We need to either rewrite these tests so that they test the public API (the commented-out methods below use methods that are now package private) or we need to move these tests into the wicket package (which i am loathe to do) - JWL
    
	/**
	 *
	 *
	public void testConstructorNoPattern()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				new Model("model"));
		Assert.assertEquals("Attribute shoud be set correctly", "test", modifier.getAttribute());
		Assert.assertNull("Pattern should not be set", modifier.getPattern());
		Assert.assertFalse("Add attributes should not be set", modifier
				.isAddAttributeIfNotPresent());
		Assert.assertEquals("Model should be set correctly", "model", modifier.getReplaceModel()
				.getObject());
	}

	/**
	 *
	 *
	public void testConstructorWithPattern()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				".*[0-9]", new Model("model"));
		Assert.assertEquals("Attribute shoud be set correctly", "test", modifier.getAttribute());
		Assert.assertEquals("Pattern should be set correctly", ".*[0-9]", modifier.getPattern());
		Assert.assertFalse("Add attributes should not be set", modifier
				.isAddAttributeIfNotPresent());
		Assert.assertEquals("Model should be set correctly", "model", modifier.getReplaceModel()
				.getObject());
	}

	/**
	 *
	 *
	public void testConstructorNoPatternAndAddAttribute()
	{
		AttributeModifier modifier = new AttributeModifier("test", true,
				new Model("model"));
		Assert.assertEquals("Attribute shoud be set correctly", "test", modifier.getAttribute());
		Assert.assertNull("Pattern should not be set", modifier.getPattern());
		Assert.assertTrue("Add attributes should be set", modifier.isAddAttributeIfNotPresent());
		Assert.assertEquals("Model should be set correctly", "model", modifier.getReplaceModel()
				.getObject());
	}

	/**
	 *
	 *
	public void testConstructorWithPatternAndAddAttribute()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				".*[0-9]", true, new Model("model"));
		Assert.assertEquals("Attribute shoud be set correctly", "test", modifier.getAttribute());
		Assert.assertEquals("Pattern should be set correctly", ".*[0-9]", modifier.getPattern());
		Assert.assertTrue("Add attributes should be set", modifier.isAddAttributeIfNotPresent());
		Assert.assertEquals("Model should be set correctly", "model", modifier.getReplaceModel()
				.getObject());
	}

	/**
	 *
	 *
	public void testSetAndGetEnabled()
	{
		AttributeModifier modifier = new AttributeModifier("test", true,
				new Model("model"));
		Assert.assertTrue("Should be enabled by default", modifier.isEnabled());
		modifier.setEnabled(false);
		Assert.assertFalse("Should now be disabled", modifier.isEnabled());
	}

	/**
	 *
	 *
	public void testModifyAttributeSuccessfully()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				new Model("model"));
		ComponentTag tag = new ComponentTag(new XmlTag());
		ValueMap attributes = tag.getAttributes();
		attributes.put("test", "oldValue");
		modifier.replaceAttibuteValue(tag);
		attributes = tag.getAttributes();
		Assert.assertEquals("Tag attribute should have been modified", "model", attributes
				.getString("test"));
	}

	/**
	 *
	 *
	public void testModifyAttributeSuccessfullyMatchingPattern()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				"old[A-Z].*", new Model("model"));
		ComponentTag tag = new ComponentTag(new XmlTag());
		ValueMap attributes = tag.getAttributes();
		attributes.put("test", "oldValue");
		modifier.replaceAttibuteValue(tag);
		attributes = tag.getAttributes();
		Assert.assertEquals("Tag attribute should have been modified", "model", attributes
				.getString("test"));

	}

	/**
	 *
	 *
	public void testModifyAttributeUnsuccessfullyMatchingPattern()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				"old[0-9].*", new Model("model"));
		ComponentTag tag = new ComponentTag(new XmlTag());
		ValueMap attributes = tag.getAttributes();
		attributes.put("test", "oldValue");
		modifier.replaceAttibuteValue(tag);
		attributes = tag.getAttributes();
		Assert.assertEquals("Tag attribute should not have been modified", "oldValue", attributes
				.getString("test"));

	}

	/**
	 *
	 *
	public void testModifyAttributeUnknownAttribute()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				new Model("model"));
		ComponentTag tag = new ComponentTag(new XmlTag());
		ValueMap attributes = tag.getAttributes();
		attributes.put("other", "value");
		modifier.replaceAttibuteValue(tag);
		attributes = tag.getAttributes();
		Assert.assertEquals("Tag attributes should not have been modified", "value", attributes
				.getString("other"));
		Assert.assertNull("Attribute should not have been added", attributes.get("test"));
	}

	/**
	 *
	 *
	public void testModifyAttributeAddMissingAttribute()
	{
		AttributeModifier modifier = new AttributeModifier("test", true,
				new Model("model"));
		ComponentTag tag = new ComponentTag(new XmlTag());
		ValueMap attributes = tag.getAttributes();
		attributes.put("other", "value");
		modifier.replaceAttibuteValue(tag);
		attributes = tag.getAttributes();
		Assert.assertEquals("Tag attributes should not have been modified", "value", attributes
				.getString("other"));
		Assert.assertEquals("Attribute should have been added", "model", attributes.get("test"));
	}

	/**
	 *
	 *
	public void testDisabledModification()
	{
		AttributeModifier modifier = new AttributeModifier("test",
				new Model("model"));
		modifier.setEnabled(false);
		ComponentTag tag = new ComponentTag(new XmlTag());
		ValueMap attributes = tag.getAttributes();
		attributes.put("test", "oldValue");
		modifier.replaceAttibuteValue(tag);
		attributes = tag.getAttributes();
		Assert.assertEquals("Tag attribute should not have been modified", "oldValue", attributes
				.getString("test"));
	}
    */
}
