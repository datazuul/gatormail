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

package edu.ufl.osg.gatormail.client.model.messageList;

import java.io.Serializable;

/**
 * Current Filters to apply to a folder
 *
 * @author Sandy McArthur
 */
public class Filter implements Serializable {
    // TODO: All, Flagged, Read, Unread, From>, To>, Deleted

    private static final String[] TYPES = new String[] {"CUSTOM", "All", "Read", "Unread", "Flagged", "Not Flagged", "To me", "To, CC me", "Not To me", "Deleted"};

    public static final Filter ALL = new Filter(1, TYPES[1]);
    public static final Filter READ = new Filter(2, TYPES[2]); 

    private int index;
    private String description;


    public Filter() {
        this(1, TYPES[1]);
    }


    private Filter(final int index, final String description) {
        this.index = index;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o instanceof Filter) {
            final Filter filter = (Filter)o;

            if (index != filter.index) return false;
            return !(description != null ? !description.equals(filter.description) : filter.description != null);
        }
        return false;
    }

    public int hashCode() {
        int result;
        result = index;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Filter{" +
                "index=" + index +
                ", description='" + description + '\'' +
                '}';
    }
}
