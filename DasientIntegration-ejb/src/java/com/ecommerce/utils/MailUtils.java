package com.ecommerce.utils;

import org.apache.log4j.Logger;
import java.util.Properties;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

/**
 * Utility class providing mail utilties for the Dasient Integration project
 * @author djdaugherty
 */
public class MailUtils {

	static Logger logger = Logger.getLogger(MailUtils.class);

	private MailUtils() {
	}

	public static void sendEmail(String subject, String message) throws Exception {

		Properties props = new Properties();
		InitialContext ictx = new InitialContext(props);
		javax.mail.Session mailSession = (javax.mail.Session) ictx.lookup("java:/Mail");
		//		Session mailSessoin = Session.getDefaultInstance(props);
		String username = (String) props.get("mail.smtp.user");
		String password = (String) props.get("mail.smtp.password");

		MimeMessage msg = new MimeMessage(mailSession);
		msg.setSubject(subject);
		String recipientList = System.getProperty("status_report_recipient_list");
		msg.setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(recipientList, false));
		msg.setText(message);
		msg.saveChanges();

		Transport transport = mailSession.getTransport("smtp");
		try {
			transport.connect(username, password);
			transport.sendMessage(msg, msg.getAllRecipients());
			logger.info("email message sent successfully!");
		} finally {
			transport.close();
		}
	}
}
