package com.liferay.portal.security.sso.facebook.connect.servlet.taglib;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

@Component(
	    immediate = true,
	    property = {"key=portal.settings.authentication", "label=Facebook"},
	    service = DynamicInclude.class
	)
public class FacebookConnectConfigurationDynamicInclude extends BaseDynamicInclude {

    @Override
    public void include(
            HttpServletRequest request, HttpServletResponse response,
            String key)
        throws IOException {

		RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(_JSP_PATH);

		try {
			
			requestDispatcher.include(request, response);
			
		} catch (ServletException se) {
			
			if (_log.isErrorEnabled()) {
				_log.error("Unable to include JSP", se);
			}
			throw new IOException("Unable to include " + _JSP_PATH, se);
		}
    }

    @Override
    public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {    	
    	dynamicIncludeRegistry.register("portal.settings.authentication");
    }

	@Reference(
			target = "(osgi.web.symbolicname=com.liferay.portal.security.sso.facebook.connect)"
		)
	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private static final String _JSP_PATH =
		"/META-INF/resources/html/portlet/portal_settings/authentication/facebook.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
			FacebookConnectConfigurationDynamicInclude.class);

	private ServletContext _servletContext;
}
