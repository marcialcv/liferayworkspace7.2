package business.core.status;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcial Calvo
 */
@Component(
    immediate = true,
    property = {
        "osgi.http.whiteboard.context.path=/",
        "osgi.http.whiteboard.servlet.pattern=/business-core-status/check"
    },
    service = Servlet.class
)
public class BusinessCoreStatus extends HttpServlet {
	
	//Event on all portlets_initialized
	@Reference(
		target = ModuleServiceLifecycle.PORTLETS_INITIALIZED, unbind = "-"
	)
	private ModuleServiceLifecycle _moduleServiceLifecycle;
	
	//Inject your OSGi business core dependencies as well
	//@Reference
	//private MyCustomService myCustomService;
	

    @Override
    public void init() throws ServletException {
        _log.info("Business Core Status Endpoint initialized");

        super.init();
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

    	 _log.info("checkEndpoint");

        _writeSampleHTML(response);
    }

    /**
     * Dummy contents
     *
     * @return dummy contents string
     */
    private String _generateSampleHTML() {
        StringBuffer sb = new StringBuffer();

        sb.append("<html>");
        sb.append("<head><title>Status HTML PAGE</title></head>");
        sb.append("<body>");
        sb.append("<h2>All Business Core modules UP & Running!!</h2>");
        sb.append("</body>");
        sb.append("</html>");

        return new String(sb);
    }

    /**
     * Write sample HTML
     *
     * @param resp
     */
    private void _writeSampleHTML(HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        try {
            resp.getWriter().write(_generateSampleHTML());
        }
        catch (Exception e) {
        	_log.error(e);

            resp.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
        }
    }

    private static final long serialVersionUID = 1L;

    
    private static final Log _log = LogFactoryUtil.getLog(BusinessCoreStatus.class);

}