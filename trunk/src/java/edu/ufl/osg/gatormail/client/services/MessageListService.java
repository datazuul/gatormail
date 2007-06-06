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
import edu.ufl.osg.gatormail.client.model.messageList.Prescript;

import java.io.Serializable;
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
    public MessageListUpdate fetchMessages(Account account, GMFolder gmFolder, Prescript prescript) throws SerializableException;

    public long[] fetchMessageUids(Account account, GMFolder GMFolder, MessageOrder order) throws SerializableException;

    public static class MessageListUpdate implements Serializable {
        private Prescript prescript;
        private long[] uids;

        public MessageListUpdate() {
            prescript = null;
            uids = new long[0];
        }

        public MessageListUpdate(final Prescript prescript, final long[] uids) {
            this.prescript = prescript;
            this.uids = uids;
        }

        public Prescript getPrescript() {
            return prescript;
        }

        public long[] getUids() {
            return uids;
        }
    }

    public static class MessageOrder implements IsSerializable {
        public static final MessageOrder RECEIVED = new MessageOrder("RECEIVED");
        public static final MessageOrder SENT = new MessageOrder("SENT");

        private String name;

        public MessageOrder() {
            setName(null);
        }

        public MessageOrder(final String name) {
            setName(name);
        }


        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name != null ? name.toUpperCase() : null;
        }

        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof MessageOrder)) return false;

            final MessageOrder that = (MessageOrder)o;

            return !(getName() != null ? !getName().equals(that.getName()) : that.getName() != null);
        }

        public int hashCode() {
            return (getName() != null ? getName().hashCode() : 0);
        }

        public String toString() {
            return getName();
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
