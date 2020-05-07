package layout.audit;

import static layout.audit.constants.LayoutAuditConstants.FRIENDLY_URL_ATTR;
import static layout.audit.constants.LayoutAuditConstants.GROUP_ID_ATTR;
import static layout.audit.constants.LayoutAuditConstants.GROUP_NAME_ATTR;
import static layout.audit.constants.LayoutAuditConstants.LAYOUT_ID_ATTR;
import static layout.audit.constants.LayoutAuditConstants.NAME_ATTR;

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

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author marcialcalvo
 */
@Component(immediate = true, service = ModelListener.class)
public class LayoutModelListener extends BaseModelListener<Layout> {

	@Override
	public void onBeforeCreate(Layout layout) throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.ADD, layout);

		super.onBeforeCreate(layout);
	}

	@Override
	public void onBeforeRemove(Layout layout) throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.DELETE, layout);

		super.onBeforeRemove(layout);
	}

	@Override
	public void onBeforeUpdate(Layout newLayout) throws ModelListenerException {
		try {
			Layout currentLayout = _layoutLocalService.getLayout(newLayout.getLayoutId());

			List<Attribute> attributes = getModifiedAttributes(newLayout, currentLayout);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(EventTypes.UPDATE,
						Layout.class.getName(), newLayout.getLayoutId(), attributes);

				_auditRouter.route(auditMessage);
			}
		} catch (Exception e) {
			throw new ModelListenerException(e);
		}
		super.onBeforeUpdate(newLayout);
	}

	protected List<Attribute> getModifiedAttributes(Layout newLayout, Layout currentLayout) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(newLayout, currentLayout);

		attributesBuilder.add(NAME_ATTR);
		attributesBuilder.add(FRIENDLY_URL_ATTR);

		return attributesBuilder.getAttributes();
	}

	protected void auditOnCreateOrRemove(String eventType, Layout layout) throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(eventType, Layout.class.getName(),
					layout.getLayoutId(), null);

			JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(NAME_ATTR, layout.getName()).put(LAYOUT_ID_ATTR, layout.getLayoutId())
					.put(FRIENDLY_URL_ATTR, layout.getFriendlyURL()).put(GROUP_ID_ATTR, layout.getGroupId())
					.put(GROUP_NAME_ATTR, layout.getGroup().getName(LocaleUtil.getDefault()));

			_auditRouter.route(auditMessage);
		} catch (Exception e) {
			throw new ModelListenerException(e);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private LayoutLocalService _layoutLocalService;

}