package co.ryred.mail_helper.mail;

import co.ryred.mail_helper.MailHelperConfig;
import co.ryred.mail_helper.enigma.Enigma;

import javax.mail.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cory Redmond on 13/04/2017.
 * ace@ac3-servers.eu
 */
public class MailOrganiser extends Thread {

    private final MailHelperConfig config;

    private Store store = null;

    public MailOrganiser(MailHelperConfig config) {
        super( "Mail Organiser Thread" );
        this.config = config;
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
            Folder dankMemesFolder = MailUtils.createFolder(inbox, toMatch);
            inbox.copyMessages(new Message[]{msg}, dankMemesFolder);

            msg.setFlag(Flags.Flag.DELETED, true);

        }

        inbox.expunge();

    }

}
