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
 * Client mode bean for a JavaMail Address.
 *
 * @author Sandy McArthur
 */
public class GMAddress implements IsSerializable {
    private String string;

    public GMAddress() {
    }

    public GMAddress(final String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(final String string) {
        this.string = string;
    }

    public String getType() {
        return "unknown";
    }

    public String toString() {
        return string;
    }
}
