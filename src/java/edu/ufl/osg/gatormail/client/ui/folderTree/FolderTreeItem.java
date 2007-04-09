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

import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.core.client.GWT;

import java.util.Iterator;
import java.util.List;

import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.services.FoldersServiceAsync;
import edu.ufl.osg.gatormail.client.services.FoldersService;
import edu.ufl.osg.gatormail.client.GatorMailWidget;

/**
 * // TODO: Write Class JavaDoc
*
* @author Sandy McArthur
* @since Sep 28, 2006 3:13:14 PM
*/
public class FolderTreeItem extends TreeItem {

    private final GatorMailWidget client;
    private GMFolder gmFolder;
    private FetchInfoCommand command;

    public FolderTreeItem(final GatorMailWidget client, final String name) {
        this(client, name, name);
    }

    public FolderTreeItem(final GatorMailWidget client, final String name, final String fullName) {
        super(name);
        this.client = client;
        command = new FetchInfoCommand(fullName);
        DeferredCommand.add(command);
    }


    public GMFolder getFolder() {
        return gmFolder;
    }

    public String getFolderName() {
        return gmFolder.getName();
    }

    public String getFolderFullName() {
        return gmFolder.getFullName();
    }

    private class FetchInfoCommand implements Command {
        private final String name;

        public FetchInfoCommand(final String name) {
            this.name = name;
        }

        public void execute() {
            final FoldersServiceAsync service = FoldersService.App.getInstance();
            service.getFolderInfo(client.getAccount(), name, new AsyncCallback() {
                public void onSuccess(final Object result) {
                    gmFolder = (GMFolder)result;
                    setText(gmFolder.getName());
                    setTitle(gmFolder.getFullName());

                    final List subFolders = gmFolder.getSubFolders();
                    final Iterator iter = subFolders.iterator();
                    while (iter.hasNext()) {
                        final String fullName = (String)iter.next();
                        final String name = gmFolder.fullNameToName(fullName);
                        addItem(new FolderTreeItem(client, name, fullName));
                    }

                }
                public void onFailure(final Throwable caught) {
                    GWT.log("Error fetching info of " + name, caught);
                }
            });
        }
    }
}
