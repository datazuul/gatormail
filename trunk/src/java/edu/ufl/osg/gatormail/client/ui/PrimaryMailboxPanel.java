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
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFolder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Nav links for the main folders in a mailbox.
 * Mainly: INBOX, Drafts, Junk, Sent, and Trash.
 *
 * @author sandymac
 */
public class PrimaryMailboxPanel extends Composite {
    private final VerticalPanel panel = new VerticalPanel();
    private final PrimaryMailboxPropertyChangeListener primaryMailboxPropertyChangeListener = new PrimaryMailboxPropertyChangeListener();

    private final GatorMailWidget client;

    public PrimaryMailboxPanel(final GatorMailWidget client) {
        this.client = client;

        initWidget(panel);
        addStyleName("gm-PrimaryMailboxPanel");
        setWidth("100%");
    }


    protected void onAttach() {
        super.onAttach();

        client.addPropertyChangeListener(primaryMailboxPropertyChangeListener);

        updateMailboxLinks();
    }

    protected void onDetach() {
        super.onDetach();

        client.removePropertyChangeListener(primaryMailboxPropertyChangeListener);
    }

    private void updateMailboxLinks() {
        final Account account = client.getAccount();
        if (account != null) {
            panel.clear();

            if (account.getInboxFolderName() != null) {
                final GMFolder inbox = client.fetchFolder(account.getInboxFolderName());
                client.requestUpdate(inbox);
                panel.add(new FolderHyperlink(client, inbox, "INBOX"));
            }

            if (account.getDraftsFolderName() != null) {
                final GMFolder drafts = client.fetchFolder(account.getDraftsFolderName());
                client.requestUpdate(drafts);
                panel.add(new FolderHyperlink(client, drafts, "Drafts"));
            }

            if (account.getJunkFolderName() != null) {
                final GMFolder junk = client.fetchFolder(account.getJunkFolderName());
                client.requestUpdate(junk);
                panel.add(new FolderHyperlink(client, junk, "Junk"));
            }

            if (account.getSentFolderName() != null) {
                final GMFolder sent = client.fetchFolder(account.getSentFolderName());
                client.requestUpdate(sent);
                panel.add(new FolderHyperlink(client, sent, "Sent"));
            }

            if (account.getTrashFolderName() != null) {
                final GMFolder trash = client.fetchFolder(account.getTrashFolderName());
                client.requestUpdate(trash);
                panel.add(new FolderHyperlink(client, trash, "Trash"));
            }

        } else {
            panel.clear();
        }
    }

    private class PrimaryMailboxPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName() == null || "account".equals(evt.getPropertyName())) {
                updateMailboxLinks();
            }
        }
    }
}
