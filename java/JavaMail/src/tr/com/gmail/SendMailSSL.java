package tr.com.gmail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailSSL {
	public static final int SHOW_MESSAGES = 1;
	public static final int CLEAR_MESSAGES = 2;
	public static final int SHOW_AND_CLEAR = SHOW_MESSAGES + CLEAR_MESSAGES;
	public static void main(String[] args) throws IOException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");// localhost local TCP/IP monitorden izleyerek TLS /SSL (�ifreli) fark�n� g�rmek i�i
		props.put("mail.smtp.socketFactory.port", "465");// 80
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		props.put("mail.store.protocol", "imap");
		props.put("mail.imap.host", "imap.gmail.com");
		props.put("mail.imap.socketFactory.port", "993");// 80
		props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.imap.port", "993");
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("ali.gelenler@gmail.com", "pfnsdomathebjsjh");
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("ali.gelenler@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("ali.gelenler@gmail.com,uygulamacepte@gmail.com,elifyavuz@gmail.com"));
			message.setSubject("Bu arada �nceki mail java mail k�t�phanesi ile g�nderilmi�tir.");
			message.setText("Sevgili okuyucu," + "\n\n aligelenler.blogspot.com ile �ok g�zel �eyler ��renebilirsiniz. hehe!");

			Transport.send(message);

			System.out.println("Done2");

			//checkInbox(session, SHOW_MESSAGES, "ali.gelenler@gmail.com");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void checkInbox(Session session, int mode, String from) throws MessagingException, IOException {
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
			if (false) { //if(clear) be careful
				msg.setFlag(Flags.Flag.DELETED, true);
			}
		}
		inbox.close(true);
		store.close();
		System.out.println();
	}
}