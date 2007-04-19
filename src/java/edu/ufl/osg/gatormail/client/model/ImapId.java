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

public class ImapId extends MailId {
    private String folder;
    private int uidValidity = -1;
    private int uid = -1;

    public ImapId() {
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(final String folder) {
        this.folder = folder;
    }

    public int getUidValidity() {
        return uidValidity;
    }

    public void setUidValidity(final int uidValidity) {
        this.uidValidity = uidValidity;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(final int uid) {
        this.uid = uid;
    }
}
