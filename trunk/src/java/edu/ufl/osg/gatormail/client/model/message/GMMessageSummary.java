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

package edu.ufl.osg.gatormail.client.model.message;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.mcarthur.sandy.gwt.event.property.client.PropertyChangeSource;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class GMMessageSummary implements IsSerializable, PropertyChangeSource {

    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private String oneLiner;
    private boolean oneLinerTruncated = false;
    private String sample;
    private boolean sampleTruncated = false;

    public boolean isOneLinerTruncated() {
        return oneLinerTruncated;
    }

    public void setOneLinerTruncated(final boolean oneLinerTruncated) {
        this.oneLinerTruncated = oneLinerTruncated;
    }

    public String getOneLiner() {
        return oneLiner;
    }

    public void setOneLiner(final String oneLiner) {
        this.oneLiner = oneLiner;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(final String sample) {
        this.sample = sample;
    }

    public boolean isSampleTruncated() {
        return sampleTruncated;
    }

    public void setSampleTruncated(final boolean sampleTruncated) {
        this.sampleTruncated = sampleTruncated;
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
