package active.mq.consumer.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import java.io.IOException;

import javax.jms.JMSException;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import active.mq.consumer.constants.ActiveMqConsumerPortletKeys;
import active.mq.consumer.util.UserActionConsumer;

/**
 * @author marcialcalvo
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=ActiveMqConsumer",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + ActiveMqConsumerPortletKeys.ACTIVEMQCONSUMER,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class ActiveMqConsumerPortlet extends MVCPortlet {

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		
		_log.info("Checking Messages in Queue");
		
		try {
			UserActionConsumer.processMessages();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.doView(renderRequest, renderResponse);
	}
	
	
	private static final Log _log = LogFactoryUtil.getLog(ActiveMqConsumerPortlet.class);
	
}