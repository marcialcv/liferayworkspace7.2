package com.liferay.mcv.document.library.audit.constants;

import com.liferay.document.library.kernel.model.DLFileEntry;

/**
 * @author marcialcalvo
 *
 */
public class DocumentLibraryAuditConstants {
	
		public static final String TITLE_ATTR = "title";
		public static final String DESCRIPTION_ATTR = "description";
		public static final String FILE_ENTRY_ID = "fileEntryId";
		public static final String GROUP_ID_ATTR = "groupId";
		public static final String COMPANY_ID_ATTR = "companyId";
		public static final String IN_TRASH_ATTR = "inTrash";
		public static final String DL_FILE_ENTRY_TYPE_NAME_ATTR = "dlFileEntryTypeName";
		public static final String AUDIT_CLASSNAME = DLFileEntry.class.getName();
		public static final String DL_FILE_ENTRY_TYPE_NAME = "myCustomDLEntryType";

}
