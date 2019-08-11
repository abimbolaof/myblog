package java.mail.com.mail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class MailClient extends Authenticator {
	public static final int SHOW_MESSAGES = 1;
	public static final int CLEAR_MESSAGES = 2;
	public static final int SHOW_AND_CLEAR = SHOW_MESSAGES + CLEAR_MESSAGES;
	private static final String PASS = "admin";

	protected String from;
	protected Session session;
	protected PasswordAuthentication authentication;

	public MailClient(String user, String host) {
		this(user, host, false);
	}

	public MailClient(String user, String host, boolean debug) {
		from = user + '@' + host;
		authentication = new PasswordAuthentication(user, PASS);
		Properties props = new Properties();
		props.put("mail.user", user);
		props.put("mail.host", host);
		props.put("mail.pop3.port", "1110");
		props.put("mail.debug", debug ? "true" : "false");
		props.put("mail.store.protocol", "pop3");
		props.put("mail.transport.protocol", "smtp");
		
		props.put("mail.smtp.host", host);//localhost local TCP/IP monitorden izleyerek TLS /SSL (�ifreli) fark�n� g�rmek i�i
//		props.put("mail.smtp.socketFactory.port", "465");//80
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "1125");//default portlar config.xml de de�i�mi� durumda
		
		session = Session.getInstance(props, this);
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return authentication;
	}

	public void sendMessage(String to, String subject, String content) throws MessagingException {
		System.out.println("SENDING message from " + from + " to " + to);
		System.out.println();
		MimeMessage msg = new MimeMessage(session);
		msg.addRecipients(Message.RecipientType.TO, to);
		msg.setSubject(subject);
		msg.setText(content);
		Transport.send(msg);
	}

	public void checkInbox(int mode) throws MessagingException, IOException {
		if (mode == 0)
			return;
		boolean show = (mode & SHOW_MESSAGES) > 0;
		boolean clear = (mode & CLEAR_MESSAGES) > 0;
		String action = (show ? "Show" : "") + (show && clear ? " and " : "") + (clear ? "Clear" : "");
		System.out.println(action + " INBOX for " + from);
		Store store = session.getStore();
		store.connect();
		Folder root = store.getDefaultFolder();
		Folder inbox = root.getFolder("inbox");
		inbox.open(Folder.READ_WRITE);
		Message[] msgs = inbox.getMessages();
		if (msgs.length == 0 && show) {
			System.out.println("No messages in inbox");
		}
		for (int i = 0; i < msgs.length; i++) {
			MimeMessage msg = (MimeMessage) msgs[i];
			if (show) {
				System.out.println("    From: " + msg.getFrom()[0]);
				System.out.println(" Subject: " + msg.getSubject());
				System.out.println(" Content: " + msg.getContent());
			}
			if (clear) {
				msg.setFlag(Flags.Flag.DELETED, true);
			}
		}
		inbox.close(true);
		store.close();
		System.out.println();
	}
}