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

import com.google.gwt.user.client.ui.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.GatorMailWidget;

/**
 * The Widigets across the header of the page.
 *
 * @author Sandy McArthur
 */
public class HeaderPanel extends Composite {

    private final HorizontalPanel panel = new HorizontalPanel();
    private final Label user = new Label();
    private final HeaderPanelPropertyChangeListener headerPanelPropertyChangeListener = new HeaderPanelPropertyChangeListener();

    private final GatorMailWidget client;

    public HeaderPanel(final GatorMailWidget client) {
        this.client = client;

        initWidget(panel);

        addStyleName("gm-HeaderPanel");
        setWidth("100%");
        panel.setVerticalAlignment(HasAlignment.ALIGN_TOP);
        panel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);

        final HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(3);
        hp.add(new Hyperlink("settings", null));
        hp.add(new Label("|"));
        hp.add(user);

        panel.add(hp);
    }

    protected void onAttach() {
        super.onAttach();

        client.addPropertyChangeListener(headerPanelPropertyChangeListener);

        updateUserLabel();
    }

    protected void onDetach() {
        super.onDetach();

        client.removePropertyChangeListener(headerPanelPropertyChangeListener);
    }

    private void updateUserLabel() {
        final Account account = client.getAccount();
        if (account !=  null) {
            user.setText(account.getUsername() + "@" + account.getAccountDomain());
            user.setTitle(account.getAccountName());
        } else {
            user.setText(null);
        }
    }

    private class HeaderPanelPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName() == null || "account".equals(evt.getPropertyName())) {
                updateUserLabel();
            }
        }
    }
}
