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

package edu.ufl.osg.gatormail.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;
import org.mcarthur.sandy.gwt.event.list.client.RangedEventList;
import org.mcarthur.sandy.gwt.event.list.client.ListEventListener;
import org.mcarthur.sandy.gwt.event.list.client.ListEvent;
import edu.ufl.osg.gatormail.client.ui.messageList.MessageList;

/**
 * Displays information about the current message list page.
 *
 * @author Sandy McArthur
 */
public final class MessageListPageLabel extends Composite {

    private final Label label = new PageLabel();
    private final ListEventListener updateListEventListener = new PageUpdateListEventListener();

    private final MessageList messagesList;
    private final RangedEventList messages;

    public MessageListPageLabel(final MessageList messagesList, final RangedEventList messages) {
        this.messagesList = messagesList;
        this.messages = messages;
        initWidget(label);

        addStyleName("gm-MessageListPageLabel");
        label.sinkEvents(Event.ONDBLCLICK);
    }

    protected void onAttach() {
        super.onAttach();

        messages.addListEventListener(updateListEventListener);

        updateLabel();
    }

    protected void onDetach() {
        super.onDetach();

        messages.removeListEventListener(updateListEventListener);
    }

    private void updateLabel() {
        final int size = messages.size();
        final int first; // zero based, add one for
        if (size == 0) {
            first = 0;
        } else {
            first = messages.getStart() + 1;
        }
        final int last = messages.getStart() + size;
        final int total = messages.getTotal();

        if (last < total) {
            label.setText(first + " - " + last + " of " + total);
        } else {
            label.setText(first + " - " + last);
        }
    }

    private void showPageConfig() {
        messagesList.showPageSizeConfig();
    }

    private class PageUpdateListEventListener implements ListEventListener {
        private int batchDepth = 0;

        public void listChanged(final ListEvent listEvent) {
            // This should avoid unnessary updates during batches.
            if (listEvent.isBatchStart()) {
                batchDepth++;
            } else if (listEvent.isBatchEnd()) {
                batchDepth--;
            }

            if (batchDepth == 0) {
                updateLabel();
            }
        }
    }

    private class PageLabel extends Label {
        public void onBrowserEvent(final Event event) {
            super.onBrowserEvent(event);

            final int type = DOM.eventGetType(event);
            switch (type) {
                case Event.ONDBLCLICK:
                    showPageConfig();
                    break;
            }
        }
    }
}
