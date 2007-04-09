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

package edu.ufl.osg.gatormail.client.ui.messageList;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageSummary;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class MessageSummaryView extends Composite {
    private final FocusPanel panel = new FocusPanel();
    private final GMMessage message;

    private boolean expanded = false;
    private final SummaryPropertyChangeListener summaryPropertyChangeListener = new SummaryPropertyChangeListener();

    public MessageSummaryView(final GMMessage message) {
        this.message = message;
        initWidget(panel);

        addStyleName("gm-MessageSummaryView");
    }

    protected void onAttach() {
        super.onAttach();

        getMessage().addPropertyChangeListener("summary", summaryPropertyChangeListener);

        updateSummary();
    }

    protected void onDetach() {
        super.onDetach();

        getMessage().removePropertyChangeListener("summary", summaryPropertyChangeListener);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(final boolean expanded) {
        if (this.expanded != expanded) {
            this.expanded = expanded;
            updateSummary();
        }
    }

    protected GMMessage getMessage() {
        return message;
    }

    private boolean clickListenerAdded = false;
    private void addClickListener() {
        if (!clickListenerAdded && message.getSummary() != null && message.getSummary().getSample() != null) {
            clickListenerAdded = true;
            panel.addClickListener(new MessageSummaryViewClickListener());
        }
    }
    private void updateSummary() {
        final GMMessage message = getMessage();
        final GMMessageSummary summary = message.getSummary();
        if (summary != null) {
            addClickListener();

            if (!isExpanded()) {
                final Label label = new Label();
                label.addStyleName("gm-MessageSummaryView-summary");
                label.setWordWrap(false);
                if (summary.getOneLiner() != null) {
                    label.setText(summary.getOneLiner());
                } else {
                    label.setText("(No plain text found.)");
                }
                if (summary.getSample() != null) {
                    //label.setTitle(summary.getSample()); // I find this annoying
                    label.addStyleName("gm-MessageSummaryView-summary-hasSample");
                }
                panel.setWidget(label);

            } else {
                final Label label = new Label();
                label.addStyleName("gm-MessageSummaryView-sample");
                if (summary.getSample() != null) {
                    label.setText(summary.getSample());
                } else {
                    label.setText("(No plain text found.)");
                }
                //label.setTitle(summary.getOneLiner());
                panel.setWidget(label);
            }
        } else {
            final Label loadingLabel = new Label("(loading...)");
            loadingLabel.addStyleName("gm-loading");
            panel.setWidget(loadingLabel);
        }
    }

    private class SummaryPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            updateSummary();
        }
    }

    private  class MessageSummaryViewClickListener implements ClickListener {
        public void onClick(final Widget sender) {
            //final MessageSummaryView msv = (MessageSummaryView)sender;
            setExpanded(!isExpanded());
        }
    }
}
