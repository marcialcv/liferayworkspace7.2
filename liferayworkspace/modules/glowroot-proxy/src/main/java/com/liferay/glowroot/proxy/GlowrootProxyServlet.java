package com.liferay.glowroot.proxy;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.mitre.dsmiley.httpproxy.ProxyServlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(
	enabled = true,
	property = {
		"osgi.http.whiteboard.context.path=/",
		"osgi.http.whiteboard.servlet.pattern=/glowroot/*",
		"servlet.init.targetUri=http://localhost:4000/o/glowroot"
	},
	service = Servlet.class
)
public class GlowrootProxyServlet extends ProxyServlet implements Serializable {

	@Override
	protected void service(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			PermissionChecker permissionChecker = _getPermissionChecker(
				httpServletRequest);

			if (permissionChecker == null || !permissionChecker.isOmniadmin()) {
				return;
			}

			super.service(
				new GzipEncodingRequestWrapper(httpServletRequest),
				httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private PermissionChecker _getPermissionChecker(
			HttpServletRequest httpServletRequest)
		throws Exception {

		User user = _portal.getUser(httpServletRequest);

		if (user == null) {
			return null;
		}

		return _permissionCheckerFactory.create(user);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GlowrootProxyServlet.class);

	private static final long serialVersionUID = 1L;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	private static class GzipEncodingRequestWrapper
		extends HttpServletRequestWrapper {

		@Override
		public String getHeader(String name) {
			if (StringUtil.equalsIgnoreCase("Accept-Encoding", name)) {
				return null;
			}

			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			if (_headerNameSet == null) {
				_headerNameSet = new HashSet<>();

				Enumeration<String> enumeration = super.getHeaderNames();

				while (enumeration.hasMoreElements()) {
					String headerName = enumeration.nextElement();

					if (!StringUtil.equalsIgnoreCase(
							"Accept-Encoding", headerName)) {

						_headerNameSet.add(headerName);
					}
				}
			}

			return Collections.enumeration(_headerNameSet);
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			if (StringUtil.equalsIgnoreCase("Accept-Encoding", name)) {
				return Collections.emptyEnumeration();
			}

			return super.getHeaders(name);
		}

		private GzipEncodingRequestWrapper(
			HttpServletRequest httpServletRequest) {

			super(httpServletRequest);
		}

		private Set<String> _headerNameSet;

	}

}