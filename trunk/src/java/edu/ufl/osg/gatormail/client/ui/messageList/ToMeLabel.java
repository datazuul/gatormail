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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMAddress;
import edu.ufl.osg.gatormail.client.GatorMailWidget;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * A label that indicates how much the message is directed at the account holder.
 *
 * @author Sandy McArthur
 */
public class ToMeLabel extends Composite {

    private static final String RIGHT_ANGLE_QUOTE = "&raquo;";
    private static final String RIGHT_SINGLE_ANGLE_QUOTE = "&rsaquo;";
    private static final String TILDE = "&sim;";
    private static final String NBSP = "&nbsp;";

    private final HTML html = new HTML();

    private final GatorMailWidget client;
    private final GMMessage message;

    private final PropertyChangeListener clientChangeListener = new ClientPropertyChangeListener();
    private final PropertyChangeListener messageChangeListener = new MessagePropertyChangeListener();

    public ToMeLabel(final GatorMailWidget client, final GMMessage message) {
        assert client != null;
        assert message != null;
        this.client = client;
        this.message = message;

        initWidget(html);
        addStyleName("gm-ToMeLabel");
    }

    protected void onAttach() {
        super.onAttach();

        client.addPropertyChangeListener(clientChangeListener);
        message.addPropertyChangeListener("headers", messageChangeListener);

        updateLabel();
    }

    protected void onDetach() {
        super.onDetach();

        client.removePropertyChangeListener(clientChangeListener);
        message.removePropertyChangeListener("headers", messageChangeListener);
    }

    private void updateLabel() {
        final Account account = client.getAccount();
        final GMMessageHeaders headers = message.getHeaders();
        if (account != null && headers != null) {
            final GMAddress[] to = headers.getTo();
            if (to != null) {
                for (int i=0; i < to.length; i++) {
                    if (client.isMe(to[i])) {
                        html.setHTML(RIGHT_ANGLE_QUOTE);
                        html.setTitle("To: " + to[i]);
                        return;
                    }
                }
            }

            final GMAddress[] cc = headers.getCc();
            if (cc != null) {
                for (int i=0; i < cc.length; i++) {
                    if (client.isMe(cc[i])) {
                        html.setHTML(RIGHT_SINGLE_ANGLE_QUOTE);
                        html.setTitle("CC: " + cc[i]);
                        return;
                    }
                }
            }

            final GMAddress[] bcc = headers.getBcc();
            if (bcc != null) {
                for (int i=0; i < bcc.length; i++) {
                    if (client.isMe(bcc[i])) {
                        html.setHTML(TILDE);
                        html.setTitle("BCC: " + bcc[i]);
                        return;
                    }
                }
            }

        } else {
            html.setHTML(NBSP);
            // TODO: use null version when GWT is fixed.
            //html.setTitle(null);
            html.setTitle("");
        }
    }

    private class ClientPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName() == null || "account".equals(evt.getPropertyName())) {
                updateLabel();
            }
        }
    }
    private class MessagePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName() == null || "headers".equals(evt.getPropertyName())) {
                updateLabel();
            }
        }
    }

}
