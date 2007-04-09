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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessagePart;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Base widget to render a message body.
 *
 * @author Sandy McArthur
 */
public class MessageBodyView extends Composite {
    private final SimplePanel sp = new SimplePanel();
    private final MessageBodyPropertyChangeListener listener = new MessageBodyPropertyChangeListener();

    private final GMMessage message;

    public MessageBodyView(final GMMessage message) {
        this.message = message;

        initWidget(sp);
        addStyleName("gm-MessageBodyView");
        setWidth("100%");
    }


    protected void onAttach() {
        super.onAttach();

        message.addPropertyChangeListener("body", listener);

        updateBody();
    }

    protected void onDetach() {
        super.onDetach();

        message.removePropertyChangeListener("body", listener);
    }

    private void updateBody() {
        final GMMessagePart body = message.getBody();
        if (body != null) {
            final Widget w = PartViewFactory.loadView(body);
            sp.setWidget(w);

        } else if (sp.getWidget() != null) {
            sp.clear();
        }
    }

    private class MessageBodyPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            updateBody();
        }
    }
}
