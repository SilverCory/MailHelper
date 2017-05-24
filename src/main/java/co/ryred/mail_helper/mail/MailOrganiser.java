package co.ryred.mail_helper.mail;

import co.ryred.mail_helper.MailHelperConfig;
import co.ryred.mail_helper.enigma.Enigma;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cory Redmond on 13/04/2017.
 * ace@ac3-servers.eu
 */
public class MailOrganiser extends Thread {

    private final MailHelperConfig config;
    private final SimpleDateFormat formatter;

    private Store store = null;

    public MailOrganiser(MailHelperConfig config) {
        super( "Mail Organiser Thread" );
        this.config = config;
        formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.S");
    }

    @Override
    public final void run() {

        try {
            procMail();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                System.out.println( "30 Second delay!" );
                Thread.sleep(TimeUnit.SECONDS.toMillis(30));
                procMail();
            } catch (InterruptedException e) {
                break;
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

    }

    public Store getMail() {

        if( store != null && store.isConnected() ) return store;

        try {
            Session session = Session.getDefaultInstance(config.getMailProperties(), null);
            Store store = session.getStore("imap");
            store.connect( config.getUsername(), config.getPassword() );

            return store;
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void procMail() throws MessagingException {

        Store store = getMail();

        Folder inbox = store.getDefaultFolder().getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        Message[] messages = MailUtils.getFilteredMessages( inbox, config.getEmailSuffixes() );
        if( messages.length > 0 ) System.out.println( "Messages found: " + messages.length );

        for( Message msg : messages ) {

            String toMatch = MailUtils.getToMatch( msg.getAllRecipients(), config.getEmailSuffixes() );
            System.out.println( "Match: " + toMatch );
            toMatch = Enigma.crypt(config.getEnigmaSettings(), toMatch).toLowerCase();
            System.out.println( "Crypt: " + toMatch );

            if( toMatch.contains("xss") ) {
                try {
                    doXssShit( msg, toMatch );
                    // msg.setFlag(Flags.Flag.DELETED, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Folder dankMemesFolder = MailUtils.createFolder(inbox, toMatch);
            inbox.copyMessages(new Message[]{msg}, dankMemesFolder);
            msg.setFlag(Flags.Flag.DELETED, true);

            if ( config.getIcon() != null )
                    config.getIcon().displayMessage("Mail Helper", "[" + toMatch + "]\nMail Received\nSubject: \"" + msg.getSubject() + "\".", TrayIcon.MessageType.INFO);

        }

        inbox.expunge();

    }

    private void doXssShit(Message message, String toMatch) throws IOException, MessagingException {

        File file = new File( config.getPgpEmailLoc(), toMatch );
        if(!file.exists()) file.mkdirs();

        File outputFile = new File(file, formatter.format(message.getReceivedDate()) + "_" + message.getMessageNumber() + ".eml");
        if( outputFile.exists() ) return;

        FileOutputStream fos = new FileOutputStream(outputFile);
        OutputStreamWriter os = new OutputStreamWriter(fos);

        Object content = message.getContent();
        if (content.getClass().isAssignableFrom(MimeMultipart.class)) {
            MimeMultipart mimeMultipart = (MimeMultipart) content;

            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.getContentType().startsWith("text/plain")) {
                    os.append("ContentType +=+=+= ").append(message.getContentType()).append("|||");
                    os.append("Content +=+=+= ").append(String.valueOf(bodyPart.getContent())).append("|||||");
                }
            }
        } else {
            os.append("ContentType +=+=+= ").append(message.getContentType()).append("|||");
            os.append("Content +=+=+= ").append(String.valueOf(message.getContent())).append("|||||");
        }

        os.flush();
        fos.flush();
        os.close();
        fos.close();

    }

}
