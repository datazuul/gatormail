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

package edu.ufl.osg.gatormail.client.ui.message.text;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import edu.ufl.osg.gatormail.client.model.message.text.GMHtml;

/**
 * View for {@link edu.ufl.osg.gatormail.client.model.message.text.GMHtml}.
 *
 * @author Sandy McArthur
 */
public class HtmlPartView extends Composite {
    private final Label text = new Label();

    private final GMHtml html;


    public HtmlPartView(final GMHtml html) {
        this.html = html;

        initWidget(text);
        addStyleName("gm-HtmlPartView");
    }


    protected void onAttach() {
        super.onAttach();

        // TODO: property change stuff

        updateHtml();
    }

    protected void onDetach() {
        super.onDetach();
    }

    private void updateHtml() {
        //text.setText(html.getHtml());
        text.setText("HTML Cannot be securely rendered yet.");
    }

}
