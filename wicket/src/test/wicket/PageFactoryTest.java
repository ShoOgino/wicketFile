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
package wicket;

import java.io.IOException;

import junit.framework.TestCase;
import wicket.protocol.http.MockHttpApplication;
import wicket.protocol.http.MockPage;
import wicket.resource.DummyApplication;

/**
 * Test the Pagefactory
 */
public class PageFactoryTest extends TestCase
{
    private MockHttpApplication application;
    private DefaultPageFactory factory;

    /**
     * Create the test case.
     *
     * @param message The test name
     */
    public PageFactoryTest(String message) {
        super(message);
    }

    protected void setUp() throws Exception {
        super.setUp();
        application = new DummyApplication();
        factory = new DefaultPageFactory();
    }

    /**
     * Test creating a new page using a class.
     */
    public void testNewPageClass()
    {
        // MyPage0: no constructor at all
        assertEquals(MyPage0.class, factory.newPage(MyPage0.class).getClass());

        // MyPage1 has only a default constructor
        assertEquals(MyPage1.class, factory.newPage(MyPage1.class).getClass());
        
        // MyPage2: PageParameter parameter constructor only
        // will call PageParameter constructor with parameter = null
        assertEquals(MyPage2.class, factory.newPage(MyPage2.class, (PageParameters)null).getClass());
        
        // MyPage3: Page parameter constructor only
        Exception e = null;
        try
        {
            factory.newPage(MyPage3.class).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage3 should have thrown an exception as it does not have a default or no constructor", e);
        
        // MyPage4: Illegal String parameter constructor only
        e = null;
        try
        {
            factory.newPage(MyPage4.class).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage4 should have thrown an exception as it does not have a default or no constructor", e);

        // MyPage5: PageParameter and default constructor 
        assertEquals(MyPage5.class, factory.newPage(MyPage5.class).getClass());

        // String: Illegal String parameter constructor only
        e = null; /*
        try
        {
            factory.newPage(String.class).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("String does not extend Page. Should habe thrown an exception", e);
        */
    }
    
    /**
     * Test a new page using a class and page parameters.
     * @throws IOException
     */
    public void testNewPageClassPageParameters() throws IOException 
    {
        assertEquals(MyPage0.class, factory.newPage(MyPage0.class, (PageParameters)null).getClass());

        // MyPage0: no constructor at all
        assertEquals(MyPage0.class, factory.newPage(MyPage0.class, new PageParameters()).getClass());

        // MyPage1 has only a default constructor
        assertEquals(MyPage1.class, factory.newPage(MyPage1.class, new PageParameters()).getClass());
        
        // MyPage2: PageParameter parameter constructor only
        assertEquals(MyPage2.class, factory.newPage(MyPage2.class, new PageParameters()).getClass());
        
        // MyPage3: Page parameter constructor only
        Exception e = null;
        try
        {
            factory.newPage(MyPage3.class, new PageParameters()).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage4 should have thrown an exception as it does not have a default or no constructor", e);
        
        // MyPage4: Illegal String parameter constructor only
        e = null;
        try
        {
            factory.newPage(MyPage4.class, new PageParameters()).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage4 should have thrown an exception as it does not have a default or no constructor", e);

        // MyPage5: PageParameter and default constructor 
        assertEquals(MyPage5.class, factory.newPage(MyPage5.class, new PageParameters()).getClass());

        // String: Illegal String parameter constructor only
        e = null;
        /*
        try
        {
            factory.newPage(String.class, request).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("String does not extend Page. Should habe thrown an exception", e);
        */
    }
         
    /**
     * Test creating a new page using a class and a page.
     * @throws IOException
     */
    public void testNewPageClassPage() throws IOException 
    {
        final Page page = new MockPage(null);
        
        // MyPage0: no constructor at all
        Exception e = null;
        try
        {
            factory.newPage(MyPage0.class, page).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage0 should have thrown an exception", e);

        // MyPage1 has only a default constructor
        e = null;
        try
        {
            factory.newPage(MyPage1.class, page).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage1 should have thrown an exception", e);
        
        // MyPage2: PageParameter parameter constructor only
        e = null;
        try
        {
            factory.newPage(MyPage2.class, page).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage2 should have thrown an exception as it does not have a default or no constructor", e);
        
        // MyPage3: Page parameter constructor only
        assertEquals(MyPage3.class, factory.newPage(MyPage3.class, page).getClass());
        
        // MyPage4: Illegal String parameter constructor only
        e = null;
        try
        {
            factory.newPage(MyPage4.class, page).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage4 should have thrown an exception as it does not have a default or no constructor", e);

        // MyPage5: PageParameter and default constructor 
        e = null;
        try
        {
            factory.newPage(MyPage5.class, page).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("MyPage5 should have thrown an exception", e);

        // String: Illegal String parameter constructor only
        e = null;
        try
        {
            factory.newPage(String.class, page).getClass();
        }
        catch (WicketRuntimeException ex)
        {
            e = ex;
        }
        assertNotNull("String does not extend Page. Should habe thrown an exception", e);
    }
}
