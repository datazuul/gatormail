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

package edu.ufl.osg.gatormail.client.ui.message.text;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import edu.ufl.osg.gatormail.client.model.message.text.GMPlain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * View for {@link edu.ufl.osg.gatormail.client.model.message.text.GMPlain}.
 *
 * @author Sandy McArthur
 */
public class PlainTextView extends Composite {
    // TODO: Provide a way to clamp width.
    private final Label text = new Label();
    private final TextPropertyChangeListener textPropertyChangeListener = new TextPropertyChangeListener();

    private final GMPlain plain;

    public PlainTextView(final GMPlain plain) {
        this.plain = plain;
        initWidget(text);

        addStyleName("gm-PlainTextView");
    }


    protected void onAttach() {
        super.onAttach();

        plain.addPropertyChangeListener(textPropertyChangeListener);

        updateText();
    }

    protected void onDetach() {
        super.onDetach();

        plain.removePropertyChangeListener(textPropertyChangeListener);
    }

    private void updateText() {
        text.setText(plain.getPlain());
    }

    private class TextPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            updateText();
        }
    }
}
