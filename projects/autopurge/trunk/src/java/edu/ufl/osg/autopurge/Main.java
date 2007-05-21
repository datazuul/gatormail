/*
 * This file is part of GatorMail AutoPurge, a tool to purge old messages.
 *
 * Copyright (C) 2007 The Open Systems Group / University of Florida
 *
 * GatorMail is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GatorMail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GatorMail; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ufl.osg.autopurge;

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.List;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * GatorMail AutoPurge launch class.
 *
 * @author Sandy McArthur
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final int EXIT_NO_HOST = 10;
    private static final int EXIT_NO_PORT = 11;
    private static final int EXIT_NO_AUTH_NAME = 20;
    private static final int EXIT_NO_AUTH_PASS = 21;
    private static final int EXIT_NO_PURGE_ROOTS = 30;


    public static void main(final String[] args) throws IOException, MessagingException {

        final Properties props = new Properties(System.getProperties());

        if (args.length == 1) {
            props.load(new FileInputStream(args[0]));
        }

        populateDefaultProps(props);

        LOGGER.info("GatorMail AutoPurge started.");

        final String mailStoreProtocol = props.getProperty("mail.store.protocol");

        final String mailHostKey = "mail." + mailStoreProtocol + ".host";
        if (props.getProperty(mailHostKey) == null) {
            showHelp(mailHostKey + " has not been set!");
            System.exit(EXIT_NO_HOST);
            return;
        }

        if (props.getProperty("mail." + mailStoreProtocol + ".port") == null) {
            showHelp("Unknown default port for mail.store.protocol: " + mailStoreProtocol);
            System.exit(EXIT_NO_PORT);
            return;
        }

        if (props.getProperty("autopurge.authentication.name") == null) {
            showHelp("autopurge.authentication.name not set.");
            System.exit(EXIT_NO_AUTH_NAME);
            return;
        }

        if (props.getProperty("autopurge.authentication.pass") == null) {
            showHelp("autopurge.authentication.pass not set.");
            System.exit(EXIT_NO_AUTH_PASS);
            return;
        }

        if (props.getProperty("autopurge.purge.roots") == null) {
            showHelp("autopurge.purge.roots not set.");
            System.exit(EXIT_NO_PURGE_ROOTS);
            return;
        }

        final AutoPurge autoPurge = new AutoPurge(props);

        final List usernames;
        try {
            usernames = autoPurge.discoverUserNames();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected failure discovering usernames.", e);
            System.exit(1);
            return; // just to make the compiler happy.
        }

        LOGGER.fine("Usernames discovered: " + usernames.size());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("Usernames: " + usernames);
        }

        if (Boolean.valueOf(props.getProperty("autopurge.usernames.shuffle")).booleanValue()) {
            LOGGER.fine("Shuffling usernames");
            Collections.shuffle(usernames);
        }

        try {
            autoPurge.purgeMailboxes(usernames);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected failure pruging mailboxes.", e);
            System.exit(1);
            return; // just to make the compiler happy.
        }

        LOGGER.info("GatorMail AutoPurge completed normally.");
    }

    private static void populateDefaultProps(final Properties props) {
        String mailStoreProtocol = props.getProperty("mail.store.protocol");

        if (mailStoreProtocol == null) {
            mailStoreProtocol = "imap";
            props.setProperty("mail.store.protocol", mailStoreProtocol);
        }

        if (props.getProperty("mail." + mailStoreProtocol + ".port") == null) {
            if ("imap".equals(mailStoreProtocol)) {
                props.setProperty("mail." + mailStoreProtocol + ".port", "143");
            } else if ("imaps".equals(mailStoreProtocol)) {
                props.setProperty("mail." + mailStoreProtocol + ".port", "993");
            }
        }

        if (props.getProperty("autopurge.namespace.user") == null) {
            props.setProperty("autopurge.namespace.user", "user");
        }

        if (props.getProperty("autopurge.usernames.shuffle") == null) {
            props.setProperty("autopurge.usernames.shuffle", Boolean.TRUE.toString());
        }

        if (props.getProperty("autopurge.purge.flag.prefix") == null) {
            props.setProperty("autopurge.purge.flag.prefix", "Discarded-");
        }

        if (props.getProperty("autopurge.purge.flag.format") == null) {
            props.setProperty("autopurge.purge.flag.format", "yyyy-MM-dd");
        }

        if (props.getProperty("autopurge.expire.after.days") == null) {
            props.setProperty("autopurge.expire.after.days", "-30");
        }

    }


    private static void showHelp(final String message) {
        if (message != null) {
            System.err.println(message);
            System.err.println("");
        }
        System.err.println("GatorMail AutoPurge\thttp://code.google.com/p/gatormail/");
        System.err.println("");
        System.err.println("To load configuration from a Java Properties file use:");
        System.err.println("\tjava -jar AutoPurge.jar <AutoPurge.properties>");
        System.err.println("");
        System.err.println("To set Java Properties on the command line use:");
        System.err.println("\tjava -Dprop.name1=value -Dprop.name2=value -jar AutoPurge.jar");
        System.err.println("");
        System.err.println("or use a combination of both. The property file settings will take precedence.");
        System.err.println("");
        System.err.println("Common Properties include [defaults]:");
        System.err.println("\tmail.store.protocol: Protocol to use [imap]");
        System.err.println("\tmail.${mail.store.protocol}.host: Mail store hostname, eg: imap.example.com");
        System.err.println("\tautopurge.authentication.name: Privilaged user to authenticate as");
        System.err.println("\tautopurge.authentication.pass: Privilaged user's password");
        System.err.println("\tautopurge.namespace.user: prefix of user folders namespace [user]");
        System.err.println("\tautopurge.usernames.shuffle: false to process the discovered usernames in the order discovered [true]");
        System.err.println("\tautopurge.purge.roots: Comma seperated list of folders in each user's mailbox to auto purge. eg: INBOX/Trash,INBOX/Junk");
        System.err.println("\tautopurge.purge.pattern: If set, only purges mailboxes that match the regex pattern. (see java.util.regex.Pattern)");
        System.err.println("\tautopurge.purge.flag.prefix: Prefix of the auto purge flag [Discarded-]");
        System.err.println("\tautopurge.purge.flag.format: Format of date in auto purge flag [yyyy-MM-dd] (see java.text.SimpleDateFormat)");
        System.err.println("\tautopurge.expire.after.days: Expire messages known to be older than number of days. Zero disables, negative pretends. [-30]");
        //System.err.println("\t");
        System.err.println("");
        System.err.println("Logging can be controled via the java.util.logging mechanisms. (see java.util.logging.LogManager)");
        System.err.println("Example config files are available in the jar, run: unzip AutoPurge.jar config/*");
    }
}
