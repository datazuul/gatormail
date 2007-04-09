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

package edu.ufl.osg.gatormail.client.model.message;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.mcarthur.sandy.gwt.event.property.client.PropertyChangeSource;
import edu.ufl.osg.gatormail.client.model.GMAddress;

public final class GMMessageHeaders implements IsSerializable, PropertyChangeSource {

    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private String subject;
    private Date receivedDate;
    private Date sentDate;

    private GMAddress[] from;
    private GMAddress[] to;
    private GMAddress[] cc;
    private GMAddress[] bcc;

    public GMMessageHeaders() {
    }

    public GMAddress[] getFrom() {
        return from;
    }

    public void setFrom(final GMAddress[] from) {
        final Object old = this.from;
        this.from = from;
        pcs.firePropertyChange("from", old, from);
    }


    public GMAddress[] getTo() {
        return to;
    }

    public void setTo(final GMAddress[] to) {
        final Object old = this.to;
        this.to = to;
        pcs.firePropertyChange("to", old, to);
    }

    public GMAddress[] getCc() {
        return cc;
    }

    public void setCc(final GMAddress[] cc) {
        final Object old = this.cc;
        this.cc = cc;
        pcs.firePropertyChange("cc", old, cc);
    }

    public GMAddress[] getBcc() {
        return bcc;
    }

    public void setBcc(final GMAddress[] bcc) {
        final Object old = this.bcc;
        this.bcc = bcc;
        pcs.firePropertyChange("bcc", old, bcc);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        final Object old = this.subject;
        this.subject = subject;
        pcs.firePropertyChange("subject", old, subject);
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(final Date receivedDate) {
        final Object old = this.receivedDate;
        this.receivedDate = receivedDate;
        pcs.firePropertyChange("receivedDate", old, receivedDate);
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(final Date sentDate) {
        final Object old = this.sentDate;
        this.sentDate = sentDate;
        pcs.firePropertyChange("sentDate", old, sentDate);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
