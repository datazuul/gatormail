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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import org.mcarthur.sandy.gwt.event.list.client.EventList;
import org.mcarthur.sandy.gwt.event.list.client.ListEventListener;
import org.mcarthur.sandy.gwt.event.list.client.ListEvent;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;

/**
 * A CheckBox to indicate selections.
 *
 * @author Sandy McArthur
 */
public class SelectionCheckBox extends Composite {

    private final CheckBox checkBox = new CheckBox();
    private final SelectionListEventListener selectionListEventListener = new SelectionListEventListener();
    private final SelectionClickListener selectionClickListener = new SelectionClickListener();

    private final EventList selections;
    private final GMMessage message;

    public SelectionCheckBox(final EventList selections, final GMMessage message) {
        assert selections != null;
        assert message != null;
        this.selections = selections;
        this.message = message;

        initWidget(checkBox);
    }


    protected void onAttach() {
        super.onAttach();

        selections.addListEventListener(selectionListEventListener);
        checkBox.addClickListener(selectionClickListener);

        updateSelected();
    }

    protected void onDetach() {
        super.onDetach();

        selections.removeListEventListener(selectionListEventListener);
        checkBox.removeClickListener(selectionClickListener);
    }

    private void updateSelected() {
        checkBox.setChecked(selections.contains(message));
    }

    private class SelectionClickListener implements ClickListener {
        public void onClick(final Widget sender) {
            final CheckBox checkBox = (CheckBox)sender;
            if (checkBox.isChecked()) {
                assert !selections.contains(message) : "Umm, message already selected?";
                selections.add(message);
            } else {
                assert selections.contains(message) : "Umm, message not selected?";
                selections.remove(message);
            }
        }
    }

    private class SelectionListEventListener implements ListEventListener {
        public void listChanged(final ListEvent listEvent) {
            // XXX: optimize this based on ListEvent
            updateSelected();
        }
    }
}
