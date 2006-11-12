/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup.html.image.resource;

import java.awt.image.BufferedImage;
import java.util.Locale;

/**
 * A DynamicImageResource subclass that holds a BufferedImage generated by code
 * elsewhere. The image is held in a non-transient field, and so if this
 * resource is clustered, the entire image will be serialized and copied. If you
 * can regenerate your image by drawing on a Graphics2D, you should prefer the
 * RenderedDynamicImageResource class instead since its image data is transient
 * and therefore it is very lightweight when clustered.
 * <p>
 * The format of the image (and therefore the resource's extension) can be
 * specified with setFormat(String). The default format is "PNG" because JPEG is
 * lossy and makes generated images look bad and GIF has patent issues.
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
	 * Construct.
	 */
	public BufferedDynamicImageResource()
	{
	}

	/**
	 * Construct.
	 * 
	 * @param locale
	 */
	public BufferedDynamicImageResource(Locale locale)
	{
		super(locale);
	}

	/**
	 * Construct.
	 * 
	 * @param format
	 * @param locale
	 */
	public BufferedDynamicImageResource(String format, Locale locale)
	{
		super(format, locale);
	}

	/**
	 * Construct.
	 * 
	 * @param format
	 */
	public BufferedDynamicImageResource(String format)
	{
		super(format);
	}

	/**
	 * @param image
	 *            The image to set
	 */
	public synchronized void setImage(final BufferedImage image)
	{
		imageData = toImageData(image);
	}

	@Override
	protected byte[] getImageData()
	{
		return imageData;
	}
}
