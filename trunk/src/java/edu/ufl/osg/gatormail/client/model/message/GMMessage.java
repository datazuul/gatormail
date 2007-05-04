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
import edu.ufl.osg.gatormail.client.model.GMFlags;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import org.mcarthur.sandy.gwt.event.property.client.NamedPropertyChangeSource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * GatorMail Message bean.
 *
 * @author Sandy McArthur
 */
public class GMMessage implements IsSerializable, NamedPropertyChangeSource {

    private transient PropertyChangeSupport pcs;

    private GMFolder folder;
    private GMMessageHeaders headers;
    private GMFlags flags;
    private GMMessageSummary summary;

    /**
     * This is too heavy weight to send of RPC willy nilly.
     */
    private transient GMPart part;

    /**
     * This is really a long but it is stored as a String to preserve the full accuracy.
     */
    private /*long*/ String uid /* = -1*/;

    public GMMessage() {
    }

    public GMFolder getFolder() {
        return folder;
    }

    public void setFolder(final GMFolder gmFolder) {
        final Object old = this.folder;
        this.folder = gmFolder;
        firePropertyChange("folder", old, gmFolder);
    }


    /**
     * If greator than zero, then the Message UID.
     *
     * @return the message UID if greator than zero.
     */
    public long getUid() {
        return uid != null ? Long.parseLong(uid) : -1;
    }

    public void setUid(final long uid) {
        assert this.uid == null : "UID cannot be changed old: " + getUid() + ", new: " + uid;
        final Object old = new Long(getUid());
        this.uid = Long.toString(uid);
        firePropertyChange("uid", old, new Long(uid));
    }

    public String getUidAsString() {
        return uid != null ? uid : "-1";
    }

    public GMFlags getFlags() {
        return flags;
    }

    public void setFlags(final GMFlags flags) {
        final Object old = this.flags;
        this.flags = flags;
        // if we ever allow headers to be reset, then we need to remove the listener.
        //headers.addPropertyChangeListener(new HeadersPropertyChangeListener());
        firePropertyChange("flags", old, flags);
    }

    public GMMessageHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(final GMMessageHeaders headers) {
        if (getHeaders() != null) {
            throw new IllegalStateException("Headers must not be set twice.");
        }
        final Object old = getHeaders();
        this.headers = headers;
        // if we ever allow headers to be reset, then we need to remove the listener.
        headers.addPropertyChangeListener(new HeadersPropertyChangeListener());
        firePropertyChange("headers", old, headers);
    }


    public GMMessageSummary getSummary() {
        return summary;
    }

    public void setSummary(final GMMessageSummary summary) {
        if (getSummary() != null) {
            throw new IllegalStateException("Summary must not be set twice.");
        }
        final Object old = getSummary();
        this.summary = summary;
        // if we ever allow summary to be reset, then we need to remove the listener.
        summary.addPropertyChangeListener(new HeadersPropertyChangeListener());
        firePropertyChange("summary", old, summary);
    }


    public GMPart getBody() {
        return part;
    }

    public void setBody(final GMPart part) {
        if (getBody() != null) {
            throw new IllegalStateException("Content must not be set twice.");
        }
        final Object old = getBody();
        this.part = part;
        // if we ever allow summary to be reset, then we need to remove the listener.
        part.addPropertyChangeListener(new HeadersPropertyChangeListener());
        firePropertyChange("body", old, part);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        if (pcs != null) {
            pcs.removePropertyChangeListener(listener);
        }
    }

    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        if (pcs != null) {
            pcs.removePropertyChangeListener(propertyName, listener);
        }
    }

    /**
     * Return true if there are any PropertyChangeListeners attached to this message.
     *
     * @return <code>true</code> if there are any PropertyChangeListeners attached to this message.
     */
    public boolean hasPropertyChangeListeners() {
        return pcs != null && pcs.getPropertyChangeListeners().length > 0;
    }

    private void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        if (pcs != null) {
            pcs.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    private class HeadersPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent event) {
            firePropertyChange("headers", null, headers);
        }
    }
}
