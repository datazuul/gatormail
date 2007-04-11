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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.ufl.osg.gatormail.client.model.message.GMPart;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMMixed;
import edu.ufl.osg.gatormail.client.ui.message.PartViewFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

/**
 * View for {@link GMMixed}.
 *
 * @author Sandy McArthur
 */
public class MixedPartView extends Composite {
    private final VerticalPanel vp = new VerticalPanel();

    private final GMMixed mixed;
    private final MixedPartPropertyChangeListener mixedPartPropertyChangeListener = new MixedPartPropertyChangeListener();


    public MixedPartView(final GMMixed mixed) {
        this.mixed = mixed;

        initWidget(vp);
        addStyleName("gm-MixedPartView");
    }


    protected void onAttach() {
        super.onAttach();

        mixed.addPropertyChangeListener(mixedPartPropertyChangeListener);

        updatePart();
    }

    protected void onDetach() {
        super.onDetach();

        mixed.removePropertyChangeListener(mixedPartPropertyChangeListener);
    }


    protected void updatePart() {
        final Iterator iter = mixed.getParts().iterator();
        while (iter.hasNext()) {
            final Widget w = PartViewFactory.loadView((GMPart)iter.next());
            vp.add(w);
        }
    }

    private class MixedPartPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            updatePart();
        }
    }
}
