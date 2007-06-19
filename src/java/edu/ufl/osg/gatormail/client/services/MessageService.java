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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RPC service methods to fetch information about messages.
 *
 * @author Sandy McArthur
 */
public interface MessageService extends RemoteService {

    public GMMessageHeaders fetchHeaders(Account account, GMMessage message) throws SerializableException;
    public GMMessageSummary fetchSummary(Account account, GMMessage message) throws SerializableException;
    public GMPart fetchMessageBody(Account account, GMMessage message) throws SerializableException;

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

        public static synchronized MessageServiceAsync getInstance() {
            if (ourInstance == null) {
                ourInstance = (MessageServiceAsync)GWT.create(MessageService.class);
                ((ServiceDefTarget)ourInstance).setServiceEntryPoint(GWT.getModuleBaseURL() + "message");
            }
            return ourInstance;
        }
    }
}
