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
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * RPC service methods to fetch information about the list of messages in a folder.
 *
 * @author Sandy McArthur
 */
public interface MessageListService extends RemoteService {
    /**
     * @return A {@link List} of {@link edu.ufl.osg.gatormail.client.model.message.GMMessage}.
     * @gwt.typeArgs <edu.ufl.osg.gatormail.client.model.message.GMMessage>
     */
    public List/*<GMMessage>*/ fetchMessages(Account account, GMFolder GMFolder) throws SerializableException;

    public long[] fetchMessageUids(Account account, GMFolder GMFolder, MessageOrder order) throws SerializableException;

    public MessageListUpdate fetchMessageListChanges(Account account, GMFolder gmFolder, long startUID, long endUID, int messageCount) throws SerializableException;

    public static class MessageOrder implements IsSerializable {
        public static final MessageOrder RECEIVED = new MessageOrder("RECEIVED");
        public static final MessageOrder SENT = new MessageOrder("SENT");

        private final String name;

        public MessageOrder() {
            name = null;
        }

        public MessageOrder(final String name) {
            this.name = name.toUpperCase();
        }

        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof MessageOrder)) return false;

            final MessageOrder that = (MessageOrder)o;

            return !(name != null ? !name.equals(that.name) : that.name != null);
        }

        public int hashCode() {
            return (name != null ? name.hashCode() : 0);
        }
    }

    public static class MessageListUpdate implements IsSerializable {
        private long requestedStartUID = -1;
        private long requestedEndUID = -1;
        private List/*<GMMessage>*/ beforeStart;
        private long[] validUIDs;
        private List/*<GMMessage>*/ afterEnd;

        public MessageListUpdate() {
        }

        public MessageListUpdate(final long requestedStartUID, final long requestedEndUID) {
            this.requestedStartUID = requestedStartUID;
            this.requestedEndUID = requestedEndUID;
        }

        public long getRequestedEndUID() {
            return requestedEndUID;
        }

        public void setRequestedEndUID(final long requestedEndUID) {
            this.requestedEndUID = requestedEndUID;
        }

        public long getRequestedStartUID() {
            return requestedStartUID;
        }

        public void setRequestedStartUID(final long requestedStartUID) {
            this.requestedStartUID = requestedStartUID;
        }

        public List getBeforeStart() {
            return beforeStart;
        }

        public void setBeforeStart(final List beforeStart) {
            if (GWT.isScript()) {
                this.beforeStart = beforeStart;
            } else {
                this.beforeStart = new ArrayList(beforeStart);
            }
        }

        public long[] getValidUIDs() {
            return validUIDs;
        }

        public void setValidUIDs(final long[] validUIDs) {
            this.validUIDs = validUIDs;
        }

        public List getAfterEnd() {
            return afterEnd;
        }

        public void setAfterEnd(final List afterEnd) {
            if (GWT.isScript()) {
                this.afterEnd = afterEnd;
            } else {
                this.afterEnd = new ArrayList(afterEnd);
            }
        }
    }

    /**
     * Utility/Convinience class.
     * Use MessageListService.App.getInstance() to access static instance of MessageListServiceAsync
     */
    public static class App {
        private static MessageListServiceAsync ourInstance = null;

        public static synchronized MessageListServiceAsync getInstance() {
            if (ourInstance == null) {
                ourInstance = (MessageListServiceAsync)GWT.create(MessageListService.class);
                ((ServiceDefTarget)ourInstance).setServiceEntryPoint(GWT.getModuleBaseURL() + "messageList");
            }
            return ourInstance;
        }
    }
}
