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

package edu.ufl.osg.gatormail.client.ui.welcome;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import edu.ufl.osg.gatormail.client.GatorMailWidget;

/**
 * A quick loading welcome page for the user.
 *
 * @author Sandy McArthur
 */
public final class WelcomeView extends Composite {
    private final GatorMailWidget client;

    private String tipId;
    private String wecomeTextId;
    private String wecomeClearId;
    private HTMLPanel hp;

    public WelcomeView(final GatorMailWidget client) {
        this.client = client;

        tipId = HTMLPanel.createUniqueId();
        wecomeTextId = HTMLPanel.createUniqueId();
        wecomeClearId = HTMLPanel.createUniqueId();
        String panelHtml = "";
        panelHtml += "<div id=\"" + tipId + "\" class=\"gm-WelcomeView-Tips\" style=\"float:right;\"></div>";
        panelHtml += "<div id=\"" + wecomeTextId + "\" class=\"gm-WelcomeView-Text\"></div>";
        panelHtml += "<div id=\"" + wecomeClearId + "\" class=\"gm-WelcomeView-Cleared\" style=\"clear:both;\"></div>";
        hp = new HTMLPanel(panelHtml);
        initWidget(hp);

        hp.add(new Label("Tips will go here."), tipId);
        hp.add(new HTML("This is alpha quality code.<br/>Expect it to be buggy and slow.<br/>Select on a folder on the left."), wecomeTextId);
    }
}
