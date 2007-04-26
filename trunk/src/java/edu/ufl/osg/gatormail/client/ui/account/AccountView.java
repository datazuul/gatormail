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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.Account;

/**
 * Account information view.
 *
 * @author Sandy McArthur
 */
public class AccountView extends Composite {

    private final VerticalPanel vp = new VerticalPanel();
    private final TabBar tabs = new TabBar();
    private final SimplePanel sp = new SimplePanel();

    private final GatorMailWidget client;
    private final Account account;


    public AccountView(final GatorMailWidget client, final Account account) {
        this.client = client;
        this.account = account;

        initWidget(vp);
        addStyleName("gm-AccountView");

        // Top bar
        final HorizontalPanel headerRow = new HorizontalPanel();
        headerRow.addStyleName("gm-AccountView-HeaderRow");
        headerRow.setWidth("100%");

        final Label label = new Label(account.getAccountName() + " Account Information");
        headerRow.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        headerRow.add(label);
        headerRow.setCellWidth(label, "100%");

        vp.add(headerRow);

        tabs.addStyleName("gm-AccountView-Tabs");
        tabs.addTab("Summary");
        tabs.selectTab(0);
        vp.add(tabs);

        sp.add(new SummaryPanel(client, account));
        vp.add(sp);
    }
}
