/*
 * This file is part of GatorMail, a web based email client.
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

package edu.ufl.osg.gatormail.server.messageList;

import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFlags;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.services.MessageListService;
import edu.ufl.osg.gatormail.server.MessageServiceImpl;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of {@link edu.ufl.osg.gatormail.client.services.MessageListService}.
 *
 * @author Sandy McArthur
 * @since Sep 28, 2006 3:32:23 PM
 */
public class MessageListServiceImpl extends RemoteServiceServlet implements MessageListService {

    private static final Map<Flags.Flag, GMFlags.GMFlag> FLAGS_MAP = new HashMap<Flags.Flag, GMFlags.GMFlag>();

    static {
        FLAGS_MAP.put(Flags.Flag.ANSWERED, GMFlags.GMFlag.ANSWERED);
        FLAGS_MAP.put(Flags.Flag.DELETED, GMFlags.GMFlag.DELETED);
        FLAGS_MAP.put(Flags.Flag.DRAFT, GMFlags.GMFlag.DRAFT);
        FLAGS_MAP.put(Flags.Flag.FLAGGED, GMFlags.GMFlag.FLAGGED);
        FLAGS_MAP.put(Flags.Flag.RECENT, GMFlags.GMFlag.RECENT);
        FLAGS_MAP.put(Flags.Flag.SEEN, GMFlags.GMFlag.SEEN);
        FLAGS_MAP.put(Flags.Flag.USER, GMFlags.GMFlag.USER);
    }

    /**
     * @gwt.typeArgs <edu.ufl.osg.sandymac.mailui.ui2.client.model.GMMessage>
     */
    public List<GMMessage> fetchMessages(final Account account, final GMFolder gmFolder) throws SerializableException {
        final Session session = MessageServiceImpl.fetchSession(account);
        final Store store = MessageServiceImpl.fetchConnectedStore(session);

        final Folder folder;
        try {
            folder = store.getFolder(gmFolder.getFullName());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            folder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            return getMessages(folder, gmFolder, 0, UIDFolder.LASTUID);
        } finally {
            try {
                folder.close(false);
            } catch (MessagingException e) {
                e.printStackTrace();
                //throw new SerializableException(e.getMessage());
            }
        }
    }

    public MessageListUpdate fetchMessageListChanges(final Account account, final GMFolder gmFolder, final long startUID, final long endUID, final int messageCount) throws SerializableException {
        final Session session = MessageServiceImpl.fetchSession(account);
        final Store store = MessageServiceImpl.fetchConnectedStore(session);

        final Folder folder;
        try {
            folder = store.getFolder(gmFolder.getFullName());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        if (!(folder instanceof UIDFolder)) {
            throw new SerializableException("Folder must implement UIDFolder!");
        }

        final UIDFolder uidFolder = (UIDFolder)folder;

        // XXX: Check folder.getType()
        try {
            folder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            final MessageListUpdate update = new MessageListUpdate(startUID, endUID);
            try {
                // check UID Valididty
                if (gmFolder.getUidValidity() != uidFolder.getUIDValidity()) {
                    throw new SerializableException("uidValidity changed, must reload.");
                }


                final Message[] messageRange = uidFolder.getMessagesByUID(startUID, endUID);
                //assert messageRange.length >= 1;

                System.err.println("messageRange startUID: " + startUID + " : " + (messageRange.length > 1 ? uidFolder.getUID(messageRange[0]) : -1));
                System.err.println("messageRange endUID: " + endUID + " : " + (messageRange.length > 1 ? uidFolder.getUID(messageRange[messageRange.length-1]) : -1));

                sortByUID(uidFolder, messageRange);

                if (messageRange.length > 0) {
                    assert uidFolder.getUID(messageRange[0]) >= startUID : "A message with an unexpected lower UID snuck in there.";
                    assert uidFolder.getUID(messageRange[messageRange.length-1]) <= endUID : "A message with an unexpected higher UID snuck in there.";
                    assert messageRange.length <= messageCount : "A message snuck into the middle.";
                }

                // Return still valid UIDs so the ones not found can be pruned.
                if (messageRange.length < messageCount) {
                    final long[] uids = new long[messageRange.length];
                    for (int i=0; i < messageRange.length; i++) {
                        uids[i] = uidFolder.getUID(messageRange[i]);
                    }
                    update.setValidUIDs(uids);
                }

                // Look for new messages out side our range
                // XXX? account for removed messages in the middle if possible.
                if (folder.getMessageCount() != messageCount) {
                    if (startUID > 0) {
                        update.setBeforeStart(getMessages(folder, gmFolder, 0, startUID-1));
                    }
                    update.setAfterEnd(getMessages(folder, gmFolder, endUID+1, Long.MAX_VALUE));
                }

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new SerializableException(e.getMessage());
            }
            return update;
        } finally {
            try {
                folder.close(false);
            } catch (MessagingException e) {
                e.printStackTrace();
                //throw new SerializableException(e.getMessage());
            }
        }
    }

    private List<GMMessage> getMessages(final Folder folder, final GMFolder gmFolder, final long startUID, final long endUID) throws SerializableException {
        final UIDFolder uidFolder = (UIDFolder)folder;

        assert folder.isOpen();

        final List<GMMessage> gmMessages;
        {


            List<Message> messages;
            try {
                final Message[] messageRange = uidFolder.getMessagesByUID(startUID, endUID);
                sortByUID((UIDFolder)folder, messageRange);
                messages = new ArrayList(Arrays.asList(messageRange));

                // double check in case the IMAP server is buggy
                final Iterator<Message> iter = messages.iterator();
                while (iter.hasNext()) {
                    final Message message = iter.next();
                    if (uidFolder.getUID(message) < startUID) {
                        iter.remove();
                    } else if (endUID!= UIDFolder.LASTUID && endUID < uidFolder.getUID(message)) {
                        iter.remove();
                    }
                }

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new SerializableException(e.getMessage());
            }

            //messages = messages.subList(0, Math.min(40, messages.size()));

            // Prefetch all flags to improve performance.
            Message[] m = new Message[messages.size()];
            m = messages.toArray(m);
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.FLAGS);
            try {
                folder.fetch(m, fp);
            } catch (MessagingException e) {
                e.printStackTrace();
                // ignore for now
            }

            gmMessages = new ArrayList(messages.size());
            for (final Message message : messages) {
                final GMMessage gmMessage = new GMMessage();
                gmMessage.setFolder(gmFolder);

                try {
                    gmMessage.setUid(uidFolder.getUID(message));
                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new SerializableException(e.getMessage());
                }

                try {
                    final GMFlags gmFlags = new GMFlags();
                    final Flags flags = message.getFlags();
                    for (final Flags.Flag flag : Arrays.asList(flags.getSystemFlags())) {
                        gmFlags.addFlag(FLAGS_MAP.get(flag));
                    }
                    gmMessage.setFlags(gmFlags);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                gmMessages.add(gmMessage);
            }

        }

        return gmMessages;
    }

    private void sortByUID(final UIDFolder uidFolder, final Message[] messages) {
        try {
            final FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            ((Folder)uidFolder).fetch(messages, fp);
        } catch (MessagingException e) {
            // ignore for now
        }

        Arrays.sort(messages, new Comparator<Message>() {
            public int compare(final Message m1, final Message m2) {
                try {
                    final long uid1 = uidFolder.getUID(m1);
                    final long uid2 = uidFolder.getUID(m2);
                    if (uid1 < uid2) {
                        return -1;
                    } else if (uid1 > uid2) {
                        return 1;
                    } else {
                        assert false : "Two messages with the same UID should never be in the same folder.";
                        return 0;
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
        });
    }
}