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
package wicket.markup.html.link;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wicket.RequestCycle;
import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.html.HtmlComponent;

/**
 * An image map holds links with different hot-area shapes.
 * @author Jonathan Locke
 */
public final class ImageMap extends HtmlComponent
{ // TODO finalize javadoc
    /** Serial Version ID. */
	private static final long serialVersionUID = 209001445308790198L;

	/** list of shape links. */
	private final List shapeLinks = new ArrayList();

    /**
     * Constructor.
     * @param name component name
     */
    public ImageMap(final String name)
    {
        super(name);
    }

    /**
     * @see wicket.Component#handleRender(RequestCycle)
     */
    protected void handleRender(final RequestCycle cycle)
    {
        // Get markup stream
        final MarkupStream markupStream = findMarkupStream();

        // Get mutable copy of next tag
        final ComponentTag tag = markupStream.getTag().mutable();

        // Must be an img tag
        checkTag(tag, "img");

        // Set map name to path
        tag.put("usemap", "#" + getPath());

        // Write out the tag
        renderTag(cycle, markupStream, tag);

        // Write out the image map
        final StringBuffer imageMap = new StringBuffer();

        imageMap.append("\n<map name=\"" + getPath() + "\"> ");

        for (Iterator iterator = shapeLinks.iterator(); iterator.hasNext();)
        {
            final ShapeLink shapeLink = (ShapeLink) iterator.next();

            imageMap.append('\n');
            imageMap.append(shapeLink.toString(cycle));
        }

        imageMap.append("\n</map>");
        cycle.getResponse().write(imageMap.toString());
    }

    /**
     * Adds a polygon link.
     * @param coordinates the coordinates for the polygon
     * @param link the link
     * @return This
     */
    public ImageMap addPolygonLink(final int[] coordinates, final Link link)
    {
        shapeLinks.add(new PolygonLink(coordinates, link));

        return this;
    }

    /**
     * Adds a rectangular link.
     * @param x1 top left x
     * @param y1 top left y
     * @param x2 bottom right x
     * @param y2 bottom right y
     * @param link
     * @return This
     */
    public ImageMap addRectangleLink(final int x1, final int y1, final int x2, final int y2,
            final Link link)
    {
        shapeLinks.add(new RectangleLink(x1, y1, x2, y2, link));

        return this;
    }

    /**
     * Adds a circle link.
     * @param x1 top left x
     * @param y1 top left y
     * @param radius the radius
     * @param link the link
     * @return This
     */
    public ImageMap addCircleLink(final int x1, final int y1, final int radius, final Link link)
    {
        shapeLinks.add(new CircleLink(x1, y1, radius, link));

        return this;
    }

    /**
     * Base class for shaped links.
     */
    private static abstract class ShapeLink
    {
        /** the link. */
        private final Link link;

        /**
         * Constructor.
         * @param link the link
         */
        public ShapeLink(final Link link)
        {
            this.link = link;
        }

        /**
         * Gets the shape type.
         * @return the shape type
         */
        abstract String getType();

        /**
         * Gets the coordinates of the shape.
         * @return the coordinates of the shape
         */
        abstract String getCoordinates();

        /**
         * The shape as a string using the given request cycle; will be used
         * for rendering.
         * @param cycle the current request cycle
         * @return The shape as a string
         */
        final String toString(final RequestCycle cycle)
        {
            //Add any popup script
            final String popupJavaScript;

            if (link.getPopupSpecification() != null)
            {
                popupJavaScript = link.getPopupSpecification().getPopupJavaScript();
            }
            else
            {
                popupJavaScript = null;
            }

            return "<area shape=\""
                    + getType() + "\"" + " coords=\"" + getCoordinates() + "\"" + " href=\""
                    + link.getURL(cycle) + "\""
                    + ((popupJavaScript == null) ? "" : (" onClick = \"" + popupJavaScript + "\""))
                    + ">";
        }
    }

    /**
     * A shape that has a free (polygon) form.
     */
    private static final class PolygonLink extends ShapeLink
    {
        /** it's coordinates. */
        private final int[] coordinates;

        /**
         * Construct.
         * @param coordinates the polygon coordinates
         * @param link the link
         */
        public PolygonLink(final int[] coordinates, final Link link)
        {
            super(link);
            this.coordinates = coordinates;
        }

        /**
         * @see wicket.markup.html.link.ImageMap.ShapeLink#getType()
         */
        String getType()
        {
            return "polygon";
        }

        /**
         * @see wicket.markup.html.link.ImageMap.ShapeLink#getCoordinates()
         */
        String getCoordinates()
        {
            final StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < coordinates.length; i++)
            {
                buffer.append(coordinates[i]);

                if (i < (coordinates.length - 1))
                {
                    buffer.append(',');
                }
            }
            return buffer.toString();
        }
    }

    /**
     * A shape that has a rectangular form.
     */
    private static final class RectangleLink extends ShapeLink
    {
        /** left upper x. */
        private final int x1;

        /** left upper y. */
        private final int y1;

        /** right bottom x. */
        private final int x2;

        /** right bottom y. */
        private final int y2;

        /**
         * Construct.
         * @param x1 left upper x
         * @param y1 left upper y
         * @param x2 right bottom x
         * @param y2 right bottom y
         * @param link the link
         */
        public RectangleLink(final int x1, final int y1, final int x2, final int y2, final Link link)
        {
            super(link);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        /**
         * @see wicket.markup.html.link.ImageMap.ShapeLink#getType()
         */
        String getType()
        {
            return "rectangle";
        }

        /**
         * @see wicket.markup.html.link.ImageMap.ShapeLink#getCoordinates()
         */
        String getCoordinates()
        {
            return x1 + "," + y1 + "," + x2 + "," + y2;
        }
    }

    /**
     * A shape that has a circle form.
     */
    private static final class CircleLink extends ShapeLink
    {
        /** left upper x. */
        private final int x1;

        /** left upper y. */
        private final int y1;

        /** the circles' radius. */
        private final int radius;

        /**
         * Construct.
         * @param x1 left upper x
         * @param y1 left upper y
         * @param radius the circles' radius
         * @param link the link
         */
        public CircleLink(final int x1, final int y1, final int radius, final Link link)
        {
            super(link);
            this.x1 = x1;
            this.y1 = y1;
            this.radius = radius;
        }

        /**
         * @see wicket.markup.html.link.ImageMap.ShapeLink#getType()
         */
        String getType()
        {
            return "circle";
        }

        /**
         * @see wicket.markup.html.link.ImageMap.ShapeLink#getCoordinates()
         */
        String getCoordinates()
        {
            return x1 + "," + y1 + "," + radius;
        }
    }
}

///////////////////////////////// End of File /////////////////////////////////
