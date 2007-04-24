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

import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;

import java.util.List;

/**
 * Fetch information about a message.
 *
 * @author Sandy McArthur
 */
public interface MessageServiceAsync {

    void fetchHeaders(Account account, GMMessage message, AsyncCallback async);

    void fetchSummary(Account account, GMMessage message, AsyncCallback async);

    void fetchMessageBody(Account account, GMMessage message, AsyncCallback async);

    void deleteMessages(Account account, List/*<GMMessage>*/ messages, AsyncCallback async);

    void deleteMessagesForever(Account account, List/*<GMMessage>*/ messages, AsyncCallback async);

    void reportSpam(Account account, List/*<GMMessage>*/ messages, AsyncCallback async);
    
    void reportHam(Account account, List/*<GMMessage>*/ messages, AsyncCallback async);
}
