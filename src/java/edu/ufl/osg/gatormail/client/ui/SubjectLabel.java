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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;

/**
 * TODO: Write Class JavaDoc
 *
 * @author Sandy McArthur
 */
public class SubjectLabel extends Composite {
    private final Label label = new Label();
    private final HeadersPropertyChangeListener headersPropertyChangeListener = new HeadersPropertyChangeListener();
    private final GMMessage message;

    public SubjectLabel(final GMMessage message) {
        this.message = message;
        initWidget(label);
        addStyleName("gm-SubjectLabel");
    }

    protected void onAttach() {
        super.onAttach();

        getMessage().addPropertyChangeListener("headers", headersPropertyChangeListener);

        updateSubject();
    }

    protected void onDetach() {
        super.onDetach();

        getMessage().removePropertyChangeListener("headers", headersPropertyChangeListener);
    }

    private void updateSubject() {
        final GMMessage message = getMessage();
        final GMMessageHeaders headers = message.getHeaders();
        label.removeStyleName("gm-SubjectLabel-noSubject");
        if (headers == null) {
            label.setText(null);
            //label.setTitle(null);
        } else if (headers.getSubject() == null || headers.getSubject().length() == 0) {
            label.setText("(No Subject)");
            //label.setTitle(null);
            label.addStyleName("gm-SubjectLabel-noSubject");
        } else {
            label.setText(headers.getSubject());
            //label.setTitle(headers.getSubject());
        }

    }

    protected GMMessage getMessage() {
        return message;
    }

    private class HeadersPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent event) {
            updateSubject();
        }
    }
}
