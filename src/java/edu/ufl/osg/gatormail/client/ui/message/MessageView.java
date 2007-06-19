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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.services.MessageService;
import edu.ufl.osg.gatormail.client.services.MessageServiceAsync;
import edu.ufl.osg.gatormail.client.ui.SubjectLabel;

/**
 * A Panel to view a Message.
 *
 * @author Sandy McArthur
 */
public class MessageView extends Composite {
    private final VerticalPanel vp = new VerticalPanel();

    private final GatorMailWidget client;
    private final GMMessage message;

    private final MessageHeadersView headers;
    private final MessageBodyView bodyView;

    public MessageView(final GatorMailWidget client, final GMMessage message) {
        this.client = client;
        this.message = message;

        initWidget(vp);
        addStyleName("gm-MessageView");
        setWidth("100%");

        // Top bar
        final HorizontalPanel headerRow = new HorizontalPanel();
        headerRow.addStyleName("gm-MessageView-HeaderRow");
        headerRow.setWidth("100%");

        final SubjectLabel subjectLabel = new SubjectLabel(message);
        headerRow.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        headerRow.add(subjectLabel);
        headerRow.setCellWidth(subjectLabel, "100%");

        vp.add(headerRow);

        headers = new MessageHeadersView(client, message);
        vp.add(headers);

        bodyView = new MessageBodyView(message);
        vp.add(bodyView);

        final MessageService.MessagePartsSet mps = new MessageService.MessagePartsSet();
        if (message.getHeaders() == null) {
            mps.add(new MessageService.MessagePart("HEADERS"));
        }
        if (message.getBody() == null) {
            mps.add(new MessageService.MessagePart("BODY"));
        }

        final MessageServiceAsync service = MessageService.App.getInstance();
        service.fetchMessageParts(client.getAccount(), message, mps, new AsyncCallback() {
            public void onSuccess(final Object result) {
                final MessageService.MessagePartsUpdate update = (MessageService.MessagePartsUpdate)result;
                update.applyUpdate(message);
            }

            public void onFailure(final Throwable caught) {
                GWT.log("Problem fetching parts: " + mps, caught);
            }
        });
    }

    
}
