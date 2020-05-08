package com.liferay.mcv.email.service.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.osgi.service.component.annotations.Component;

/**
 * @author marcialcalvo
 */
@Component(immediate = true, service = EmailServiceUtil.class)
public class EmailServiceUtil {

	public void sendEmail(String senderEmail, String receiverEmail, String subject, String body, boolean htmlFormat) {

		MailMessage mailMessage = buildMailMessage(senderEmail, receiverEmail, subject, body, htmlFormat);

		if (_log.isDebugEnabled()) {
			_log.debug("Sending email from " + senderEmail + " to " + receiverEmail + " \r\n Subject= " + subject
					+ " \r\n Body= " + body + " \r\n htmlFormat=" + htmlFormat);
		}
 
		MailServiceUtil.sendEmail(mailMessage);
	}

	public MailMessage buildMailMessage(String senderEmail, String receiverEmail, String subject, String body,
			boolean htmlFormat) {
		InternetAddress from = null;
		InternetAddress to = null;
		MailMessage mailMessage = null;
		try {
			from = new InternetAddress(senderEmail);
			to = new InternetAddress(receiverEmail);
			mailMessage = new MailMessage(from, to, subject, body, htmlFormat);
		} catch (AddressException ae) {
			_log.error(ae);
		}
		return mailMessage;

	}

	private static final Log _log = LogFactoryUtil.getLog(EmailServiceUtil.class);

}