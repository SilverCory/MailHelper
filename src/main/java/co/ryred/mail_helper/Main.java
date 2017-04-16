package co.ryred.mail_helper;

import co.ryred.mail_helper.gui.MailTrayIcon;
import co.ryred.mail_helper.mail.MailOrganiser;

import javax.mail.MessagingException;
import java.io.File;

/**
 * Created by Cory Redmond on 13/04/2017.
 * ace@ac3-servers.eu
 */
public class Main {

    public static void main(String[] args) throws MessagingException {

        File configFile = new File("config.json");

        if(!configFile.exists()) {
            MailHelperConfig.getDefaultConfiguration().save( configFile );
            System.out.println( "Default configuration saved, please edit it!" );
            System.exit(1);
        }

        MailHelperConfig config = MailHelperConfig.load( configFile );

        MailTrayIcon.show(config);
        //MailTrayPopup.display(config);

        MailOrganiser organiser = new MailOrganiser(config);
        organiser.setDaemon(false);
        organiser.start();

    }

}
