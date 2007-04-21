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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.GMFolder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FolderHyperlink extends Hyperlink {
    private final FolderPropertyChangeListener folderPropertyChangeListener = new FolderPropertyChangeListener();

    private final GatorMailWidget client;
    private final GMFolder folder;
    private final String text;

    public FolderHyperlink(final GatorMailWidget client, final GMFolder folder, final String text) {
        this.client = client;
        this.folder = folder;
        this.text = text;

        addStyleName("gm-FolderHyperlink");
        addStyleName("gm-FolderHyperlink-" + text);

        updateText();
        setTargetHistoryToken(folder.getFullName());

        addClickListener(new FolderClickListener());
    }


    protected void onAttach() {
        super.onAttach();

        folder.addPropertyChangeListener(folderPropertyChangeListener);

        updateText();
    }

    protected void onDetach() {
        super.onDetach();

        folder.removePropertyChangeListener(folderPropertyChangeListener);
    }

    private void updateText() {
        String text = this.text;
        String title = folder.getFullName();

        final int count = folder.getMessageCount();
        final int unreadCount = folder.getUnreadMessageCount();

        if (count == 0) {
            title += ": no  messages";
        } else if (count == 1) {
            title += ": one message";
        } else {
            title += ": " + count + " messages";
        }

        if (unreadCount > 0) {
            text += " (" + unreadCount + ")";
            title += ", " + unreadCount + " unread";
        }

        setText(text);
        setTitle(title);
    }

    private static class FolderClickListener implements ClickListener {
        public void onClick(final Widget sender) {
            final FolderHyperlink hyperlink = (FolderHyperlink)sender;
            hyperlink.client.openFolder(hyperlink.folder);                      
        }
    }

    private class FolderPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            updateText();
        }
    }
}
