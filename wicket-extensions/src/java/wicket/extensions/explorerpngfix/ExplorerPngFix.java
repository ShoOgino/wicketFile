package wicket.extensions.explorerpngfix;

import wicket.RequestCycle;
import wicket.ResourceReference;
import wicket.behavior.AbstractBehavior;
import wicket.markup.html.IHeaderContributor;
import wicket.markup.html.IHeaderResponse;
import wicket.protocol.http.WebRequestCycle;
import wicket.protocol.http.request.WebClientInfo;

/**
 * A behavior that adds the necessary javascript to the page to make ie < 7.0
 * properly work with png transparency.
 * 
 * @author ivaynberg
 * 
 */
public class ExplorerPngFix extends AbstractBehavior implements IHeaderContributor
{

	private static final long serialVersionUID = 1L;

	private static final ResourceReference ref = new ResourceReference(ExplorerPngFix.class,
			"explorerPngFix.js");		

	/**
	 * @see wicket.markup.html.IHeaderContributor#renderHead(wicket.markup.html.IHeaderResponse)
	 */
	public void renderHead(IHeaderResponse response)
	{		
		if (response.wasRendered(ref) == false)
		{
			WebClientInfo info = ((WebRequestCycle)RequestCycle.get()).getClientInfo();

			if (info.isBrowserInternetExplorer() && info.getBrowserMajorVersion() < 7)
			{
				response.getResponse().write("<!--[if lt IE 7.]> <script defer type=\"text/javascript\" src=\"");
				response.getResponse().write(RequestCycle.get().urlFor(ref));
				response.getResponse().write("\"></script> <![endif]-->");
				
				response.markRendered(ref);
			}
		}
	}
}
