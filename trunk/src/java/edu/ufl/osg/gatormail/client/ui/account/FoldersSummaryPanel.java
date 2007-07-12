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

package edu.ufl.osg.gatormail.client.ui.account;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.FavoritesAccount;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.services.FoldersService;
import edu.ufl.osg.gatormail.client.services.FoldersServiceAsync;
import edu.ufl.osg.gatormail.client.ui.FolderFullNameLabel;
import org.mcarthur.sandy.gwt.event.list.client.EventList;
import org.mcarthur.sandy.gwt.event.list.client.EventLists;
import org.mcarthur.sandy.gwt.event.list.client.SortedEventList;
import org.mcarthur.sandy.gwt.table.client.ObjectListTable;
import org.mcarthur.sandy.gwt.table.client.TableBodyGroup;
import org.mcarthur.sandy.gwt.table.client.TableDataCell;
import org.mcarthur.sandy.gwt.table.client.TableFooterGroup;
import org.mcarthur.sandy.gwt.table.client.TableHeaderCell;
import org.mcarthur.sandy.gwt.table.client.TableHeaderGroup;
import org.mcarthur.sandy.gwt.table.client.TableRow;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Table of summary information about an {@link edu.ufl.osg.gatormail.client.model.Account}'s folders.
 *
 * @author Sandy McArthur
 */
public class FoldersSummaryPanel extends Composite {

    private final VerticalPanel vp = new VerticalPanel();

    private final EventList/*<GMFolder>*/ folders = EventLists.eventList();
    private final SortedEventList sel = EventLists.sortedEventList(folders, new FolderComparator());
    final ObjectListTable olt = new ObjectListTable(new FoldersRenderer(), sel);

    private final GatorMailWidget client;
    private final Account account;
    private FoldersSummaryPanel.FoldersChangeListener listener;

    public FoldersSummaryPanel(final GatorMailWidget client, final Account account) {
        this.client = client;
        this.account = account;
        olt.addStyleName("gm-FoldersSummary-FoldersTable");

        initWidget(vp);
        addStyleName("gm-FoldersSummary");

        final Label headerLabel = new Label("Folders:");
        headerLabel.addStyleName("gm-FoldersSummary-Header");
        vp.add(headerLabel);

        vp.add(olt);

        listener = new FoldersChangeListener(client);

        // XXX: This isn't fully real-time :-/
        final FoldersServiceAsync service = FoldersService.App.getInstance();
        service.getRootFolders(account, new AsyncCallback() {
            public void onSuccess(final Object result) {
                final String[] rootFolders = (String[])result;
                for (int i=0; i < rootFolders.length; i++) {
                    final GMFolder folder = client.fetchFolder(rootFolders[i]);
                    if (isAttached()) {
                        folder.addPropertyChangeListener(listener);
                    }
                    folders.add(folder);
                    updateFoldersSubFolders(folder);
                }
            }
            public void onFailure(final Throwable caught) {
                GWT.log("Error fetching Root Folders for " + account, new RuntimeException(caught));
            }
        });
    }

    protected void onAttach() {
        super.onAttach();

        final Iterator iter = folders.iterator();
        while (iter.hasNext()) {
            final GMFolder folder = (GMFolder)iter.next();
            folder.addPropertyChangeListener(listener);
        }

        updateFolderList();
    }

    protected void onDetach() {
        super.onDetach();

        final Iterator iter = folders.iterator();
        while (iter.hasNext()) {
            final GMFolder folder = (GMFolder)iter.next();
            folder.removePropertyChangeListener(listener);
        }
    }

    private void updateFolderList() {
        final List rootFolders = new ArrayList();

        final Iterator iter = folders.iterator();
        while (iter.hasNext()) {
            final GMFolder folder = (GMFolder)iter.next();
            if (folder.getParentFullName() == null) {
                rootFolders.add(folder);
            }
        }

        final Iterator rootIter = rootFolders.iterator();
        while (rootIter.hasNext()) {
            final GMFolder rootFolder = (GMFolder)rootIter.next();
            updateFoldersSubFolders(rootFolder);
        }
    }

    private void updateFoldersSubFolders(final GMFolder folder) {
        final List renderedSubFolders = new ArrayList();
        final Iterator subFolderIter = folders.iterator();
        while (subFolderIter.hasNext()) {
            final GMFolder subFolder = (GMFolder)subFolderIter.next();
            if (folder.getFullName().equals(subFolder.getParentFullName())) {
                renderedSubFolders.add(subFolder);
            }
        }

        // we have a list of the old sub folder

        // update subfolders that still exist and remove the ones that no longer exits
        final List subFolderNames = folder.getSubFoldersNames();
        final Iterator rsfIter = renderedSubFolders.iterator();
        while (rsfIter.hasNext()) {
            final GMFolder renderedSubFolder = (GMFolder)rsfIter.next();
            if (subFolderNames.contains(renderedSubFolder.getFullName())) {
                subFolderNames.remove(renderedSubFolder.getFullName());
                updateFoldersSubFolders(renderedSubFolder);

            } else {
                folders.remove(renderedSubFolder);
                renderedSubFolder.removePropertyChangeListener(listener);
            }
        }

        // subFolderNames is now a list of name sthat needs to be added

        final Iterator sfNIter = subFolderNames.iterator();
        while (sfNIter.hasNext()) {
            final String subFolderFullName = (String)sfNIter.next();
            final GMFolder subFolder = client.fetchFolder(subFolderFullName);
            if (isAttached()) {
                subFolder.addPropertyChangeListener(listener);
            }
            folders.add(subFolder);
            updateFoldersSubFolders(subFolder);
        }
    }

    /**
     * Sorts {@link GMFolder}s aphabetically except "INBOX" is sorted first.
     */
    private static class FolderComparator implements Comparator {
        public int compare(final Object o1, final Object o2) {
            final GMFolder f1 = (GMFolder)o1;
            final GMFolder f2 = (GMFolder)o2;

            final String fn1 = f1.getFullName();
            final String fn2 = f2.getFullName();

            if (fn1.startsWith("INBOX") && fn2.startsWith("INBOX")) {
                return fn1.compareTo(fn2);

            } else if (fn1.startsWith("INBOX")) { // fn2 implicitly does not start with INBOX
                return -1;

            } else if (fn2.startsWith("INBOX")) { // fn1 implicitly does not start with INBOX
                return 1;

            } else {
                return fn1.compareTo(fn2);
            }
        }
    }

    private class FoldersRenderer implements ObjectListTable.Renderer {
        public void render(final Object obj, final TableBodyGroup bodyGroup) {
            final GMFolder folder = (GMFolder)obj;

            final TableRow tr = bodyGroup.newTableRow();

            final TableDataCell fullNameCell = tr.newTableDataCell();
            fullNameCell.add(new FolderFullNameLabel(folder));
            tr.add(fullNameCell);

            if (account instanceof FavoritesAccount) {
                // TODO: Make this update the favorites
                final TableHeaderCell favoriteCell = tr.newTableHeaderCell();
                favoriteCell.add(new CheckBox());
                tr.add(favoriteCell);
            }

            // TODO: Open an edit folder view.
            final TableDataCell editFolderCell = tr.newTableDataCell();
            editFolderCell.add(new Label("[edit]"));
            tr.add(editFolderCell);

            bodyGroup.add(tr);
        }

        public void renderHeader(final TableHeaderGroup headerGroup) {
            final TableRow tr = headerGroup.newTableRow();

            final TableHeaderCell folderNameCell = tr.newTableHeaderCell();
            folderNameCell.add(new Label("Folder Name"));
            tr.add(folderNameCell);

            if (account instanceof FavoritesAccount) {
                final TableHeaderCell favoriteCell = tr.newTableHeaderCell();
                favoriteCell.add(new Label("Favorite"));
                tr.add(favoriteCell);
            }

            final TableHeaderCell editFolderCell = tr.newTableHeaderCell();
            editFolderCell.add(new HTML("&nbsp;"));
            tr.add(editFolderCell);

            headerGroup.add(tr);
        }

        public void renderFooter(final TableFooterGroup footerGroup) {
        }
    }

    private class FoldersChangeListener implements PropertyChangeListener {
        private final GatorMailWidget client;

        public FoldersChangeListener(final GatorMailWidget client) {
            this.client = client;
        }

        public void propertyChange(final PropertyChangeEvent evt) {
            final GMFolder folder = (GMFolder)evt.getSource();

            updateFoldersSubFolders(folder);
            /*
            final List subFolderNames = folder.getSubFoldersNames();
            final Iterator iter = subFolderNames.iterator();
            while (iter.hasNext()) {
                final String fullName = (String)iter.next();
                final GMFolder subFolder = client.fetchFolder(fullName);
                if (!folders.contains(subFolder)) {
                    subFolder.addPropertyChangeListener(this);
                    folders.add(subFolder);
                    updateFoldersSubFolders(subFolder);
                }
            }
            */
        }
    }
}
