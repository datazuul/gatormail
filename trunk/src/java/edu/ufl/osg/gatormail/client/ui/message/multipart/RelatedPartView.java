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

package edu.ufl.osg.gatormail.client.ui.message.multipart;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import edu.ufl.osg.gatormail.client.model.message.GMPart;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMRelated;
import edu.ufl.osg.gatormail.client.ui.message.PartViewFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * View for {@link edu.ufl.osg.gatormail.client.model.message.multipart.GMRelated}.
 *
 * @author Sandy McArthur
 */
public class RelatedPartView extends Composite {
    private final SimplePanel sp = new SimplePanel();

    private final GMRelated related;


    public RelatedPartView(final GMRelated related) {
        this.related = related;

        initWidget(sp);
    }

    protected void onAttach() {
        super.onAttach();

        updatePart();
    }

    protected void onDetach() {
        super.onDetach();
    }

    private void updatePart() {
        // TODO: convert to use list iterator with GWT 1.4
        final List parts = new ArrayList(related.getParts());

        final String cid = related.getContentId();

        if (cid != null) {
            for (int i=parts.size()-1; i >=0; i--) {
                final GMPart part = (GMPart)parts.get(i);

                if (cid.equals(part.getContentId())) {
                    final Widget view = PartViewFactory.loadView(part);
                    sp.setWidget(view);
                    return;
                }
            }
        }

        // If no cid missing or doesn't match, use first part
        sp.setWidget(PartViewFactory.loadView((GMPart)parts.get(0)));
    }
}
