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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.FavoritesAccount;
import edu.ufl.osg.gatormail.client.ui.folderTree.FoldersTree;

public class NavPanel extends Composite {
    private final VerticalPanel panel = new VerticalPanel();

    private final GatorMailWidget client;

    private final PrimaryMailboxPanel primaryMailboxPanel;

    private Tree folders;

    public NavPanel(final GatorMailWidget client) {
        this.client = client;

        initWidget(panel);

        addStyleName("gm-NavPanel");


        final Image logo = new Image();
        logo.setTitle("GatorMail 2-alpha #2");
        //logo.setUrl("webMail-logoSmall.jpg");
        logo.setUrl("mail.png");
        panel.add(logo);

        panel.add(new Label("Compose"));
        panel.add(new HTML("&nbsp;"));

        // The big click targets for: INBOX, Drafts, Junk, Sent, Trash
        primaryMailboxPanel = new PrimaryMailboxPanel(client);
        panel.add(primaryMailboxPanel);

        panel.add(new HTML("&nbsp;"));

        if (client.getAccount() instanceof FavoritesAccount) {
            panel.add(new Label("Favorites:"));
            panel.add(new HTML("&nbsp;"));
        }
        
        panel.add(new Label("Account:"));

        folders = new FoldersTree(client);

        panel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        panel.add(folders);

    }
}
