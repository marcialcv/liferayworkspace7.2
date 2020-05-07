package document.library.audit;

import static document.library.audit.constants.DocumentLibraryAuditConstants.AUDIT_CLASSNAME;
import static document.library.audit.constants.DocumentLibraryAuditConstants.COMPANY_ID_ATTR;
import static document.library.audit.constants.DocumentLibraryAuditConstants.DESCRIPTION_ATTR;
import static document.library.audit.constants.DocumentLibraryAuditConstants.DL_FILE_ENTRY_TYPE_NAME;
import static document.library.audit.constants.DocumentLibraryAuditConstants.DL_FILE_ENTRY_TYPE_NAME_ATTR;
import static document.library.audit.constants.DocumentLibraryAuditConstants.FILE_ENTRY_ID;
import static document.library.audit.constants.DocumentLibraryAuditConstants.GROUP_ID_ATTR;
import static document.library.audit.constants.DocumentLibraryAuditConstants.IN_TRASH_ATTR;
import static document.library.audit.constants.DocumentLibraryAuditConstants.TITLE_ATTR;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author marcialcalvo
 */
@Component(immediate = true, service = ModelListener.class)
public class DLFileEntryModelListener extends BaseModelListener<DLFileEntry> {

	@Override
	public void onBeforeRemove(DLFileEntry dlFileEntry) throws ModelListenerException {
		auditOnUpdateOrRemove(EventTypes.DELETE, dlFileEntry);

		super.onBeforeRemove(dlFileEntry);
	}

	@Override
	public void onBeforeUpdate(DLFileEntry dlFileEntry) throws ModelListenerException {
		auditOnUpdateOrRemove(EventTypes.UPDATE, dlFileEntry);

		super.onBeforeUpdate(dlFileEntry);
	}

	protected void auditOnUpdateOrRemove(String eventType, DLFileEntry dlFileEntry) throws ModelListenerException {

		try {
			if (DL_FILE_ENTRY_TYPE_NAME.equals(dlFileEntry.getDLFileEntryType().getName(LocaleUtil.getDefault()))
					&& dlFileEntry.isInTrash()) {
				AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(eventType, AUDIT_CLASSNAME,
						dlFileEntry.getPrimaryKey(), null);

				JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

				additionalInfoJSONObject.put(TITLE_ATTR, dlFileEntry.getFileVersion().getTitle())
						.put(DESCRIPTION_ATTR, dlFileEntry.getFileVersion().getDescription())
						.put(FILE_ENTRY_ID, dlFileEntry.getFileEntryId()).put(GROUP_ID_ATTR, dlFileEntry.getGroupId())
						.put(COMPANY_ID_ATTR, dlFileEntry.getCompanyId()).put(IN_TRASH_ATTR, dlFileEntry.isInTrash())
						.put(DL_FILE_ENTRY_TYPE_NAME_ATTR,
								dlFileEntry.getDLFileEntryType().getName(LocaleUtil.getDefault()));

				_auditRouter.route(auditMessage);
			}
		} catch (Exception e) {
			throw new ModelListenerException(e);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}