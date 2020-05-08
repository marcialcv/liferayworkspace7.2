package com.liferay.mcv.layout.audit.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
	    category = "audit", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
	)
@Meta.OCD(id = "com.liferay.mcv.layout.audit.configuration.LayoutAuditConfiguration", localization = "content/Language", name = "layout-audit-configuration")
public interface LayoutAuditConfiguration {

	@Meta.AD(deflt = "test@liferay.com", required = false)
	public String senderEmail();

	@Meta.AD(deflt = "destination@liferay.com", required = false)
	public String receiverEmail();

	@Meta.AD(deflt = "Alert: Someone has deleted a Home Page!", required = false)
	public String subject();

	@Meta.AD(deflt = "Someone deleted a Home Page. Please access to <a href='http://localhost:8080/group/control_panel/manage?p_p_id=com_liferay_portal_security_audit_web_portlet_AuditPortlet'>Audit Liferay page</a> to get more details", required = false)
	public String body();
	
	@Meta.AD(deflt = "true", required = false)
	public boolean enabled();

}