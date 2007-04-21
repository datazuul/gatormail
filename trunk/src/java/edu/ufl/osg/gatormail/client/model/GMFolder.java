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

package edu.ufl.osg.gatormail.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.mcarthur.sandy.gwt.event.property.client.PropertyChangeSource;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;


/**
 * GatorMail Folder bean.
 *
 * @author Sandy McArthur
 */
public final class GMFolder implements IsSerializable, PropertyChangeSource {

    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private String urlName;
    private String name;
    private String fullName;
    private boolean holdsFolders;
    private boolean holdsMessages;
    private int messageCount;
    private int deletedMessageCount;
    private int newMessageCount;
    private int unreadMessageCount;
    private int type;
    private char separator;

    /**
     * This is really a long but because JavaScript cannot store a long value reliably
     * we store is as a String and convert as needed.
     */
    private String uidValidity;

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private List/*<String>*/ subFolders = new ArrayList/*<String>*/();

    public GMFolder() {
    }

    public GMFolder(final String fullName) {
        setFullName(fullName);
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(final String urlName) {
        final Object old = getUrlName();
        this.urlName = urlName;
        pcs.firePropertyChange("urlName", old, urlName);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        final Object old = getName();
        this.name = name;
        pcs.firePropertyChange("name", old, name);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        final Object old = getFullName();
        this.fullName = fullName;
        pcs.firePropertyChange("fullName", old, fullName);
    }

    public boolean isHoldsFolders() {
        return holdsFolders;
    }

    public void setHoldsFolders(final boolean holdsFolders) {
        final boolean old = isHoldsFolders();
        this.holdsFolders = holdsFolders;
        pcs.firePropertyChange("holdsFolders", old, holdsFolders);
    }

    public boolean isHoldsMessages() {
        return holdsMessages;
    }

    public void setHoldsMessages(final boolean holdsMessages) {
        final boolean old = isHoldsMessages();
        this.holdsMessages = holdsMessages;
        pcs.firePropertyChange("holdsMessages", old, holdsMessages);
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(final int messageCount) {
        final int old = getMessageCount();
        this.messageCount = messageCount;
        pcs.firePropertyChange("messageCount", old, messageCount);
    }

    public int getDeletedMessageCount() {
        return deletedMessageCount;
    }

    public void setDeletedMessageCount(final int deletedMessageCount) {
        final int old = getDeletedMessageCount();
        this.deletedMessageCount = deletedMessageCount;
        pcs.firePropertyChange("deletedMessageCount", old, deletedMessageCount);
    }

    public int getNewMessageCount() {
        return newMessageCount;
    }

    public void setNewMessageCount(final int newMessageCount) {
        final int old = getNewMessageCount();
        this.newMessageCount = newMessageCount;
        pcs.firePropertyChange("newMessageCount", old, newMessageCount);
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(final int unreadMessageCount) {
        final int old = getUnreadMessageCount();
        this.unreadMessageCount = unreadMessageCount;
        pcs.firePropertyChange("unreadMessageCount", old, unreadMessageCount);
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        final int old = getType();
        this.type = type;
        pcs.firePropertyChange("type", old, type);
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(final char separator) {
        final Object old = new Character(getSeparator());
        this.separator = separator;
        pcs.firePropertyChange("separator", old, new Character(separator));
    }

    public long getUidValidity() {
        return uidValidity != null ? Long.parseLong(uidValidity) : -1;
    }

    public void setUidValidity(final long uidValidity) {
        final Object old = new Long(getUidValidity());
        this.uidValidity = Long.toString(uidValidity);
        pcs.firePropertyChange("uidValidity", old, new Long(uidValidity));
    }

    public String fullNameToName(final String fullName) {
        return fullName.substring(fullName.lastIndexOf(getSeparator()) + 1);
    }

    public void addSubFolder(final String fullName) {
        subFolders.add(fullName);
        pcs.firePropertyChange("subFolders", null, subFolders);
    }

    public List/*<String>*/ getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(final List/*<String>*/ subFolders) {
        final Object old = getSubFolders();
        this.subFolders = subFolders;
        pcs.firePropertyChange("subFolders", old, subFolders);
    }

    public void applyUpdate(final GMFolder folder) throws IllegalArgumentException {
        if (!getFullName().equals(folder.getFullName())) {
            throw new IllegalArgumentException("Cannot apply updates from a different folder.");
        }
        setDeletedMessageCount(folder.getDeletedMessageCount());
        setFullName(folder.getFullName());
        setHoldsFolders(folder.isHoldsFolders());
        setHoldsMessages(folder.isHoldsMessages());
        setMessageCount(folder.getMessageCount());
        setName(folder.getName());
        //setFullName(folder.getFullName());
        setNewMessageCount(folder.getNewMessageCount());
        setSeparator(folder.getSeparator());
        setSubFolders(folder.getSubFolders());
        setType(folder.getType());
        setUidValidity(folder.getUidValidity());
        setUnreadMessageCount(folder.getUnreadMessageCount());
        setUrlName(folder.getUrlName());
    }

    public boolean equals(final Object o) {
        return this == o || o instanceof GMFolder && urlName.equals(((GMFolder)o).urlName);
    }

    public int hashCode() {
        return urlName.hashCode();
    }

    public String toString() {
        return "GMFolder{" +
                "fullName='" + fullName + '\'' +
                '}';
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
