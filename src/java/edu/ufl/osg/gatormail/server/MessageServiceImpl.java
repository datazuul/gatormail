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

package edu.ufl.osg.gatormail.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.client.rpc.SerializableException;
import edu.ufl.osg.gatormail.client.services.MessageService;
import edu.ufl.osg.gatormail.client.services.LoginService;
import edu.ufl.osg.gatormail.client.model.message.GMMessagePart;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageSummary;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.model.UidValidityChangedException;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMMixed;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMAlternative;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMDigest;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMParallel;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMRelated;
import edu.ufl.osg.gatormail.client.model.message.text.GMPlain;
import edu.ufl.osg.gatormail.client.model.message.text.GMHtml;
import edu.ufl.osg.gatormail.client.model.GMAddress;
import edu.ufl.osg.gatormail.client.model.GMInternetAddress;
import edu.ufl.osg.gatormail.client.model.GMNewsAddress;
import edu.ufl.osg.gatormail.client.model.impl.GatorLinkAccount;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Flags;
import javax.mail.UIDFolder;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.NewsAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.Iterator;
import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringWriter;

import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;

/**
 * TODO: Write class JavaDoc.
 *
 * @author Sandy McArthur
 */
public class MessageServiceImpl extends RemoteServiceServlet implements MessageService {

    public GMMessageHeaders fetchHeaders(final Account account, final GMMessage gmMessage) throws SerializableException {
        final Session session = fetchSession(account);
        final Store store = fetchConnectedStore(session);

        final GMFolder gmFolder = gmMessage.getFolder();
        final Folder folder;
        try {
            folder = store.getFolder(gmFolder.getFullName());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        // XXX: Check folder.getType()
        try {
            folder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            final Message message;

            try {
                final UIDFolder uidFolder = (UIDFolder)folder;
                message = uidFolder.getMessageByUID(gmMessage.getUid());

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new SerializableException(e.getMessage());
            }

            return convertHeaders(message);
        } finally {
            try {
                folder.close(false);
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new SerializableException(e.getMessage());
            }
        }
    }

    public GMMessageSummary fetchSummary(final Account account, final GMMessage gmMessage) throws SerializableException {
        final Session session = fetchSession(account);
        final Store store = fetchConnectedStore(session);

        final GMFolder gmFolder = gmMessage.getFolder();
        final Folder folder;
        try {
            folder = store.getFolder(gmFolder.getFullName());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        // XXX: Check folder.getType()
        try {
            folder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            final Message message;

            try {
                final UIDFolder uidFolder = (UIDFolder)folder;
                message = uidFolder.getMessageByUID(gmMessage.getUid());
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new SerializableException(e.getMessage());
            }

            return createSummary(message);
        } finally {
            try {
                folder.close(false);
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new SerializableException(e.getMessage());
            }
        }
    }

    public GMMessagePart fetchMessageBody(final Account account, final GMMessage gmMessage) throws SerializableException {
        final Session session = fetchSession(account);
        final Store store = fetchConnectedStore(session);

        GMMessagePart part = null;
        try {
            Folder folder = null;
            try {
                final GMFolder gmFolder = gmMessage.getFolder();
                // XXX: Don't assume all messages are from the same folder.
                try {
                    folder = store.getFolder(gmFolder.getFullName());
                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new SerializableException(e.getMessage());
                }

                folder.open(Folder.READ_ONLY);

                final UIDFolder uidFolder = (UIDFolder)folder;

                // Check that the UID Validity hasn't changed
                final long uidValidity = uidFolder.getUIDValidity();
                if (gmFolder.getUidValidity() != uidValidity) {
                    throw new UidValidityChangedException(folder.getFullName(), gmFolder.getUidValidity(), uidValidity);
                }

                final Message message = uidFolder.getMessageByUID(gmMessage.getUid());

                part = convertMessageParts(message);
                System.err.println("message.contentType: " + message.getContentType());
                System.err.println("message.content.class: " + message.getContent().getClass());

            } catch (MessagingException e) {
                throw new SerializableException("MessagingException message: " + gmMessage + ", reason: " + e.getMessage());

            } catch (IOException e) {
                throw new SerializableException("IOException message: " + gmMessage + ", reason: " + e.getMessage());

            } finally {
                if (folder != null) {
                    try {
                        folder.close(false);
                    } catch (MessagingException e) {
                        // ignore
                    }
                }
            }

        } finally {
            try {
                store.close();
            } catch (MessagingException e) {
                // swallow
            }
        }
        return part;
    }

    public DeleteMessagesResponse deleteMessages(final Account account, final List/*<GMMessage>*/ messages) throws SerializableException {

        final Session session = fetchSession(account);
        final Store store = fetchConnectedStore(session);
        final Folder trashFolder = fetchTrashFolder(account, store);

        try {
            // XXX: Don't assume all messages are from the same folder.
            Folder folder = null;

            final Iterator<GMMessage> iter = ((List<GMMessage>)messages).iterator();
            while (iter.hasNext()) {
                final GMMessage message = iter.next();
                final GMFolder gmFolder = message.getFolder();
                if (gmFolder == null) {
                    throw new SerializableException("GMMessage doesn't have a folder set: " + message);
                }

                try {
                    if (folder == null) {
                        try {
                            folder = store.getFolder(gmFolder.getFullName());
                        } catch (MessagingException e) {
                            e.printStackTrace();
                            throw new SerializableException(e.getMessage());
                        }
                    } else {
                        if (!gmFolder.getFullName().equals(folder.getFullName())) {
                            throw new SerializableException("Folder not the same for all messages.!");
                        }
                    }

                    folder.open(Folder.READ_WRITE);

                    final UIDFolder uidFolder = (UIDFolder)folder;

                    // Check that the UID Validity hasn't changed
                    final long uidValidity = uidFolder.getUIDValidity();
                    if (gmFolder.getUidValidity() != uidValidity) {
                        throw new UidValidityChangedException(folder.getFullName(), gmFolder.getUidValidity(), uidValidity);
                    }

                    final Message messageToDelete = uidFolder.getMessageByUID(message.getUid());
                    folder.copyMessages(new Message[] {messageToDelete}, trashFolder);

                    messageToDelete.setFlag(Flags.Flag.DELETED, true);

                } catch (MessagingException e) {
                    throw new SerializableException("Problem deleteing message: " + message + ", reason: " + e.getMessage());

                } finally {
                    if (folder != null) {
                        try {
                            folder.close(true);
                        } catch (MessagingException e) {
                            // ignore
                        }
                    }
                }
            }

        } finally {
            try {
                store.close();
            } catch (MessagingException e) {
                // swallow
            }
        }
        return null;
    }

    public DeleteMessagesResponse deleteMessagesForever(final Account account, final List/*<GMMessage>*/ messages) throws SerializableException {
        final Session session = fetchSession(account);
        final Store store = fetchConnectedStore(session);

        try {
            // XXX: Don't assume all messages are from the same folder.
            Folder folder = null;

            final Iterator<GMMessage> iter = ((List<GMMessage>)messages).iterator();
            while (iter.hasNext()) {
                final GMMessage message = iter.next();
                final GMFolder gmFolder = message.getFolder();
                if (gmFolder == null) {
                    throw new SerializableException("GMMessage doesn't have a folder set: " + message);
                }

                try {
                    if (folder == null) {
                        try {
                            folder = store.getFolder(gmFolder.getFullName());
                        } catch (MessagingException e) {
                            e.printStackTrace();
                            throw new SerializableException(e.getMessage());
                        }
                    } else {
                        if (!gmFolder.getFullName().equals(folder.getFullName())) {
                            throw new SerializableException("Folder not the same for all messages.!");
                        }
                    }

                    folder.open(Folder.READ_WRITE);

                    final UIDFolder uidFolder = (UIDFolder)folder;

                    // Check that the UID Validity hasn't changed
                    final long uidValidity = uidFolder.getUIDValidity();
                    if (gmFolder.getUidValidity() != uidValidity) {
                        throw new UidValidityChangedException(folder.getFullName(), gmFolder.getUidValidity(), uidValidity);
                    }

                    final Message messageToDelete = uidFolder.getMessageByUID(message.getUid());
                    messageToDelete.setFlag(Flags.Flag.DELETED, true);

                } catch (MessagingException e) {
                    throw new SerializableException("Problem deleteing message: " + message + ", reason: " + e.getMessage());

                } finally {
                    if (folder != null) {
                        try {
                            folder.close(true);
                        } catch (MessagingException e) {
                            // ignore
                        }
                    }
                }
            }

        } finally {
            try {
                store.close();
            } catch (MessagingException e) {
                // swallow
            }
        }
        return null;
    }

    public static Session fetchSession(final Account account) throws SerializableException {
        if (!(account instanceof GatorLinkAccount)) {
            throw new LoginService.LoginException("Unexpected account type: " + (account != null ? account.getClass() : null));
        }

        final Properties props = new Properties(System.getProperties());

        props.setProperty("mail.store.protocol", "imap"); // TODO: switch to imaps for production
        props.setProperty("mail.imap.host", "imap.ufl.edu");
        props.setProperty("mail.imap.port", "143");

        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "smtp.ufl.edu");
        props.setProperty("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.getUsername(), account.getPassword());
            }
        });
    }

    public static Store fetchConnectedStore(final Session session) throws LoginService.LoginException {
        final Store store;
        try {
            store = session.getStore();
        } catch (NoSuchProviderException e) {
            throw new LoginService.LoginException(e.getMessage(), e);
        } catch (Exception e) {
            throw new LoginService.LoginException(e.getMessage(), e);
        }

        try {
            store.connect();
        } catch (MessagingException e) {
            throw new LoginService.LoginException(e.getMessage(), e);
        } catch (Exception e) {
            throw new LoginService.LoginException(e.getMessage(), e);
        }
        return store;
    }

    private Folder fetchTrashFolder(final Account account, final Store store) throws SerializableException {
        final Folder trashFolder;
        try {
            trashFolder = store.getFolder(account.getTrashFolderName());
        } catch (MessagingException e) {
            throw new SerializableException("Trash folder not found: " + e.getMessage());
        }
        return trashFolder;
    }

    private static GMMessageHeaders convertHeaders(final Message message) throws SerializableException {
        final GMMessageHeaders gmMessageHeaders = new GMMessageHeaders();
        try {
            gmMessageHeaders.setSubject(message.getSubject());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        try {
            gmMessageHeaders.setReceivedDate(message.getReceivedDate());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        try {
            gmMessageHeaders.setSentDate(message.getSentDate());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            gmMessageHeaders.setFrom(convertAddresses(message.getFrom()));
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            gmMessageHeaders.setTo(convertAddresses(message.getRecipients(Message.RecipientType.TO)));
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            gmMessageHeaders.setCc(convertAddresses(message.getRecipients(Message.RecipientType.CC)));
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            gmMessageHeaders.setBcc(convertAddresses(message.getRecipients(Message.RecipientType.BCC)));
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        return gmMessageHeaders;
    }

    private static GMAddress[] convertAddresses(final Address[] addresses) {
        if (addresses != null) {
            final GMAddress[] from = new GMAddress[addresses.length];
            for (int i=0; i < addresses.length; i++) {
                final Address address = addresses[i];
                if (address instanceof InternetAddress) {
                    final InternetAddress internetAddress = (InternetAddress)address;
                    from[i] = new GMInternetAddress(internetAddress.getPersonal(), internetAddress.getAddress());
                } else if (address instanceof NewsAddress) {
                    final NewsAddress newsAddress = (NewsAddress)address;
                    from[i] = new GMNewsAddress(newsAddress.getNewsgroup(), newsAddress.getHost());
                } else {
                    from[i] = new GMAddress(address.toString());
                }
            }
            return from;
        }
        return null;
    }

    private static final GMMessageSummary EMPTY_SUMMARY = new GMMessageSummary();

    private static GMMessageSummary createSummary(final Message message) {
        String text = null;
        try {
            text = findFirstPlainTextPart(message.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        if (text != null) {
            text = stripQuotes(text);
        }
        if (text != null) {
            final GMMessageSummary gmSummary = new GMMessageSummary();

            gmSummary.setSample(stripLineFeeds(text));

            final ISummariser summariser = new SimpleSummariser();
            final String summary = summariser.summarise(text, 1);

            String oneLiner = stripLineFeeds(summary);
            if (oneLiner.length() > 250) {
                oneLiner = oneLiner.substring(0, 250);
            }
            gmSummary.setOneLiner(oneLiner);

            return gmSummary;
        } else {
            return EMPTY_SUMMARY;
        }
    }

    private static String stripLineFeeds(final String text) {
        final StringReader sr = new StringReader(text);
        final BufferedReader br = new BufferedReader(sr);
        final StringBuffer sb = new StringBuffer();
        String line;

        try {
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    sb.append(line).append(" ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static String stripQuotes(final String text) {
        final StringReader sr = new StringReader(text);
        final BufferedReader br = new BufferedReader(sr);
        final StringBuffer sb = new StringBuffer();
        String line;

        try {
            while ((line = br.readLine()) != null) {
                if ("--".equals(line)) {
                    // cut line, should be the end of message body
                    break;
                }
                if (!line.startsWith(">") && !line.endsWith(" wrote:")) {
                    sb.append(line).append("\n");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private static String findFirstPlainTextPart(final Object obj) {
        if (obj instanceof String) {
            return (String)obj;
        } else if (obj instanceof MimeMultipart) {
            final MimeMultipart mimeMultipart = (MimeMultipart)obj;
            try {
                final int parts = mimeMultipart.getCount();
                for (int i=0; i < parts; i++) {
                    final String s = findFirstPlainTextPart(mimeMultipart.getBodyPart(i));
                    if (s != null) {
                        return s;
                    }
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        } else if (obj instanceof MimeBodyPart) {
            final MimeBodyPart mimeBodyPart = (MimeBodyPart)obj;
            try {
                if (mimeBodyPart.getContentType().startsWith("text/plain") || mimeBodyPart.getContentType().startsWith("multipart/")) {
                    return findFirstPlainTextPart(mimeBodyPart.getContent());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        } else if (obj != null) {
            System.err.println(obj.getClass());
        }
        return null;
    }

    private static GMMessagePart convertMessageParts(final Part part) throws MessagingException, IOException {
        // TODO: image/*
        // TODO: audio/*
        if (part.isMimeType("multipart/mixed")) {
            final MimeMultipart mimeMultipart = (MimeMultipart)part.getContent();

            final GMMixed mixed = new GMMixed();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                final Part p = mimeMultipart.getBodyPart(i);
                mixed.addPart(convertMessageParts(p));
            }
            return mixed;

        } else if (part.isMimeType("multipart/alternative")) {
            final MimeMultipart mimeMultipart = (MimeMultipart)part.getContent();

            final GMAlternative alternative = new GMAlternative();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                final Part p = mimeMultipart.getBodyPart(i);
                alternative.addPart(convertMessageParts(p));
            }
            return alternative;

        } else if (part.isMimeType("multipart/digest")) {
            final MimeMultipart mimeMultipart = (MimeMultipart)part.getContent();

            final GMDigest digest = new GMDigest();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                final Part p = mimeMultipart.getBodyPart(i);
                digest.addPart(convertMessageParts(p));
            }
            return digest;

        } else if (part.isMimeType("multipart/parallel")) {
            final MimeMultipart mimeMultipart = (MimeMultipart)part.getContent();

            final GMParallel parallel = new GMParallel();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                final Part p = mimeMultipart.getBodyPart(i);
                parallel.addPart(convertMessageParts(p));
            }
            return parallel;

        } else if (part.isMimeType("multipart/related")) {
            System.err.println("multipart/related isn't fully supported yet!");
            final MimeMultipart mimeMultipart = (MimeMultipart)part.getContent();

            final GMRelated related = new GMRelated();
            // TODO: Set: type, start, start-info
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                final Part p = mimeMultipart.getBodyPart(i);
                related.addPart(convertMessageParts(p));
            }
            return related;

        } else if (part.isMimeType("multipart/*")) {
            System.err.println("Unexpected multipart/* mime type: " + part.getContentType());
            final MimeMultipart mimeMultipart = (MimeMultipart)part.getContent();
            final GMMixed mixed = new GMMixed();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                final Part p = mimeMultipart.getBodyPart(i);
                mixed.addPart(convertMessageParts(p));
            }
            return mixed;

        /* TODO: text/richtext
        } else if (part.isMimeType("text/richtext")) {
            return new GMRichText(part.getContent());
        */
        } else if (part.isMimeType("text/html")) {
            return new GMHtml((String)part.getContent());

        } else if (part.isMimeType("text/plain")) {
            return new GMPlain((String)part.getContent());

        } else if (part.isMimeType("text/*")) {
            System.err.println("Unexpected text/* mime type: " + part.getContentType());
            final Object o = part.getContent();
            if (o instanceof String) {
                final String s = (String)o;
                return new GMPlain(s);
            } else if (o instanceof InputStream) {
                final InputStream inputStream = (InputStream)o;
                final StringWriter sw = new StringWriter(inputStream.available());
                int b;
                while ((b = inputStream.read()) >= 0) {
                    sw.write(b);
                }
                return new GMPlain(sw.toString());
            } else {
                System.err.println("How do I really convert: " + (o != null ? o.getClass() : null) + " to a String?");
                return new GMPlain("" + o);
            }

        } else {
            System.err.println("Unexpected mime type: " + part.getContentType());
            return null;
        }
    }

    /*
    public static void main(String[] args) {
        String text = "I really, really, no really, appreciate it Sandy.\n" +
                "\n" +
                "I actually have on my to do list to cover replace-with, but your\n" +
                "insight on that, and everything else you noted it very helpful.  All\n" +
                "of it will get some attention in the book.\n" +
                "\n" +
                "Also, just FYI in regards to your \"style\" comments, we are going even\n" +
                "farther in the next round of revisions to get out of the \"basic\"\n" +
                "feel.Chapters 1-4, we ended up feeling were a bit wordy, and almost\n" +
                "too basic, so we cut a bunch of that out and changed the early on\n" +
                "focus to match more of later parts of the book.  (And we are using\n" +
                "GWTx in, I think 4 places now, whereas early copies we used it once.)\n" +
                "\n" +
                "I know you are busy as heck, but again much appreciated.  I will get\n" +
                "you on the MEAP list and send you a copy when it hits print.";
        final ISummariser summariser = new SimpleSummariser();
        final String summary = summariser.summarise(text, 1);
        System.out.println("In: ");
        System.out.println(text);
        System.out.println("-----");
        System.out.println("Out:");
        System.out.println(summary);
        System.out.println("-----");
    }
    */
}