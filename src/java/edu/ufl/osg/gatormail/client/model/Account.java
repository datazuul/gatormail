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

package edu.ufl.osg.gatormail.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * An account for a mail box.
 */
public interface Account {

    /**
     * Account Name.
     *
     * @return the name of the account.
     */
    public String getAccountName();

    /**
     * The username for this account.
     *
     * @return The username for this account.
     */
    public String getUsername();

    /**
     * The password for this account.
     *
     * @return the password for this account.
     */
    public String getPassword();

    /**
     * The domain part of an email address.
     * The part after the '@'.
     *
     * @return The domain part of an email address.
     */
    public String getAccountDomain();

    /**
     * The users email address for this account.
     * This can probably be derived from the {@link #getUsername() username} and
     * {@link #getAccountDomain() accountDomain} properties. Doing this will reduce the size of RPC calls.
     *
     * @return the users email address for this account.
     */
    public String getEmailAddress();

    /**
     * The user's INBOX folder full name.
     *
     * @return The user's INBOX folder full name.
     */
    public String getInboxFolderName();

    /**
     * The user's Drafts folder full name.
     *
     * @return the user's Drafts folder full name.
     */
    public String getDraftsFolderName();

    /**
     * This accounts Junk folder full name.
     *
     * @return This accounts Junk folder full name.
     */
    public String getJunkFolderName();

    /**
     * This accounts Sent folder full name.
     *
     * @return This accounts Sent folder full name.
     */
    public String getSentFolderName();

    /**
     * This accounts Trash folder full name.
     * 
     * @return This accounts Trash folder full name.
     */
    public String getTrashFolderName();
}
