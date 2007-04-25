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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.services.FoldersService;
import edu.ufl.osg.gatormail.client.services.FoldersServiceAsync;

/**
 * TreeItem for a {@link edu.ufl.osg.gatormail.client.model.Account}.
 *
 * @author Sandy McArthur
 */
public class AccountTreeItem extends TreeItem {
    private final GatorMailWidget client;
    private final Account account;

    public AccountTreeItem(final GatorMailWidget client, final Account account) {
        super(account.getAccountName());
        this.client = client;
        this.account = account;

        updateAccount();
    }

    private void updateAccount() {
        setText(account.getAccountName());

        final FoldersServiceAsync service = FoldersService.App.getInstance();
        service.getRootFolders(account, new AsyncCallback() {
            public void onSuccess(final Object result) {
                final String[] rootFolders = (String[])result;
                for (int i=0; i < rootFolders.length; i++) {
                    final GMFolder folder = client.fetchFolder(rootFolders[i]);
                    final FolderTreeItem item = new FolderTreeItem(client, folder);
                    addItem(item);
                }
            }
            public void onFailure(final Throwable caught) {
                GWT.log("Error fetching Root Folders for " + account, caught);
            }
        });
    }
}
