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
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.model.Account;

/**
 * Async RPC interface for {@link edu.ufl.osg.gatormail.client.services.MessageListService}.
 *
 * @author Sandy McArthur
 */
public interface MessageListServiceAsync {

    void/*<GMMessage>*/ fetchMessages(Account account, GMFolder GMFolder, AsyncCallback async);

    void fetchMessageListChanges(Account account, GMFolder gmFolder, long startUID, long endUID, int messageCount, AsyncCallback async);
}
