package active.mq.initializer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.broker.BrokerService;
import org.osgi.service.component.annotations.Component;
import static active.mq.initializer.constants.ActiveMQConstants.HOSTNAME;
import static active.mq.initializer.constants.ActiveMQConstants.PORT;

@Component(immediate = true, property = { 
		"osgi.http.whiteboard.context.path=/",
		"osgi.http.whiteboard.servlet.pattern=/activemq/*" 
		},
service = Servlet.class)
public class ActiveMqInitializer extends HttpServlet {
	
	private static final long serialVersionUID = -1039786001509280342L;
	
	private BrokerService broker;

	@Override
	public void init() throws ServletException {
		// configure the broker
		try {
			broker = new BrokerService();
			broker.addConnector("tcp://"+HOSTNAME+":"+PORT);
			broker.start();
			_log.info("ActiveMQ loaded succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("Unable to load ActiveMQ!");
		}
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	@Override
	public void destroy() {
		try {
			System.out.println("ActiveMQ exiting");
			broker.stop();
			System.out.println("ActiveMQ exit succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to exit ActiveMQ!");
		}
	}
	
	private static final Log _log = LogFactoryUtil.getLog(ActiveMqInitializer.class);

}