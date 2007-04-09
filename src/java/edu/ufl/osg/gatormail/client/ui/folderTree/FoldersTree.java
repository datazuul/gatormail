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

package edu.ufl.osg.gatormail.client.ui.folderTree;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.core.client.GWT;
import edu.ufl.osg.gatormail.client.services.FoldersServiceAsync;
import edu.ufl.osg.gatormail.client.services.FoldersService;
import edu.ufl.osg.gatormail.client.GatorMailWidget;

/**
 * Widget representing the folderTree in a mailbox.
 *
 * @author Sandy McArthur
 * @since Sep 28, 2006 12:08:32 PM
 */
public class FoldersTree extends Tree {
    private final GatorMailWidget client;

    public FoldersTree(final GatorMailWidget client) {
        this.client = client;
        DeferredCommand.add(new FetchRootFoldersCommand());
    }

    private class FetchRootFoldersCommand implements Command {
        public void execute() {
            final FoldersServiceAsync service = FoldersService.App.getInstance();
            service.getRootFolders(client.getAccount(), new AsyncCallback() {
                public void onSuccess(final Object result) {
                    final String[] rootFolders = (String[])result;
                    for (int i=0; i < rootFolders.length; i++) {
                        final FolderTreeItem item = new FolderTreeItem(client, rootFolders[i]);
                        addItem(item);
                    }
                }
                public void onFailure(final Throwable caught) {
                    GWT.log("Error fetching Root Folders ", caught);
                }
            });
        }
    }
}