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
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.GMFolder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * TreeItem for a GMFolder.
 *
 * @author Sandy McArthur
 */
public class FolderTreeItem extends TreeItem {

    private final GatorMailWidget client;
    private GMFolder folder;
    private final FolderPropertyChangeListener folderPropertyChangeListener = new FolderPropertyChangeListener();

    // TODO: make this take a GMFolder instead of a name
    public FolderTreeItem(final GatorMailWidget client, final GMFolder folder) {
        super(pickName(folder));
        this.client = client;
        this.folder = folder;

        folder.addPropertyChangeListener(folderPropertyChangeListener);

        updateFolder();
    }

    public FolderTreeItem getFolderTreeItemChild(final int index) {
        return (FolderTreeItem)getChild(index);
    }

    public GMFolder getFolder() {
        return folder;
    }

    public String getFolderName() {
        return getFolder().getName();
    }

    public String getFolderFullName() {
        return getFolder().getFullName();
    }

    private void dispose() {
        folder.removePropertyChangeListener(folderPropertyChangeListener);
    }

    private static String pickName(final GMFolder folder) {
        if (folder.getName() != null) {
            return folder.getName();
        } else {
            return folder.getFullName();
        }
    }

    private void updateFolder() {
        setText(pickName(getFolder()));
        setTitle(folder.getFullName());

        final List/*<String>*/ updatedChildren = new ArrayList(folder.getSubFolders());
        final List/*<FolderTreeItem>*/ currentChildItems = new ArrayList/*<FolderTreeItem>*/();
        final List/*<String>*/ currentChildNames = new ArrayList/*<String>*/();
        for (int i=0; i < getChildCount(); i++) {
            final FolderTreeItem item = getFolderTreeItemChild(i);
            currentChildItems.add(item);
            currentChildNames.add(item.getFolderFullName());
        }

        Collections.sort(updatedChildren);

        // TODO: Manage these better
        if (true) {
            Iterator updateIter = updatedChildren.iterator();
            while (updateIter.hasNext()) {
                final String fullName = (String)updateIter.next();
                if (!currentChildNames.contains(fullName)) {
                    final GMFolder subFolder = client.fetchFolder(fullName);
                    addItem(new FolderTreeItem(client, subFolder));
                }
            }
        } else {
            /* Not needed, they will be added in order
            Collections.sort(currentChildItems, new Comparator() {
                public int compare(final Object o1, final Object o2) {
                    final FolderTreeItem f1 = (FolderTreeItem)o1;
                    final FolderTreeItem f2 = (FolderTreeItem)o2;
                    return f1.getFolderFullName().compareTo(f2.getFolderFullName());
                }
            });
            */

            // remove any old folder items
            Iterator currentIter = currentChildItems.iterator();
            while (currentIter.hasNext()) {
                final FolderTreeItem item = (FolderTreeItem)currentIter.next();
                removeItem(item);
                currentIter.remove();
                item.dispose();
            }
        }
    }

    private class FolderPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            updateFolder();
        }
    }
}
