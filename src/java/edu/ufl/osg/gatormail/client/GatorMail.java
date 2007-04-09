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

package edu.ufl.osg.gatormail.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;

/**
 * A basic EntryPoint for the GatorMail client.
 *
 * @author Sandy McArthur
 */
public class GatorMail implements EntryPoint {
    public void onModuleLoad() {
        RootPanel gm2 = RootPanel.get("GatorMail2");

        if (gm2 == null) {
            gm2 = RootPanel.get();
            GWT.log("Warning, using default root panel.", null);
        }

        final GatorMailWidget mcw = new GatorMailWidget();
        gm2.add(mcw);
    }
}
