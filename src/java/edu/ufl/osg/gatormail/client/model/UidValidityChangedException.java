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

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * Thrown when the UID Validity of the folder on hthe server has changed.
 *
 * @author Sandy McArthur
 */
public class UidValidityChangedException extends SerializableException {

    private String folderFullName;
    private String expectedUidValidity;
    private String currentUidValidity;

    public UidValidityChangedException() {
        folderFullName = expectedUidValidity = currentUidValidity = null;
    }

    public UidValidityChangedException(final String folderFullName, final long expected, final long current) {
        this.folderFullName = folderFullName;
        this.expectedUidValidity = Long.toString(expected);
        this.currentUidValidity = Long.toString(current);
    }

    public String getMessage() {
        return "UIDValidity mismatch. Expected " + expectedUidValidity + " Found: " + currentUidValidity + " for " + folderFullName;
    }

    public long getCurrentUidValidity() {
        return Long.parseLong(currentUidValidity);
    }

    public long getExpectedUidValidity() {
        return Long.parseLong(expectedUidValidity);
    }

    public String getFolderFullName() {
        return folderFullName;
    }
}
