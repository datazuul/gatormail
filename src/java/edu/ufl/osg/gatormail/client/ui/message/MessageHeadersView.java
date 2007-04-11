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

package edu.ufl.osg.gatormail.client.ui.message;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;
import edu.ufl.osg.gatormail.client.ui.BccAddressesLabel;
import edu.ufl.osg.gatormail.client.ui.CcAddressesLabel;
import edu.ufl.osg.gatormail.client.ui.FromAddressesLabel;
import edu.ufl.osg.gatormail.client.ui.ToAddressesLabel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Shows the headers of a message.
 *
 * @author Sandy McArthur
 */
public class MessageHeadersView extends Composite {

    private final Grid grid = new Grid();
    private final PropertyChangeListener listener = new MessageHeadersPropertyChangeListener();

    private final GatorMailWidget client;
    private final GMMessage message;


    public MessageHeadersView(final GatorMailWidget client, final GMMessage message) {
        this.client = client;
        this.message = message;

        initWidget(grid);
        addStyleName("gm-MessageHeadersView");
        setWidth("100%");
    }


    protected void onAttach() {
        super.onAttach();

        message.addPropertyChangeListener("headers", listener);

        updateHeaders();
    }

    protected void onDetach() {
        super.onDetach();

        message.removePropertyChangeListener("headers", listener);
    }

    private void updateHeaders() {
        grid.resizeColumns(2);
        int rows = 0;
        final GMMessageHeaders headers = message.getHeaders();
        if (headers != null) {
            if (headers.getFrom() != null) {
                if (grid.getRowCount() < ++rows) grid.resizeRows(rows);
                grid.setWidget(rows-1, 0, new Label("From:"));
                grid.setWidget(rows-1, 1, new FromAddressesLabel(message));
            }
            if (headers.getTo() != null) {
                if (grid.getRowCount() < ++rows) grid.resizeRows(rows);
                grid.setWidget(rows-1, 0, new Label("To:"));
                grid.setWidget(rows-1, 1, new ToAddressesLabel(message));
            }
            if (headers.getCc() != null) {
                if (grid.getRowCount() < ++rows) grid.resizeRows(rows);
                grid.setWidget(rows-1, 0, new Label("CC:"));
                grid.setWidget(rows-1, 1, new CcAddressesLabel(message));
            }
            if (headers.getBcc() != null) {
                if (grid.getRowCount() < ++rows) grid.resizeRows(rows);
                grid.setWidget(rows-1, 0, new Label("BCC:"));
                grid.setWidget(rows-1, 1, new BccAddressesLabel(message));
            }
            /*
            if (headers.getSentDate() != null) {
                vp.add(new SentDateLabel(message));
            }
            */
        }
    }

    private class MessageHeadersPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            updateHeaders();
        }
    }
}
