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

import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Purges messages based on the number of days they have been in a folder.
 *
 * @author Sandy McArthur
 */
public class AutoPurge {

    private static final Logger LOGGER = Logger.getLogger(AutoPurge.class.getName());

    private static final FetchProfile FLAGS_FETCH_PROFILE = new FetchProfile();
    static {
        FLAGS_FETCH_PROFILE.add(FetchProfile.Item.FLAGS);
    }

    private final Properties props;

    private final Authenticator authenticator = new CustomAuthenticator();
    private final String authenticationName;
    private final String authenticationPassword;
    private final List purgeRoots;
    private final String purgeFlagPrefix;
    private final Pattern purgePattern;
    private final DateFormat dateFormat;
    private final int expireAfterDays;
    private final String userMailboxNamespace;
    private final String mailStoreProtocol;

    private final ThreadLocal currentUser = new ThreadLocal();

    final StatsThreadLocal currentStats = new StatsThreadLocal();

    public AutoPurge(final Properties props) {
        this.props = (Properties)props.clone();

        final Logger initLogger = Logger.getLogger(AutoPurge.class.getName() + ".<init>");

        mailStoreProtocol = props.getProperty("mail.store.protocol");
        initLogger.config("mail.store.protocol=" + mailStoreProtocol);
        initLogger.config("mail." + mailStoreProtocol + ".host=" + props.getProperty("mail." + mailStoreProtocol + ".host"));
        initLogger.config("mail." + mailStoreProtocol + ".port=" + props.getProperty("mail." + mailStoreProtocol + ".port"));

        LOGGER.info("Connecting to " + mailStoreProtocol + "://" + props.getProperty("mail." + mailStoreProtocol + ".host") + ":" + props.getProperty("mail." + mailStoreProtocol + ".port"));

        authenticationName = props.getProperty("autopurge.authentication.name");
        initLogger.config("autopurge.authentication.name=" + authenticationName);

        authenticationPassword = props.getProperty("autopurge.authentication.pass");
        initLogger.config("autopurge.authentication.pass=" + authenticationPassword);

        userMailboxNamespace = props.getProperty("autopurge.namespace.user");
        initLogger.config("autopurge.namespace.user=" + userMailboxNamespace);

        purgeRoots = Collections.unmodifiableList(
                Arrays.asList(props.getProperty("autopurge.purge.roots").split(",")));
        initLogger.config("autopurge.purge.roots=" + purgeRoots);
        LOGGER.info("Mailbox purge roots are: " + purgeRoots);

        if (props.getProperty("autopurge.purge.pattern") != null) {
            purgePattern = Pattern.compile(props.getProperty("autopurge.purge.pattern"));
            initLogger.config("autopurge.purge.pattern=" + props.getProperty("autopurge.purge.pattern"));
            LOGGER.info("Only mailboxes usernames matching '" + props.getProperty("autopurge.purge.pattern") + "' will be included in the discovered username list.");
        } else {
            purgePattern = null;
            initLogger.config("autopurge.purge.pattern not set.");
        }

        purgeFlagPrefix = props.getProperty("autopurge.purge.flag.prefix");
        initLogger.config("autopurge.purge.flag.prefix=" + purgeFlagPrefix);

        dateFormat = new SimpleDateFormat(props.getProperty("autopurge.purge.flag.format"));
        initLogger.config("autopurge.purge.flag.format=" + dateFormat);

        expireAfterDays = Integer.parseInt(props.getProperty("autopurge.expire.after.days", "-1"));
        initLogger.config("autopurge.expire.after.days=" + expireAfterDays);

        if (expireAfterDays > 0) {
            LOGGER.info("Messages with a auto purge flag older than " + expireAfterDays + " days will expunged.");
        } else if (expireAfterDays < 0) {
            LOGGER.info("Pretend auto purge messages with a flag older than " + Math.abs(expireAfterDays) + " days.");
        } else {
            LOGGER.info("Message auto purging is disabled. To enable set autopurge.expire.after.days to a positive value.");
        }
    }

    public List discoverUserNames() throws MessagingException {
        final Logger logger = Logger.getLogger(AutoPurge.class.getName() + ".discoverUserNames");

        final Session session = Session.getInstance(props, authenticator);
        final Store store;
        try {
            store = session.getStore();
        } catch (NoSuchProviderException e) {
            logger.log(Level.FINE, "Unable to load a suitable JavaMail provider.", e);
            throw e;
        }

        try {
            try {
                store.connect();
            } catch (MessagingException me) {
                logger.log(Level.FINE, "Unable to connect to mail store.", me);
                throw me;
            }

            final Folder userNamespace;
            try {
                userNamespace = store.getFolder(userMailboxNamespace);
            } catch (MessagingException e) {
                logger.log(Level.FINE, "Exception opening user mailbox namespace folder: " + userMailboxNamespace, e);
                throw e;
            }

            final Folder[] userFolders;
            try {
                userFolders = userNamespace.list();
            } catch (MessagingException e) {
                logger.log(Level.FINE, "Exception listing user mailboxes.", e);
                throw e;
            }

            final List userNames = new ArrayList(userFolders.length);

            for (int i=0; i < userFolders.length; i++) {
                final String userName = userFolders[i].getName();
                if (purgePattern != null) {
                    if (!purgePattern.matcher(userName).matches()) {
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest(userName + " excluded by pattern.");
                        }
                        continue;
                    } else {
                        logger.finer(userName + " included by pattern.");
                    }
                }
                userNames.add(userName);
            }

            return userNames;
        } finally {
            try {
                store.close();
            } catch (MessagingException e) {
                logger.log(Level.FINER, "Exception closing mail store. Exception swallowed.", e);
            }
        }
    }

    public void purgeMailboxes(final List usernames) throws NoSuchProviderException {
        final Logger logger = Logger.getLogger(AutoPurge.class.getName() + ".purgeMailbox");

        final Iterator iter = usernames.iterator();

        while (iter.hasNext()) {
            final String username = (String)iter.next();
            currentUser.set(username);
            currentStats.getStats().processedUsers++;
            logger.fine("Processing username: " + username);

            final Properties props = getProperties(username);
            final Session session = Session.getInstance(props, authenticator);
            final Store store;
            try {
                store = session.getStore();
            } catch (NoSuchProviderException e) {
                logger.log(Level.FINE, "Unable to load a suitable JavaMail provider.", e);
                throw e; // exit method, not likely to recover from this
            }

            try {
                try {
                    store.connect();
                } catch (MessagingException me) {
                    logger.log(Level.FINE, "Unable to connect to mail store for " + username, me);
                    throw me;
                }

                final Iterator prIter = purgeRoots.iterator();
                while (prIter.hasNext()) {
                    final String purgeRootName = (String)prIter.next();
                    final Folder purgeRoot;
                    try {
                        purgeRoot = store.getFolder(purgeRootName);
                    } catch (MessagingException e) {
                        logger.log(Level.FINE, "Exception opening " + purgeRootName + " for " + username, e);
                        currentStats.getStats().problemFolders++;
                        continue;
                    }
                    purge(purgeRoot);
                }

            } catch (MessagingException e) {
                logger.log(Level.WARNING, "Unexpected error for mailbox: " + username, e);
                currentStats.getStats().problemMailboxes++;

            } finally {
                try {
                    store.close();
                    currentStats.getStats().processedMailboxes++;
                } catch (MessagingException e) {
                    logger.log(Level.FINER, "Exception closing mail store. Exception swallowed.", e);
                }
            }
        }
    }

    private void purge(final Folder folder)  {
        try {
            if (!folder.exists()) {
                final Logger logger = Logger.getLogger(AutoPurge.class.getName() + ".purgeMailbox");
                logger.fine(currentUser.get() + ": folder " + folder + " does not exists.");
                return; // next folder, this one doesn't exist.
            }
        } catch (MessagingException e) {
            final Logger logger = Logger.getLogger(AutoPurge.class.getName() + ".purgeMailbox");
            logger.log(Level.FINER, currentUser.get() + ": unexpected error checking if " + folder + " exists.", e);
            return; // skip this folder
        }

        // Auto Purge current folder
        try {
            purgeMessages(folder);
        } catch (MessagingException e) {
            final Logger logger = Logger.getLogger(AutoPurge.class.getName() + ".purgeMessage");
            logger.log(Level.WARNING, currentUser.get() + ": unexpected error purging messages from " + folder, e);
        }

        // Auto purge sub folders
        Folder[] subFolders = null;
        try {
            subFolders = folder.list();
        } catch (MessagingException e) {
            final Logger logger = Logger.getLogger(AutoPurge.class.getName() + ".purgeMailbox");
            logger.log(Level.FINE, currentUser.get() + ": unexpected error getting list of subfolders from " + folder, e);
        }
        if (subFolders != null) {
            for (int i=0; i < subFolders.length; i++) {
                purge(subFolders[i]);
            }
        }
    }

    private void purgeMessages(final Folder folder) throws MessagingException {
        final Logger logger = Logger.getLogger(AutoPurge.class.getName() + ".purgeMessage");
        if (folder.exists() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            try {
                folder.open(Folder.READ_WRITE);
            } catch (MessagingException e) {
                logger.log(Level.FINE, currentUser.get() + ": unable to open " + folder, e);
                return;
            }
            try {
                final Message[] messages;
                try {
                    messages = folder.getMessages();
                    folder.fetch(messages, FLAGS_FETCH_PROFILE);
                } catch (MessagingException e) {
                    logger.log(Level.FINE, currentUser.get() + ": exception fetching messages in folder " + folder, e);
                    currentStats.getStats().problemFolders++;
                    return;
                }

                for (int i=0; i < messages.length; i++) {
                    processMessage(messages[i]);
                }

            } finally {
                try {
                    folder.close(true);
                    currentStats.getStats().processedFolders++;
                } catch (MessagingException me) {
                    logger.log(Level.FINE, currentUser.get() + ": unable to close and expunge " + folder, me);
                }
            }
        } else {
            logger.fine(currentUser.get() + " skipping " + folder + " because it doesn't exist or doesn't hold messages.");
        }
    }

    private void processMessage(final Message message) {
        final Logger logger = Logger.getLogger(AutoPurge.class.getName() + ".purgeMessage");
        try {
            final String[] flags = message.getFlags().getUserFlags();
            Date taggedDate = null;
            for (int j=0; j < flags.length; j++) {
                if (flags[j].startsWith(purgeFlagPrefix)) {
                    String tag = flags[j];
                    tag = tag.substring(purgeFlagPrefix.length());
                    try {
                        taggedDate = dateFormat.parse(tag);
                        break;
                    } catch (ParseException e) {
                        logger.log(Level.FINEST, currentUser.get() + ": Pasrse exception of flag: " + flags[j], e);
                    }
                }
            }
            if (taggedDate == null) {
                // add tag
                final String tag = purgeFlagPrefix + dateFormat.format(new Date());
                if (message.getFolder() instanceof UIDFolder) {
                    final UIDFolder uidFolder = (UIDFolder)message.getFolder();
                    logger.finest(currentUser.get() + ": adding flag " + tag + " to message uid: " + uidFolder.getUID(message) + " in folder " + uidFolder);
                } else {
                    logger.finest(currentUser.get() + ": adding flag " + tag + " to message " + message.getMessageNumber() + " in folder " + message.getFolder());
                }

                final Flags flag = new Flags(tag);
                message.setFlags(flag, true);
                currentStats.getStats().flaggedMessages++;

            } else if (expireAfterDays != 0) {
                // check if expired
                final Calendar taggedCal = Calendar.getInstance();
                taggedCal.setTime(taggedDate);
                final Calendar expireCal = Calendar.getInstance();
                expireCal.add(Calendar.DATE, -1 * Math.abs(expireAfterDays));
                if (taggedCal.before(expireCal)) {
                    if (expireAfterDays > 0) {
                        if (message.getFolder() instanceof UIDFolder) {
                            final UIDFolder uidFolder = (UIDFolder)message.getFolder();
                            logger.finest(currentUser.get() + ": purging message uid: " + uidFolder.getUID(message) + " in folder " + uidFolder);
                        } else {
                            logger.finest(currentUser.get() + ": purging message " + message.getMessageNumber() + " in folder " + message.getFolder());
                        }
                        message.setFlag(Flags.Flag.DELETED, true);

                    } else if (expireAfterDays < 0) {
                        if (message.getFolder() instanceof UIDFolder) {
                            final UIDFolder uidFolder = (UIDFolder)message.getFolder();
                            logger.finest(currentUser.get() + ": would have purged message uid: " + uidFolder.getUID(message) + " in folder " + uidFolder);
                        } else {
                            logger.finest(currentUser.get() + ": would have purged message " + message.getMessageNumber() + " in folder " + message.getFolder());
                        }
                    }
                    currentStats.getStats().deletedMessages++;

                } else {
                    if (message.getFolder() instanceof UIDFolder) {
                        final UIDFolder uidFolder = (UIDFolder)message.getFolder();
                        logger.finest(currentUser.get() + ": " + ElapsedTime.getDays(Calendar.getInstance(), taggedCal) + " days old, keeping message uid: " + uidFolder.getUID(message) + " in folder " + uidFolder);
                    } else {
                        logger.finest(currentUser.get() + ": " + ElapsedTime.getDays(Calendar.getInstance(), taggedCal) + " days old, keeping message " + message.getMessageNumber() + " in folder " + message.getFolder());
                    }
                    currentStats.getStats().keptMessages++;
                }
            }
        } catch (MessagingException e) {
            // ignore, problem with this message, try the next one
            logger.log(Level.FINER, currentUser.get() + ": problem with message: " + message.getMessageNumber(), e);
        }
    }

    private Properties getProperties(final String username) {
        final Properties props = new Properties(this.props);
        props.setProperty("mail." + mailStoreProtocol  + ".sasl.authorizationid", username);
        return props;
    }

    private class CustomAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(authenticationName, authenticationPassword);
        }
    }

    private static class Stats {
        public int processedUsers;
        public int processedMailboxes;
        public int processedFolders;

        public int problemMailboxes;
        public int problemFolders;
        public int flaggedMessages;
        public int deletedMessages;
        public int keptMessages;

        public String toString() {
            return "Stats {" +
                    "users=" + processedUsers +
                    ", mailboxes=" + processedMailboxes + "/" + problemMailboxes +
                    ", folders=" + processedFolders + "/" + problemFolders +
                    ", flaggedMessages=" + flaggedMessages +
                    ", keptMessages=" + keptMessages +
                    ", deletedMessages=" + deletedMessages +
                    '}';
        }
    }

    class StatsThreadLocal extends ThreadLocal {
        protected Object initialValue() {
            return new Stats();
        }

        public Stats getStats() {
            return (Stats)get();
        }
    }
}
