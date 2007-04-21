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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import edu.ufl.osg.gatormail.client.model.GMFolder;

/**
 * Displays the name of a Folder.
 *
 * @author Sandy McArthur
 */
public class MessageListFolderNameLabel extends Composite {
    private final HorizontalPanel hp = new HorizontalPanel();
    private final Label folderName = new Label();

    private final GMFolder folder;


    public MessageListFolderNameLabel(final GMFolder folder) {
        assert folder != null;
        this.folder = folder;

        initWidget(hp);
        addStyleName("gm-MessageListFolderNameLabel");

        folderName.addStyleName("gm-MessageList-FolderName");

        hp.add(folderName);
    }


    protected void onAttach() {
        super.onAttach();

        updateFolderLabel();
    }

    protected void onDetach() {
        super.onDetach();
    }

    private void updateFolderLabel() {
        folderName.setText(folder.getFullName());
    }
}
