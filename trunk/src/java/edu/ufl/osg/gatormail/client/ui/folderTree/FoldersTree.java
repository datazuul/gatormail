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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.services.FoldersService;
import edu.ufl.osg.gatormail.client.services.FoldersServiceAsync;

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
        addItem(new AccountTreeItem(client, client.getAccount()));

        addTreeListener(new TreeListener() {
            public void onTreeItemSelected(final TreeItem item) {
                if (item instanceof AccountTreeItem) {
                    final AccountTreeItem accountTreeItem = (AccountTreeItem)item;
                    client.openAccount(accountTreeItem.getAccount());

                } else if (item instanceof FolderTreeItem) {
                    final FolderTreeItem folderTreeItem = (FolderTreeItem)item;
                    final GMFolder folder = folderTreeItem.getFolder();
                    if (folder.isHoldsMessages()) {
                        client.openFolder(folder);
                    }

                } else {
                    final ClassCastException cce = new ClassCastException(GWT.getTypeName(item));
                    GWT.log("Unknown TreeItem type: " + GWT.getTypeName(item), cce);
                    throw cce;
                }
            }

            public void onTreeItemStateChanged(final TreeItem item) {
                // TODO: use this to defer some loading.
            }
        });
    }

    private class FetchRootFoldersCommand implements Command {
        public void execute() {
            final FoldersServiceAsync service = FoldersService.App.getInstance();
            service.getRootFolders(client.getAccount(), new AsyncCallback() {
                public void onSuccess(final Object result) {
                    final String[] rootFolders = (String[])result;
                    for (int i=0; i < rootFolders.length; i++) {
                        final GMFolder folder = client.fetchFolder(rootFolders[i]);
                        final FolderTreeItem item = new FolderTreeItem(client, folder);
                        addItem(item);
                    }
                }
                public void onFailure(final Throwable caught) {
                    GWT.log("Error fetching Root Folders ", new RuntimeException(caught));
                }
            });
        }
    }
}