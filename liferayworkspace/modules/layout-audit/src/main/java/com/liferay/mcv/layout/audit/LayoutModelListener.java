package com.liferay.mcv.layout.audit;

import static com.liferay.mcv.layout.audit.constants.LayoutAuditConstants.FRIENDLY_URL_ATTR;
import static com.liferay.mcv.layout.audit.constants.LayoutAuditConstants.GROUP_ID_ATTR;
import static com.liferay.mcv.layout.audit.constants.LayoutAuditConstants.GROUP_NAME_ATTR;
import static com.liferay.mcv.layout.audit.constants.LayoutAuditConstants.LAYOUT_ID_ATTR;
import static com.liferay.mcv.layout.audit.constants.LayoutAuditConstants.NAME_ATTR;

import com.liferay.mcv.email.service.util.EmailServiceUtil;
import com.liferay.mcv.layout.audit.configuration.LayoutAuditConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author marcialcalvo
 */
@Component(immediate = true, service = ModelListener.class, configurationPid = "com.liferay.mcv.layout.audit.configuration.LayoutAuditConfiguration")
public class LayoutModelListener extends BaseModelListener<Layout> {

	@Override
	public void onBeforeCreate(Layout layout) throws ModelListenerException {

		auditOnEvent(EventTypes.ADD, layout);

		super.onBeforeCreate(layout);
	}

	@Override
	public void onBeforeRemove(Layout layout) throws ModelListenerException {

		auditOnEvent(EventTypes.DELETE, layout);

		super.onBeforeRemove(layout);
	}

	@Override
	public void onBeforeUpdate(Layout layout) throws ModelListenerException {

		auditOnEvent(EventTypes.UPDATE, layout);

		super.onBeforeUpdate(layout);
	}

	protected List<Attribute> getModifiedAttributes(Layout newLayout, Layout currentLayout) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(newLayout, currentLayout);

		attributesBuilder.add(NAME_ATTR);
		attributesBuilder.add(FRIENDLY_URL_ATTR);

		return attributesBuilder.getAttributes();
	}

	protected void auditOnEvent(String eventType, Layout layout) throws ModelListenerException {
		if (_configuration.enabled()) {
			try {
				List<Attribute> attributes = new ArrayList<>();

				if (EventTypes.UPDATE.equals(eventType)) {
					Layout currentLayout = _layoutLocalService.getLayout(layout.getPlid());
					attributes = getModifiedAttributes(layout, currentLayout);
				}

				if ((EventTypes.UPDATE.equals(eventType) && !attributes.isEmpty())
						|| !EventTypes.UPDATE.equals(eventType)) {
					AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(eventType, Layout.class.getName(),
							layout.getLayoutId(), attributes);

					JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

					additionalInfoJSONObject.put(NAME_ATTR, layout.getName(LocaleUtil.getDefault()))
							.put(LAYOUT_ID_ATTR, layout.getPlid()).put(FRIENDLY_URL_ATTR, layout.getFriendlyURL())
							.put(GROUP_ID_ATTR, layout.getGroupId())
							.put(GROUP_NAME_ATTR, layout.getGroup().getName(LocaleUtil.getDefault()));

					_auditRouter.route(auditMessage);
				}

				if (EventTypes.DELETE.equals(eventType) && layout.isFirstParent()) {
					emailServiceUtil.sendEmail(_configuration.senderEmail(), _configuration.receiverEmail(),
							_configuration.subject(), _configuration.body(), Boolean.TRUE);
				}
			} catch (Exception e) {
				throw new ModelListenerException(e);
			}

		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(LayoutAuditConfiguration.class, properties);
	}

	private volatile LayoutAuditConfiguration _configuration;

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private EmailServiceUtil emailServiceUtil;

}