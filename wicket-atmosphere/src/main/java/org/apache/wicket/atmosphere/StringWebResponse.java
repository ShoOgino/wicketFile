package org.apache.wicket.atmosphere;

import javax.servlet.http.Cookie;

import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.time.Time;

public class StringWebResponse extends WebResponse
{
	protected final AppendingStringBuffer out;

	public StringWebResponse()
	{
		out = new AppendingStringBuffer(128);
	}

	@Override
	public void addCookie(Cookie cookie)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearCookie(Cookie cookie)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeader(String name, String value)
	{
	}

	@Override
	public void addHeader(String name, String value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDateHeader(String name, Time date)
	{
	}

	@Override
	public void setContentLength(long length)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentType(String mimeType)
	{
	}

	@Override
	public void setStatus(int sc)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendError(int sc, String msg)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeRedirectURL(CharSequence url)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendRedirect(String url)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(byte[] array)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(byte[] array, int offset, int length)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeURL(CharSequence url)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getContainerResponse()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRedirect()
	{
		return false;
	}

	@Override
	public void reset()
	{
		out.clear();
	}

	@Override
	public void flush()
	{
	}

	@Override
	public void write(CharSequence sequence)
	{
		out.append(sequence);
	}

	/**
	 * @return The internal buffer directly as a {@link CharSequence}
	 */
	public CharSequence getBuffer()
	{
		return out;
	}

	@Override
	public String toString()
	{
		return out.toString();
	}
}
