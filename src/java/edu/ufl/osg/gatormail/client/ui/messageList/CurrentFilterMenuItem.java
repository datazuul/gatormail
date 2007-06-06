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

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * MenuItem that indicates the current selection.
 *
 * @author Sandy McArthur
 */
public class CurrentFilterMenuItem extends MenuItem {
    private final PrescriptPropertyChangeListener prescriptListener = new PrescriptPropertyChangeListener();

    private final PrescriptedMessageList messageList;

    public CurrentFilterMenuItem(final PrescriptedMessageList messageList, final MenuBar subMenu) {
        super("", subMenu);
        this.messageList = messageList;

        messageList.addPropertyChangeListener(prescriptListener);

        updateLabel();
    }

    public void setHTML(final String html) {
        super.setHTML(html + " &darr;"); // doesn't seem to be supported by IE: &or; &#8615; &#8609; &#8642;
    }

    private void updateLabel() {
        setHTML(messageList.getPrescript().getFilter().getDescription());
    }

    private class PrescriptPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            updateLabel();
        }
    }
}
