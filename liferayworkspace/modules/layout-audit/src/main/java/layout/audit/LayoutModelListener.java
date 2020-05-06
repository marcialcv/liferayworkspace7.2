package layout.audit;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.LayoutLocalService;
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
			Layout currentLayout = _layoutLocalService.getLayout(newLayout.getPlid());

			List<Attribute> attributes = getModifiedAttributes(newLayout, currentLayout);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(EventTypes.UPDATE,
						Layout.class.getName(), newLayout.getPlid(), attributes);

				_auditRouter.route(auditMessage);
			}
		} catch (Exception e) {
			throw new ModelListenerException(e);
		}
		super.onBeforeUpdate(newLayout);
	}

	protected List<Attribute> getModifiedAttributes(Layout newLayout, Layout currentLayout) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(newLayout, currentLayout);

		attributesBuilder.add("name");
		attributesBuilder.add("layoutId");
		attributesBuilder.add("groupId");
		attributesBuilder.add("friendlyURL");

		return attributesBuilder.getAttributes();
	}

	protected void auditOnCreateOrRemove(String eventType, Layout layout) throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(eventType, Layout.class.getName(),
					layout.getUserId(), null);

			JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put("layoutName", layout.getName()).put("layoutId", layout.getLayoutId())
					.put("layoutFriendlyURL", layout.getFriendlyURL()).put("layoutGroupId", layout.getGroupId());

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