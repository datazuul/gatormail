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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import org.mcarthur.sandy.gwt.event.list.client.RangedEventList;
import org.mcarthur.sandy.gwt.event.list.client.ListEventListener;
import org.mcarthur.sandy.gwt.event.list.client.ListEvent;

/**
 * Prev and Next buttons to control the message list view.
 *
 * @author Sandy McArthur
 */
public final class MessageListPageButtons extends Composite {

    private final HorizontalPanel hp = new HorizontalPanel();
    private final ListEventListener updateListEventListener = new PageUpdateListEventListener();
    private final Button prev = new Button("&lt;prev");
    private final Button next = new Button("next>");

    private final RangedEventList messageList;


    public MessageListPageButtons(final RangedEventList messageList) {
        this.messageList = messageList;

        initWidget(hp);
        addStyleName("gm-MessageListPageButtons");

        prev.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                prevClick();
            }
        });
        next.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                nextClick();
            }
        });

        hp.add(prev);
        hp.add(next);
    }

    protected void onAttach() {
        super.onAttach();

        messageList.addListEventListener(updateListEventListener);

        updateButtons();
    }

    protected void onDetach() {
        super.onDetach();

        messageList.removeListEventListener(updateListEventListener);
    }

    private void updateButtons() {
        final int first = messageList.getStart();
        final int last = messageList.getStart() + messageList.size();
        final int total = messageList.getTotal();

        prev.setEnabled(first != 0);
        next.setEnabled(last < total);
    }

    private void prevClick() {
        final int first = messageList.getStart();

        if (first > 0) {
            messageList.setStart(Math.max(0, first - messageList.getMaxSize()));
        }
    }

    private void nextClick() {
        final int first = messageList.getStart();
        final int last = messageList.getStart() + messageList.size();
        final int total = messageList.getTotal();

        if (last < total) {
            assert first + messageList.getMaxSize() < total;
            messageList.setStart(first + messageList.getMaxSize());
        }
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
                updateButtons();
            }
        }
    }
}
