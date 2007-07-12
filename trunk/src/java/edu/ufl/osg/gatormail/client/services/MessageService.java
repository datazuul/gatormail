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

package edu.ufl.osg.gatormail.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFlags;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;
import edu.ufl.osg.gatormail.client.model.message.GMMessageSummary;
import edu.ufl.osg.gatormail.client.model.message.GMPart;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RPC service methods to fetch information about messages.
 *
 * @author Sandy McArthur
 */
public interface MessageService extends RemoteService {

    public MessagePartsUpdate fetchMessageParts(Account account, GMMessage message, MessagePartsSet parts) throws SerializableException;

    /**
     * @gwt.typeArgs messages <edu.ufl.osg.gatormail.client.model.message.GMMessage>
     */
    public DeleteMessagesResponse deleteMessages(Account account, List/*<GMMessage>*/ messages) throws SerializableException;

    /**
     * @gwt.typeArgs messages <edu.ufl.osg.gatormail.client.model.message.GMMessage>
     */
    public DeleteMessagesResponse deleteMessagesForever(Account account, List/*<GMMessage>*/ messages) throws SerializableException;

    /**
     * @gwt.typeArgs messages <edu.ufl.osg.gatormail.client.model.message.GMMessage>
     */
    public DeleteMessagesResponse reportSpam(Account account, List/*<GMMessage>*/ messages) throws SerializableException;

    /**
     * @gwt.typeArgs messages <edu.ufl.osg.gatormail.client.model.message.GMMessage>
     */
    public DeleteMessagesResponse reportHam(Account account, List/*<GMMessage>*/ messages) throws SerializableException;

    public static class DeleteMessagesResponse implements Serializable {
        public DeleteMessagesResponse() {
        }
    }

    /**
     * Enum of parts of a message.
     *
     * @author Sandy McArthur
     */
    public class MessagePart implements Serializable {
        public static final MessagePart HEADERS = new MessagePart("HEADERS");
        public static final MessagePart FLAGS = new MessagePart("FLAGS");
        public static final MessagePart SUMMARY = new MessagePart("SUMMARY");
        public static final MessagePart BODY = new MessagePart("BODY");

        private String type;

        public MessagePart() {
            type = null;
        }

        public MessagePart(final String type) {
            this.type = type;
        }

        public boolean equals(final Object o) {
            if (this == o) return true;

            if (o instanceof MessagePart) {
                final MessagePart that = (MessagePart)o;
                return !(type != null ? !type.equals(that.type) : that.type != null);
            }
            return false;
        }

        public int hashCode() {
            return type != null ? type.hashCode() : 0;
        }

        public String toString() {
            return type;
        }
    }

    /**
     * EnumSet of {@link edu.ufl.osg.gatormail.client.services.MessageService.MessagePart}.
     *
     * @author Sandy McArthur
     */
    public class MessagePartsSet implements Serializable {
        public static MessagePartsSet create(final MessageService.MessagePart part) {
            final MessagePartsSet mps = new MessagePartsSet();
            if (part != null) {
                mps.add(part);
            }
            return mps;
        }

        public static MessagePartsSet create(final MessageService.MessagePart part1, final MessageService.MessagePart part2) {
            final MessagePartsSet mps = create(part1);
            if (part2 != null) {
                mps.add(part2);
            }
            return mps;
        }

        public static MessagePartsSet create(final MessageService.MessagePart part1, final MessageService.MessagePart part2, final MessageService.MessagePart part3) {
            final MessagePartsSet mps = create(part1, part2);
            if (part3 != null) {
                mps.add(part3);
            }
            return mps;
        }

        /**
         * @gwt.typeArgs <edu.ufl.osg.gatormail.client.services.MessageService.MessagePart>
         */
        private Set/*<MessagePart>*/ set;

        public MessagePartsSet() {
        }

        public void add(final MessageService.MessagePart part) {
            if (set == null) {
                set = new HashSet/*<MessagePartsSet>*/();
            }
            set.add(part);
        }

        public boolean contains(final MessageService.MessagePart part) {
            return set != null && set.contains(part);
        }

        public boolean isEmpty() {
            return set == null || set.isEmpty();
        }

        public String toString() {
            return "" + set;
        }
    }

    /**
     * Container for sending message parts over RPC.
     *
     * @author Sandy McArthur
     */
    public class MessagePartsUpdate implements Serializable {
        private GMMessageHeaders headers;
        private GMFlags flags;
        private GMMessageSummary summary;
        private GMPart body;

        public MessagePartsUpdate() {
        }

        public GMFlags getFlags() {
            return flags;
        }

        public void setFlags(final GMFlags flags) {
            this.flags = flags;
        }

        public GMMessageHeaders getHeaders() {
            return headers;
        }

        public void setHeaders(final GMMessageHeaders headers) {
            this.headers = headers;
        }

        public GMMessageSummary getSummary() {
            return summary;
        }

        public void setSummary(final GMMessageSummary summary) {
            this.summary = summary;
        }

        public GMPart getBody() {
            return body;
        }

        public void setBody(final GMPart body) {
            this.body = body;
        }

        public void applyUpdate(final GMMessage message) {
            if (getHeaders() != null) {
                try {
                    message.setHeaders(getHeaders());
                } catch (IllegalStateException ise) {
                    GWT.log(ise.getMessage(), ise);
                }
            }
            if (getFlags() != null) {
                try {
                    message.setFlags(getFlags());
                } catch (IllegalStateException ise) {
                    GWT.log(ise.getMessage(), ise);
                }
            }
            if (getSummary() != null) {
                try {
                    message.setSummary(getSummary());
                } catch (IllegalStateException ise) {
                    GWT.log(ise.getMessage(), ise);
                }
            }
            if (getBody() != null) {
                try {
                    message.setBody(getBody());
                } catch (IllegalStateException ise) {
                    GWT.log(ise.getMessage(), ise);
                }
            }
        }
    }
    
    /**
     * Utility/Convinience class.
     * Use MessageService.App.getInstance() to access static instance of MessageServiceAsync
     */
    public static class App {
        private static MessageServiceAsync ourInstance = null;

        /**
         * A Map of Accounts to a Map of Messages to Message Parts.
         */
        private static Map/*<Account, Map<GMMessage, MessagePartsSet>>*/ accountsToMessages = new HashMap/*<Account, Map<GMMessage, MessagePartsSet>>*/();

        private static Command fetcher;

        public static synchronized MessageServiceAsync getInstance() {
            if (ourInstance == null) {
                ourInstance = (MessageServiceAsync)GWT.create(MessageService.class);
                ((ServiceDefTarget)ourInstance).setServiceEntryPoint(GWT.getModuleBaseURL() + "message");
            }
            return ourInstance;
        }

        /**
         * Batch message part fetches.
         *
         * @param account Account associated with the message.
         * @param message GMMessage to fetch parts of.
         * @param part Message part to fetch.
         */
        public static void fetchMessagePart(final Account account, final GMMessage message, final MessagePart part) {
            Map/*<GMMessage, MessagePartsSet>*/ messagesToParts = (Map)accountsToMessages.get(account);
            if (messagesToParts == null) {
                messagesToParts = new HashMap/*<GMMessage, MessagePartsSet>*/();
                accountsToMessages.put(account, messagesToParts);
            }

            MessagePartsSet parts = (MessagePartsSet)messagesToParts.get(message);
            if (parts == null) {
                parts = new MessagePartsSet();
                messagesToParts.put(message, parts);
            }
            parts.add(part);

            if (fetcher == null) {
                fetcher = new FetcherCommand();
                DeferredCommand.addCommand(fetcher);
            }
        }

        private static class FetcherCommand implements Command {
            public void execute() {
                fetcher = null;
                final MessageServiceAsync service = getInstance();
                final Iterator accountIter = accountsToMessages.keySet().iterator();
                while (accountIter.hasNext()) {
                    final Account account = (Account)accountIter.next();
                    final Map/*<GMMessage, MessagePartsSet>*/ messagesToParts = (Map)accountsToMessages.get(account);
                    accountIter.remove();

                    final Iterator messageIter = messagesToParts.keySet().iterator();
                    while (messageIter.hasNext()) {
                        final GMMessage message = (GMMessage)messageIter.next();
                        final MessagePartsSet parts = (MessagePartsSet)messagesToParts.get(message);
                        messageIter.remove();

                        service.fetchMessageParts(account, message, parts, new AsyncCallback() {
                            public void onSuccess(final Object result) {
                                final MessagePartsUpdate update = (MessagePartsUpdate)result;
                                update.applyUpdate(message);
                            }
                            public void onFailure(final Throwable caught) {
                                GWT.log("Problem fetching parts " + parts, new RuntimeException(caught));
                            }
                        });
                    }
                }
            }
        }
    }
}
