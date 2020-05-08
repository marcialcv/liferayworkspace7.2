package com.liferay.mcv.journal.article.audit;

import static com.liferay.mcv.journal.article.audit.constants.JournalArticleAuditConstants.AUDIT_CLASSNAME;
import static com.liferay.mcv.journal.article.audit.constants.JournalArticleAuditConstants.COMPANY_ID_ATTR;
import static com.liferay.mcv.journal.article.audit.constants.JournalArticleAuditConstants.GROUP_ID_ATTR;
import static com.liferay.mcv.journal.article.audit.constants.JournalArticleAuditConstants.ID_ATTR;
import static com.liferay.mcv.journal.article.audit.constants.JournalArticleAuditConstants.IN_TRASH_ATTR;
import static com.liferay.mcv.journal.article.audit.constants.JournalArticleAuditConstants.URL_TITLE_ATTR;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
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
public class JournalArticleModelListener extends BaseModelListener<JournalArticle> {

	@Override
	public void onBeforeRemove(JournalArticle journalArticle) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.DELETE, journalArticle);

		super.onBeforeRemove(journalArticle);
	}

	@Override
	public void onBeforeUpdate(JournalArticle newJournalArticle) throws ModelListenerException {
		try {
			JournalArticle currentJournalArticle = _journalArticleLocalService
					.getJournalArticle(newJournalArticle.getId());

			List<Attribute> attributes = getModifiedAttributes(newJournalArticle, currentJournalArticle);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(EventTypes.UPDATE, AUDIT_CLASSNAME,
						newJournalArticle.getId(), attributes);

				_auditRouter.route(auditMessage);
			}
		} catch (Exception e) {
			throw new ModelListenerException(e);
		}
		super.onBeforeUpdate(newJournalArticle);
	}

	protected List<Attribute> getModifiedAttributes(JournalArticle newJournalArticle,
			JournalArticle currentJournalArticle) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(newJournalArticle, currentJournalArticle);

		attributesBuilder.add(IN_TRASH_ATTR);

		return attributesBuilder.getAttributes();
	}

	protected void auditOnCreateOrRemove(String eventType, JournalArticle journalArticle)
			throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(eventType, AUDIT_CLASSNAME,
					journalArticle.getId(), null);

			JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(URL_TITLE_ATTR, journalArticle.getUrlTitle())
					.put(ID_ATTR, journalArticle.getId()).put(GROUP_ID_ATTR, journalArticle.getGroupId())
					.put(COMPANY_ID_ATTR, journalArticle.getCompanyId()).put(IN_TRASH_ATTR, journalArticle.isInTrash());

			_auditRouter.route(auditMessage);
		} catch (Exception e) {
			throw new ModelListenerException(e);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

}