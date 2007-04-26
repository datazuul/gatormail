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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.Account;
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
import org.mcarthur.sandy.gwt.table.client.TableHeaderGroup;
import org.mcarthur.sandy.gwt.table.client.TableRow;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

        initWidget(vp);
        addStyleName("gm-FoldersSummary");

        final Label headerLabel = new Label("Folders:");
        headerLabel.addStyleName("gm-FoldersSummary-Header");
        vp.add(headerLabel);

        vp.add(olt);

        listener = new FoldersChangeListener(client);

        if (true) {
            throw new UnsupportedOperationException("Doesn't work yet");
            
        } else {
            // TODO: refactor this so it doesn't leak
            final FoldersServiceAsync service = FoldersService.App.getInstance();
            service.getRootFolders(account, new AsyncCallback() {
                public void onSuccess(final Object result) {
                    final String[] rootFolders = (String[])result;
                    for (int i=0; i < rootFolders.length; i++) {
                        final GMFolder folder = client.fetchFolder(rootFolders[i]);
                        folders.add(folder);
                        folder.addPropertyChangeListener(listener);
                    }
                }
                public void onFailure(final Throwable caught) {
                    GWT.log("Error fetching Root Folders for " + account, caught);
                }
            });
        }
    }

    protected void onAttach() {
        super.onAttach();

        // TODO: populate folder list table and attach PCL
    }

    protected void onDetach() {
        super.onDetach();

        // TODO: clear folder list table and dettach PCL
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

            final TableDataCell td = tr.newTableDataCell();
            td.add(new FolderFullNameLabel(folder));
            tr.add(td);

            bodyGroup.add(tr);
        }

        public void renderHeader(final TableHeaderGroup headerGroup) {
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

            final List subFolderNames = folder.getSubFolders();
            final Iterator iter = subFolderNames.iterator();
            while (iter.hasNext()) {
                final String fullName = (String)iter.next();
                final GMFolder subFolder = client.fetchFolder(fullName);
                if (!folders.contains(subFolder)) {
                    subFolder.addPropertyChangeListener(this);
                    folders.add(subFolder);
                }
            }
        }
    }
}
