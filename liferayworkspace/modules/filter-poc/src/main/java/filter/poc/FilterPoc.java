package filter.poc;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author marcialcalvo
 */
@Component(
	    property = {
	        // Leave as is, means target Liferay Portal Context path
	        "servlet-context-name=",
	        // Name of the Servlet Filter
	        "servlet-filter-name=CustomFilter",
	        // Pattern the filter will match
	        "url-pattern=/*",
	        // In liferay-web.xml which filter should this go before
	        // See https://github.com/liferay/liferay-portal/blob/7.1.x/portal-web/docroot/WEB-INF/liferay-web.xml
	        "before-filter=Absolute Redirects Filter",
	        // In liferay-web.xml which filter should this go after
	        //"after-filter="
	        // See https://github.com/liferay/liferay-portal/blob/7.1.x/portal-web/docroot/WEB-INF/liferay-web.xml
	        // Dispatchers to match
	        "dispatcher=REQUEST",
	        "dispatcher=FORWARD"
	    }
	)
public class FilterPoc implements Filter {

	@Override
	public void destroy() {

		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		String uri = HttpUtil.normalizePath(
			(String)servletRequest.getAttribute(WebKeys.INVOKER_FILTER_URI));

		_log.info("URI --> " + uri);

		uri = uri.replace(
			_COMPANY_DEFAULT_HOME_URL + CharPool.SLASH, StringPool.BLANK);

		String[] pathArray = StringUtil.split(uri, CharPool.SLASH);

		String friendlyUrl =
			CharPool.SLASH +
				((pathArray.length > 0) ? String.valueOf(pathArray[0]) :
				StringPool.BLANK);

		_log.info("friendlyUrl --> " + friendlyUrl);

		long companyId = PortalUtil.getDefaultCompanyId();

		Group targetGroup = _groupLocalService.fetchFriendlyURLGroup(
			companyId, friendlyUrl);

		if (targetGroup != null) {
			String path =
				_LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING + CharPool.SLASH +
					uri;

			_log.info("Forward --> " + path);

			servletRequest.getRequestDispatcher(
				path
			).forward(
				servletRequest, servletResponse
			);
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void init(FilterConfig filterConfig) throws ServletException {

		// TODO Auto-generated method stub

	}

	private static final String _COMPANY_DEFAULT_HOME_URL =
		PropsValues.COMPANY_DEFAULT_HOME_URL;

	private static final String _LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING =
		PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING;

	private static final Log _log = LogFactoryUtil.getLog(FilterPoc.class);

	@Reference
	private GroupLocalService _groupLocalService;

}