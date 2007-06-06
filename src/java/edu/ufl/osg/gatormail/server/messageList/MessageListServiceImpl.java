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
import edu.ufl.osg.gatormail.client.model.UidValidityChangedException;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.messageList.Filter;
import edu.ufl.osg.gatormail.client.model.messageList.Prescript;
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
    private static final Map<MessageOrder, Comparator<Message>> MESSAGE_ORDERS = new HashMap<MessageOrder, Comparator<Message>>();

    static {
        FLAGS_MAP.put(Flags.Flag.ANSWERED, GMFlags.GMFlag.ANSWERED);
        FLAGS_MAP.put(Flags.Flag.DELETED, GMFlags.GMFlag.DELETED);
        FLAGS_MAP.put(Flags.Flag.DRAFT, GMFlags.GMFlag.DRAFT);
        FLAGS_MAP.put(Flags.Flag.FLAGGED, GMFlags.GMFlag.FLAGGED);
        FLAGS_MAP.put(Flags.Flag.RECENT, GMFlags.GMFlag.RECENT);
        FLAGS_MAP.put(Flags.Flag.SEEN, GMFlags.GMFlag.SEEN);
        FLAGS_MAP.put(Flags.Flag.USER, GMFlags.GMFlag.USER);

        MESSAGE_ORDERS.put(MessageOrder.RECEIVED, new Comparator<Message>() {
            public int compare(final Message m1, final Message m2) {
                try {
                    return m1.getReceivedDate().compareTo(m2.getReceivedDate());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        MESSAGE_ORDERS.put(MessageOrder.SENT, new Comparator<Message>() {
            public int compare(final Message m1, final Message m2) {
                try {
                    return m1.getSentDate().compareTo(m2.getSentDate());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * @gwt.typeArgs <edu.ufl.osg.sandymac.mailui.ui2.client.model.GMMessage>
     */
    public MessageListUpdate fetchMessages(final Account account, final GMFolder gmFolder, final Prescript prescript) throws SerializableException {
        final Session session = MessageServiceImpl.fetchSession(account);
        final Store store = MessageServiceImpl.fetchConnectedStore(session);

        final Folder folder;
        try {
            folder = store.getFolder(gmFolder.getFullName());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        final UIDFolder uidFolder = (UIDFolder)folder;
        checkUidValidity(gmFolder, uidFolder);

        try {
            folder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            final Message[] messages;
            if (Filter.ALL.equals(prescript.getFilter())) {
                try {
                    messages = folder.getMessages();
                } catch (MessagingException e) {
                    throw new SerializableException();
                }
            } else {
                throw new SerializableException("Filter not yet supported: " + prescript.getFilter());
            }

            // Prefetch all flags to improve performance.
            final FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.FLAGS);
            fp.add(FetchProfile.Item.ENVELOPE);
            try {
                folder.fetch(messages, fp);
            } catch (MessagingException e) {
                e.printStackTrace();
                // XXX: ignore for now
            }

            final Comparator<Message> c = new Comparator<Message>() {
                public int compare(final Message m1, final Message m2) {
                    try {
                        return m1.getReceivedDate().compareTo(m2.getReceivedDate());
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            try {
                Arrays.sort(messages, c);
            } catch (RuntimeException re) {
                re.printStackTrace();
                throw re;
            }


            final long[] uids = new long[messages.length];
            for (int i=0; i < messages.length; i++) {
                final Message message = messages[i];

                try {
                    uids[i] = (uidFolder.getUID(message));
                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new SerializableException(e.getMessage());
                }
            }

            return new MessageListUpdate(prescript, uids);
        } finally {
            try {
                folder.close(false);
            } catch (MessagingException e) {
                e.printStackTrace();
                //throw new SerializableException(e.getMessage());
            }
        }
    }

    private void checkUidValidity(final GMFolder gmFolder, final UIDFolder uidFolder) throws UidValidityChangedException {
        long validity;
        try {
            validity = uidFolder.getUIDValidity();
        } catch (MessagingException e) {
            validity = -1;
        }
        if (validity < 0 || validity != gmFolder.getUidValidity()) {
            throw new UidValidityChangedException(gmFolder.getFullName(), gmFolder.getUidValidity(), validity);
        }
    }

    public long[] fetchMessageUids(final Account account, final GMFolder gmFolder, final MessageOrder order) throws SerializableException {
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
            final UIDFolder uidFolder = (UIDFolder)folder;

            assert folder.isOpen();

            final Message[] messages;
            try {
                messages = folder.getMessages();

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new SerializableException(e.getMessage());
            }

            // Prefetch all flags to improve performance.
            final FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.FLAGS);
            fp.add(FetchProfile.Item.ENVELOPE);
            try {
                folder.fetch(messages, fp);
            } catch (MessagingException e) {
                e.printStackTrace();
                // ignore for now
            }

            // TODO: Why is this sometimes null?
            Comparator<Message> c = MESSAGE_ORDERS.get(order);
            c = new Comparator<Message>() {
                public int compare(final Message m1, final Message m2) {
                    try {
                        return m1.getReceivedDate().compareTo(m2.getReceivedDate());
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            try {
                Arrays.sort(messages, c);
            } catch (RuntimeException re) {
                re.printStackTrace();
                throw re;
            }

            final long[] uids = new long[messages.length];
            for (int i=0; i < messages.length; i++) {
                final Message message = messages[i];

                try {
                    uids[i] = (uidFolder.getUID(message));
                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new SerializableException(e.getMessage());
                }
            }

            return uids;
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


            final List<Message> messages;
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
            final FetchProfile fp = new FetchProfile();
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