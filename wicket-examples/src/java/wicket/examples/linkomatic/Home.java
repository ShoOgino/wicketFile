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
package wicket.examples.linkomatic;

import wicket.Page;
import wicket.PageParameters;
import wicket.examples.util.NavigationPanel;
import wicket.markup.html.HtmlPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.IPageLink;
import wicket.markup.html.link.ImageMap;
import wicket.markup.html.link.Link;
import wicket.markup.html.link.OnClickLink;
import wicket.markup.html.link.PageLink;
import wicket.markup.html.link.PopupSpecification;
import wicket.markup.html.link.ExternalLink;


/**
 * Demonstrates different flavors of hyperlinks.
 * @author Jonathan Locke
 */
public class Home extends HtmlPage
{
    /** click count for Link. */
    private int linkClickCount = 0;

    /** click count for OnClickLink. */
    private int onClickLinkClickCount = 0;

    /**
     * Constructor
     * @param parameters Page parameters (ignored since this is the home page)
     */
    public Home(final PageParameters parameters)
    {
        add(new NavigationPanel("mainNavigation", "LinkOMatic example"));

        // Action link counts link clicks
        final Link actionLink = new Link("actionLink")
        {
            public void linkClicked()
            {
                linkClickCount++;
                
                // Redirect back to result to avoid refresh updating the link count
                getRequestCycle().setRedirect(true);
            }
        };
        actionLink.add(new Label("linkClickCount", this, "linkClickCount"));
        add(actionLink);

        // Action link counts link clicks on works with onclick handler
        final OnClickLink actionOnClickLink = new OnClickLink("actionOnClickLink")
        {
            public void linkClicked()
            {
                onClickLinkClickCount++;
                
                // Redirect back to result to avoid refresh updating the link count
                getRequestCycle().setRedirect(true);
            }
        };

        add(actionOnClickLink);
        add(new Label("onClickLinkClickCount", this, "onClickLinkClickCount"));

        // Link to Page1 is a simple external page link 
        add(new BookmarkablePageLink("page1Link", Page1.class));

        // Link to Page2 is automaticLink, so no code
        // Link to Page3 is an external link which takes a parameter
        add(new BookmarkablePageLink("page3Link", Page3.class).setParameter("id", 3));

        // Link to BookDetails page
        add(new PageLink("bookDetailsLink",
                new IPageLink()
            {
                public Page getPage()
                {
                    return new BookDetails(new Book("The Hobbit"));
                }

                public Class getPageClass()
                {
                    return BookDetails.class;
                }
            }));

        // Delayed link to BookDetails page
        add(new PageLink("bookDetailsLink2",
                new IPageLink()
            {
                public Page getPage()
                {
                    return new BookDetails(new Book("Inside The Matrix"));
                }

                public Class getPageClass()
                {
                    return BookDetails.class;
                }
            }));

        // Image map link example
        add(new ImageMap("imageMap").addRectangleLink(0, 0, 100, 100,
                new BookmarkablePageLink("page1", Page1.class))
                                    .addCircleLink(160, 50, 35,
                new BookmarkablePageLink("page2", Page2.class)).addPolygonLink(new int[]
                {
                    212, 79, 241, 4, 279, 54, 212, 79
                }, new BookmarkablePageLink("page3", Page3.class)));

        // Popup example
        PopupSpecification popupSpec = new PopupSpecification().setHeight(100)
                                                               .setWidth(100);

        add(new BookmarkablePageLink("popupLink", Page1.class)
            .setPopupSpecification(popupSpec));
        
        add(new ExternalLink("google", "http://www.google.com",
                "Click this link to go to Google"));
    }

    /**
     * @return Returns the linkClickCount.
     */
    public int getLinkClickCount()
    {
        return linkClickCount;
    }

    /**
     * @param linkClickCount The linkClickCount to set.
     */
    public void setLinkClickCount(final int linkClickCount)
    {
        this.linkClickCount = linkClickCount;
    }

    /**
     * Gets onClickLinkClickCount.
     * @return onClickLinkClickCount
     */
    public int getOnClickLinkClickCount()
    {
        return onClickLinkClickCount;
    }

    /**
     * Sets onClickLinkClickCount.
     * @param onClickLinkClickCount onClickLinkClickCount
     */
    public void setOnClickLinkClickCount(int onClickLinkClickCount)
    {
        this.onClickLinkClickCount = onClickLinkClickCount;
    }
}


