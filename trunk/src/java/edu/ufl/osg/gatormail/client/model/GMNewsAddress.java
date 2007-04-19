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
 * Client mode bean for a JavaMail NewsAddress.
 *
 * @author Sandy McArthur
 */
public class GMNewsAddress extends GMAddress implements IsSerializable {
    private String newsgroup;
    private String host;

    public GMNewsAddress() {
    }

    public GMNewsAddress(final String newsgroup) {
        this(newsgroup, null);
    }

    public GMNewsAddress(final String newsgroup, final String host) {
        this.newsgroup = newsgroup;
        this.host = host;
    }

    public String getNewsgroup() {
        return newsgroup;
    }

    public void setNewsgroup(final String newsgroup) {
        this.newsgroup = newsgroup;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getType() {
        return "news";
    }

    public String getString() {
        return toString();
    }

    public void setString(final String string) {
        throw new UnsupportedOperationException("setString not supported by GMNewsAddress");
    }

    public String toString() {
        return getNewsgroup();
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof GMNewsAddress)) return false;
        final GMNewsAddress that = (GMNewsAddress)o;
        return !(host != null ? !host.equals(that.host) : that.host != null) && newsgroup.equals(that.newsgroup);
    }

    public int hashCode() {
        int result;
        result = newsgroup.hashCode();
        result = 31 * result + (host != null ? host.hashCode() : 0);
        return result;
    }
}
