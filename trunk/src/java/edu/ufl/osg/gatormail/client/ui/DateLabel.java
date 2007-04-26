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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * Renders a Date from a message.
 *
 * @author Sandy McArthur
 */
public abstract class DateLabel extends Composite {
    private final Label label = new Label();
    private final Timer timer = new Timer() {
        public void run() {
            label.setText(summary());
        }
    };
    private final HeaderPropertyChangeListener headerPropertyChangeListener = new HeaderPropertyChangeListener();

    private final GMMessage message;

    public DateLabel(final GMMessage message) {
        this.message = message;
        initWidget(label);

    }

    protected GMMessage getMessage() {
        return message;
    }

    protected abstract Date getDate();

    protected void onAttach() {
        super.onAttach();

        getMessage().addPropertyChangeListener("headers", headerPropertyChangeListener);

        updateDate();

        // XXX: smarter scheduling
        timer.scheduleRepeating(1000 * 60); // every minute
    }

    protected void onDetach() {
        super.onDetach();

        getMessage().removePropertyChangeListener("headers", headerPropertyChangeListener);

        timer.cancel();
    }

    private void updateDate() {
        final Date date = getDate();
        if (date == null) {
            label.setTitle(null);
            label.setText(null);
            return;
        }

        label.setTitle(date.toString());
        label.setText(summary());
    }

    private String summary() {
        final Date date = getDate();
        if (date == null) {
            return null;
        }

        // Before this year
        final Date thisYear = new Date();
        thisYear.setMonth(0);
        thisYear.setHours(0);
        thisYear.setMinutes(0);
        thisYear.setSeconds(0);
        if (date.before(thisYear)) {
            // mm/dd/yy
            String year = "" + date.getYear();
            if (year.length() > 2) year = year.substring(year.length()-2);
            return (date.getMonth() + 1) + "/" + (date.getDate()) + "/" + year;
        }

        // Before today
        final Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        if (date.before(today)) {
            // Mon Day
            final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            return months[date.getMonth()] + " " + date.getDate();
        }

        // Today
        final Date now = new Date();
        if (date.after(today) && date.before(now)) {

            final Date tenMinAgo = new Date();
            tenMinAgo.setMinutes(now.getMinutes()-10);
            if (date.after(tenMinAgo)) {
                final long min = (long)((now.getTime() - date.getTime()) / 60000f);
                return "" + min + " min ago";
            }

            final boolean am;
            String s;
            // H:MM xm
            if (date.getHours() < 12) {
                // am
                am = true;
                if (date.getHours() == 0) {
                    s = "12";
                } else {
                    s = "" + date.getHours();
                }
            } else {
                // pm
                am = false;
                if (date.getHours() == 12) {
                    s = "12";
                } else {
                    s = "" + (date.getHours() - 12);
                }
            }

            final int minutes = date.getMinutes();
            if (minutes < 10) {
                s += ":0" + minutes;
            } else {
                s += ":" + minutes;

            }
            s += am ? " am" : " pm";
            return s;
        }

        // Other? future?
        return date.toString();
    }

    private class HeaderPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent event) {
            updateDate();
        }
    }
}
