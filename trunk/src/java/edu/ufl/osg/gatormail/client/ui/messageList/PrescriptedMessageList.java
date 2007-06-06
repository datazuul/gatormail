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

package edu.ufl.osg.gatormail.client.ui.messageList;

import edu.ufl.osg.gatormail.client.model.messageList.Filter;
import edu.ufl.osg.gatormail.client.model.messageList.Order;
import edu.ufl.osg.gatormail.client.model.messageList.Prescript;
import org.mcarthur.sandy.gwt.event.list.client.AbstractEventList;
import org.mcarthur.sandy.gwt.event.list.client.EventList;
import org.mcarthur.sandy.gwt.event.list.client.ListEvent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Presents an ondered {@link EventList} of {@link edu.ufl.osg.gatormail.client.model.message.GMMessage}
 *
 * @author Sandy McArthur
 */
final class PrescriptedMessageList extends AbstractEventList implements EventList {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final MessageCache cache;

    private Prescript prescript = new Prescript(Order.RECEIVED, Filter.ALL);
    private long[] uids = new long[0];

    public PrescriptedMessageList(final MessageCache cache) {
        this.cache = cache;
    }

    public Prescript getPrescript() {
        return prescript;
    }

    public void setUids(final Prescript prescript,final long[] uids) {
        final Object old = getPrescript();
        this.prescript = prescript;
        pcs.firePropertyChange("prescript", old, prescript);

        setUids(uids);
    }

    /**
     * @deprecated {@link #setUids(edu.ufl.osg.gatormail.client.model.messageList.Prescript, long[])}
     */
    void setUids(final long[] uids) {
        // TODO: optimize this sommehow
        if (size() > 0) {
            fireListEvent(ListEvent.createRemoved(this, 0, size()));
        }
        this.uids = uids;
        if (size() > 0) {
            fireListEvent(ListEvent.createAdded(this, 0, size()));
        }
    }

    public Object get(final int index) {
        return cache.getMessage(uids[index]);
    }

    public int size() {
        return uids.length;
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
