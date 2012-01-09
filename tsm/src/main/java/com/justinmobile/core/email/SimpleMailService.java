package com.justinmobile.core.email;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * 纯文本邮件服务类.
 * 
 * @author peak
 */
public class SimpleMailService {

	private MailSender mailSender;
	
	/**
	 * 发送纯文本的用户修改通知邮件.
	 */
	public void sendMail(String fromMail, String toMail, String title, String content) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(fromMail);
		msg.setTo(toMail);
		msg.setSubject(title);
		msg.setText(content);
		try {
			mailSender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

}
