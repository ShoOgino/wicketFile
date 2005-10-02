/*
 * $Id: BufferedDynamicImageResource.java,v 1.3 2005/03/08 21:12:40
 * jonathanlocke Exp $ $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html.image.resource;

import java.awt.image.BufferedImage;

import wicket.util.time.Time;

/**
 * A DynamicImageResource subclass that holds a BufferedImage generated by code
 * elsewhere. The image is held in a non-transient field, and so if this
 * resource is clustered, the entire image will be serialized and copied. If you
 * can regenerate your image by drawing on a Graphics2D, you should prefer the
 * RenderedDynamicImageResource class instead since its image data is transient
 * and therefore it is very lightweight when clustered.
 * <p>
 * The extension/format of the image resource can be specified with
 * setFormat(String).
 * 
 * @see wicket.markup.html.image.resource.RenderedDynamicImageResource
 * @author Jonathan Locke
 */
public class BufferedDynamicImageResource extends DynamicImageResource
{
	private static final long serialVersionUID = 1L;
	
	/** The byte array holding the contents of the dynamic image */
	private byte[] imageData;

	/**
	 * @param image
	 *            The image to set
	 */
	public void setImage(final BufferedImage image)
	{
		imageData = toImageData(image);
		setLastModifiedTime(Time.now());
	}

	/**
	 * @see wicket.markup.html.image.resource.DynamicImageResource#getImageData()
	 */
	protected byte[] getImageData()
	{
		return imageData;
	}
}
