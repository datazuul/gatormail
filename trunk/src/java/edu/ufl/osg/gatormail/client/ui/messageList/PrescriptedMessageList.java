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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.model.messageList.Filter;
import edu.ufl.osg.gatormail.client.model.messageList.Order;
import edu.ufl.osg.gatormail.client.model.messageList.Prescript;
import edu.ufl.osg.gatormail.client.services.MessageListService;
import edu.ufl.osg.gatormail.client.services.MessageListServiceAsync;
import edu.ufl.osg.gatormail.client.util.diff.AddDelta;
import edu.ufl.osg.gatormail.client.util.diff.ChangeDelta;
import edu.ufl.osg.gatormail.client.util.diff.Chunk;
import edu.ufl.osg.gatormail.client.util.diff.DeleteDelta;
import edu.ufl.osg.gatormail.client.util.diff.Delta;
import edu.ufl.osg.gatormail.client.util.diff.Diff;
import edu.ufl.osg.gatormail.client.util.diff.Revision;
import org.mcarthur.sandy.gwt.event.list.client.AbstractEventList;
import org.mcarthur.sandy.gwt.event.list.client.EventList;
import org.mcarthur.sandy.gwt.event.list.client.ListEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;

/**
 * Presents an ondered {@link EventList} of {@link edu.ufl.osg.gatormail.client.model.message.GMMessage}
 *
 * @author Sandy McArthur
 */
final class PrescriptedMessageList extends AbstractEventList implements EventList {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final Account account;
    private final GMFolder folder;
    private final MessageCache cache;

    private Prescript prescript = new Prescript(Order.RECEIVED, Filter.ALL);
    private Long[] uids = new Long[0];

    public PrescriptedMessageList(final Account account, final GMFolder folder, final MessageCache cache) {
        this.account = account;
        this.folder = folder;
        this.cache = cache;

        folder.addPropertyChangeListener("uidValidity", new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                refresh();
            }
        });

        pcs.addPropertyChangeListener("prescript", new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                refresh();
            }
        });

        refresh();
    }

    public Prescript getPrescript() {
        return prescript;
    }

    public void setPrescript(final Prescript prescript) {
        if (prescript == null) {
            throw new NullPointerException("prescript cannot be null");
        }
        
        final Object old = getPrescript();
        this.prescript = prescript;
        pcs.firePropertyChange("prescript", old, prescript);
    }

    public void refresh() {

        if (folder.getUidValidity() == -1) {
            // folder not ready
            return;
        }

        final MessageListServiceAsync service = MessageListService.App.getInstance();
        service.fetchMessages(account, folder, getPrescript(), new AsyncCallback() {
            public void onSuccess(final Object result) {
                final MessageListService.MessageListUpdate update = (MessageListService.MessageListUpdate)result;
                update(update.getPrescript(), update.getUids());
            }

            public void onFailure(final Throwable caught) {
                GWT.log("Failed to update UIDs", new RuntimeException(caught));
            }
        });
    }

    private void setUids(final Long[] uids) {
        // TODO: convert this back to a long[] and optimize the Diff code.

        if (uids == null) {
            throw new NullPointerException("uids cannot be null.");
        }

        final Diff diff = new Diff(this.uids);
        final Revision revision = diff.diff(uids);

        this.uids = uids;

        final Iterator deltsIter = revision.iterator();
        if (deltsIter.hasNext()) {
            DeferredCommand.addCommand(new IncrementalCommand() {
                public boolean execute() {
                    final Delta delta = (Delta)deltsIter.next();
                    if (delta instanceof DeleteDelta) {
                        final DeleteDelta deleteDelta = (DeleteDelta)delta;
                        final Chunk original = deleteDelta.getOriginal();
                        final Chunk revised = deleteDelta.getRevised();
                        //GWT.log("D: " + revised.first() + "," + (revised.first() + original.size()), null);
                        fireListEvent(ListEvent.createRemoved(PrescriptedMessageList.this, revised.first(), revised.first() + original.size()));
                    } else if (delta instanceof ChangeDelta) {
                        final ChangeDelta changeDelta = (ChangeDelta)delta;
                        final Chunk original = changeDelta.getOriginal();
                        final Chunk revised = changeDelta.getRevised();
                        if (original.size() == revised.size()) {
                            //GWT.log("C: " + revised.first() + "," + (revised.first() + original.size()), null);
                            fireListEvent(ListEvent.createChanged(PrescriptedMessageList.this, revised.first(), revised.first() + revised.size()));
                        } else {
                            //GWT.log("D: " + revised.first() + "," + (revised.first() + original.size()), null);
                            fireListEvent(ListEvent.createRemoved(PrescriptedMessageList.this, revised.first(), revised.first() + original.size()));
                            //GWT.log("A: " + revised.first() + "," + (revised.first() + revised.size()), null);
                            fireListEvent(ListEvent.createAdded(PrescriptedMessageList.this, revised.first(), revised.first() + revised.size()));
                        }
                    } else if (delta instanceof AddDelta) {
                        final AddDelta addDelta = (AddDelta)delta;
                        final Chunk original = addDelta.getOriginal();
                        final Chunk revised = addDelta.getRevised();
                        //GWT.log("A: " + revised.first() + "," + (revised.first() + revised.size()), null);
                        fireListEvent(ListEvent.createAdded(PrescriptedMessageList.this, revised.first(), revised.first() + revised.size()));
                    }

                    return deltsIter.hasNext();
                }
            });
        }
    }

    /**
     * If prescript matches the current prescript then update with these uids.
     *
     * @param prescript the Prescript these uids represent
     * @param uids a list of message uids.
     */
    private void update(final Prescript prescript, final Long[] uids) {
        if (getPrescript().equals(prescript)){
            setUids(uids);
        }
    }

    // EventList methods
    public Object get(final int index) {
        return cache.getMessage(uids[index].longValue());
    }

    public int size() {
        return uids.length;
    }

    // PropertyChangeSupport methods
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
