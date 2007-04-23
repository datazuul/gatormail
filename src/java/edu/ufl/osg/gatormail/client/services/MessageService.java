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
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;
import edu.ufl.osg.gatormail.client.model.message.GMMessageSummary;
import edu.ufl.osg.gatormail.client.model.message.GMPart;

import java.util.List;

/**
 * RPC service methods to fetch information about messages.
 *
 * @author Sandy McArthur
 */
public interface MessageService extends RemoteService {

    public GMMessageHeaders fetchHeaders(Account account, GMMessage message) throws SerializableException;
    public GMMessageSummary fetchSummary(Account account, GMMessage message) throws SerializableException;
    public GMPart fetchMessageBody(Account account, GMMessage message) throws SerializableException;

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

    public static class DeleteMessagesResponse implements IsSerializable {
        public DeleteMessagesResponse() {
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
