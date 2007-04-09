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
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.ui.FromAddressesLabel;
import edu.ufl.osg.gatormail.client.ui.ToAddressesLabel;
import edu.ufl.osg.gatormail.client.ui.CcAddressesLabel;
import edu.ufl.osg.gatormail.client.ui.BccAddressesLabel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Shows the headers of a message.
 *
 * @author Sandy McArthur
 */
public class MessageViewHeadersPanel extends Composite {

    private final VerticalPanel vp = new VerticalPanel();
    private final PropertyChangeListener listener = new MessageHeadersPropertyChangeListener();

    private final GatorMailWidget client;
    private final GMMessage message;


    public MessageViewHeadersPanel(final GatorMailWidget client, final GMMessage message) {
        this.client = client;
        this.message = message;

        initWidget(vp);
        addStyleName("gm-MessageViewHeadersPanel");
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
        vp.clear();
        final GMMessageHeaders headers = message.getHeaders();
        if (headers != null) {
            if (headers.getFrom() != null) {
                vp.add(new FromAddressesLabel(message));
            }
            if (headers.getTo() != null) {
                vp.add(new ToAddressesLabel(message));
            }
            if (headers.getCc() != null) {
                vp.add(new CcAddressesLabel(message));
            }
            if (headers.getBcc() != null) {
                vp.add(new BccAddressesLabel(message));
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
