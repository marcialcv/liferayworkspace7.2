package com.liferay.mcv.maintenance.audit.storage;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author marcialcalvo
 *
 */
@Component(immediate = true, service = MaintenanceAuditEventsUtil.class)
public class MaintenanceAuditEventsUtil {
	
	public void removePastAuditEvents() {
		Calendar calendarGT = Calendar.getInstance();
		calendarGT.setTime(new Date());
		calendarGT.add(Calendar.MONTH, -6);
		Date createDateGT = calendarGT.getTime();

		Calendar calendarLT = Calendar.getInstance();
		calendarLT.setTime(new Date());
		calendarLT.add(Calendar.MONTH, -3);
		Date createDateLT = calendarLT.getTime();

		List<AuditEvent> auditEventList = auditEventLocalService.getAuditEvents(PortalUtil.getDefaultCompanyId(), 0,
				null, createDateGT, createDateLT, null, null, null, null, null, null, 0, null, true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		auditEventList.forEach(auditEvent -> auditEventLocalService.deleteAuditEvent(auditEvent));
	}
	
	@Reference
	protected AuditEventLocalService auditEventLocalService;

}
