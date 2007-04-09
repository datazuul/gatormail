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
import com.google.gwt.user.client.ui.Image;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.GMFlags;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Indicated if a message is flagged.
 *
 * @author Sandy McArthur
 */
public class FlaggedLabel extends Composite {
    private final GMMessage message;

    private final Image image = new Image();

    private final MessagePropertyChangeListener messageChangeListener = new MessagePropertyChangeListener();

    public FlaggedLabel(final GMMessage message) {
        assert message != null;
        this.message = message;

        initWidget(image);
        addStyleName("gm-FlaggedLabel");
    }


    protected void onAttach() {
        super.onAttach();

        getMessage().addPropertyChangeListener(messageChangeListener);

        updateFlag();
    }

    protected void onDetach() {
        super.onDetach();

        getMessage().removePropertyChangeListener(messageChangeListener);
    }

    public GMMessage getMessage() {
        return message;
    }

    private void updateFlag() {
        if (message.getFlags() != null) {
            final GMFlags flags = message.getFlags();
            if (flags.contains(GMFlags.GMFlag.FLAGGED)) {
                image.setUrl("icon_flagged.gif");
            } else {
                image.setUrl("clear.gif");
            }
        }
    }

    private class MessagePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName() == null || "flags".equals(evt.getPropertyName())) {
                updateFlag();
            }
        }
    }

}
