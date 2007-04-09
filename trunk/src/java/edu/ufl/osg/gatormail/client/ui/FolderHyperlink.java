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

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.GatorMailWidget;

public class FolderHyperlink extends Hyperlink {

    private final GatorMailWidget client;
    private final GMFolder folder;

    public FolderHyperlink(final GatorMailWidget client, final GMFolder folder) {
        this.client = client;
        this.folder = folder;
        addStyleName("gm-FolderHyperlink");
    }

    public FolderHyperlink(final GatorMailWidget client, final GMFolder folder, final String text) {
        this(client, folder);
        setText(text);
        setTargetHistoryToken(folder.getFullName());
        addStyleName("gm-FolderHyperlink-" + text);
        this.addClickListener(new FolderClickListener());
    }


    private static class FolderClickListener implements ClickListener {
        public void onClick(final Widget sender) {
            final FolderHyperlink hyperlink = (FolderHyperlink)sender;
            hyperlink.client.openFolder(hyperlink.folder);                      
        }
    }


}
