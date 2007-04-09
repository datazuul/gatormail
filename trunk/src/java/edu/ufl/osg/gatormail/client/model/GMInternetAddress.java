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
 * TODO: Write Class JavaDoc
 *
 * @author Sandy McArthur
 * @since Sep 28, 2006 4:19:14 PM
 */
public class GMInternetAddress extends GMAddress implements IsSerializable {
    private String address;
    private String personal;

    public GMInternetAddress() {
    }

    public GMInternetAddress(final String address) {
        this(null, address);
    }

    public GMInternetAddress(final String personal, final String address) {
        this.address = address;
        this.personal = personal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(final String personal) {
        this.personal = personal;
    }

    public String getType() {
        return "rfc822";
    }

    public String getString() {
        return toString();
    }

    public void setString(final String string) {
        throw new UnsupportedOperationException("setString not supported by GMInternetAddress");
    }

    public String toString() {
        if (personal == null || personal.length() < 1) {
            return address;
        } else {
            return "\"" + personal + "\" <" + address + ">";
        }
    }


    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof GMInternetAddress)) return false;
        final GMInternetAddress that = (GMInternetAddress)o;
        return address.equalsIgnoreCase(that.address);
    }

    public int hashCode() {
        return address.hashCode();
    }
}
