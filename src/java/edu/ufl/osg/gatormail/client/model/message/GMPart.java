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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Base class for a GMMessage part.
 *
 * @author Sandy McArthur
 */
public abstract class GMPart implements IsSerializable, PropertyChangeSource {
    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private String contentId;
    private String description;
    private GMContentDisposition disposition;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(final String contentId) {
        final Object old = getContentId();
        this.contentId = contentId;
        firePropertyChange("contentId", old, contentId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        final Object old = getDescription();
        this.description = description;
        firePropertyChange("description", old, description);
    }

    public GMContentDisposition getDisposition() {
        return disposition;
    }

    public void setDisposition(final GMContentDisposition disposition) {
        final Object old = getDisposition();
        this.disposition = disposition;
        firePropertyChange("disposition", old, disposition);
    }

    protected final void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
