package co.ryred.mail_helper.mail;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AddressStringTerm;
import javax.mail.search.RecipientStringTerm;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Cory Redmond on 14/04/2017.
 * ace@ac3-servers.eu
 */
public class MailUtils {

    public static String getToMatch( Address[] addresses, String... matches ) {

        Set<AddressFilter> filters = new HashSet<>();
        for( String match : matches ) filters.add(new AddressFilter("_e@" + match));

        for( Address address : addresses ) {
            if(!(address instanceof InternetAddress)) continue;
            InternetAddress addr = ((InternetAddress) address);
            for( AddressFilter filter : filters ) {
                if( filter.matches(addr.getAddress()) )
                    return filter.matchPrefix(addr.getAddress());
            }
        }

        return null;

    }

    public static Message[] getFilteredMessages(Folder search, String... matchAddresses ) throws MessagingException {

        HashSet<Message> matchedMessages = new HashSet<>();

        for( String address : matchAddresses ) {
            matchedMessages.addAll(Arrays.asList(search.search(new RecipientStringTerm(Message.RecipientType.TO, "_e@" + address))));
            matchedMessages.addAll(Arrays.asList(search.search(new RecipientStringTerm(Message.RecipientType.BCC, "_e@" + address))));
            matchedMessages.addAll(Arrays.asList(search.search(new RecipientStringTerm(Message.RecipientType.CC, "_e@" + address))));
        }

        return matchedMessages.toArray( new Message[matchedMessages.size()] );

    }

    /**
     * Note that in Gmail folder hierarchy is not maintained.
     **/
    public static Folder createFolder(Folder parent, String folderName) {

        Folder newFolder;

        try {
            newFolder = parent.getFolder(folderName);
            newFolder.create(Folder.HOLDS_MESSAGES);
        } catch (Exception e) {
            System.out.println("Error creating folder: " + e.getMessage());
            e.printStackTrace();
            newFolder = null;
        }

        return newFolder;

    }

    public static class AddressFilter extends AddressStringTerm {

        /**
         * Constructor.
         *
         * @param pattern the address pattern to be compared.
         */
        protected AddressFilter(String pattern) {
            super(pattern);
        }

        @Override
        public boolean match(Message msg) {
            throw new UnsupportedOperationException();
        }

        public boolean matches( String matches ) {
            return super.match(matches);
        }

        public String matchPrefix( String matches ) {

            int len = matches.length() - pattern.length();
            for (int i=0; i <= len; i++) {
                if (matches.regionMatches(ignoreCase, i, pattern, 0, pattern.length()))
                    return matches.substring(0, i);
            }

            return null;

        }

    }

}
