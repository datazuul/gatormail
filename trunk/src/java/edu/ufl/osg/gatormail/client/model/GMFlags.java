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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Bean for a message's Flags.
 *
 * @author Sandy McArthur
 */
public class GMFlags implements Serializable {

    /**
     * @gwt.typeArgs <edu.ufl.osg.gatormail.client.model.GMFlags.GMFlag>
     */
    private Set/*<GMFlag>*/ systemFlags;

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private Set/*<String>*/ userFlags;


    public Collection getSystemFlags() {
        if (systemFlags == null) {
            systemFlags = new HashSet();
        }
        return systemFlags;
    }

    public Collection getUserFlags() {
        if (userFlags == null) {
            userFlags = new HashSet();
        }
        return userFlags;
    }

    public boolean contains(final GMFlag flag) {
        return systemFlags != null && systemFlags.contains(flag);
    }

    public boolean contains(final String flag) {
        return userFlags != null && userFlags.contains(flag);
    }

    public void addFlag(final GMFlag flag) {
        getSystemFlags().add(flag);
    }

    public void addFlag(final String flag) {
        getUserFlags().add(flag);
    }


    /**
     * Bean for a Flag.
     */
    public static class GMFlag implements Serializable {
        public static final GMFlag ANSWERED = new GMFlag("ANSWERED");
        public static final GMFlag DELETED = new GMFlag("DELETED");
        public static final GMFlag DRAFT = new GMFlag("DRAFT");
        public static final GMFlag FLAGGED = new GMFlag("FLAGGED");
        public static final GMFlag RECENT = new GMFlag("RECENT");
        public static final GMFlag SEEN = new GMFlag("SEEN");
        public static final GMFlag USER = new GMFlag("USER");

        private String name = null;

        public GMFlag() {
        }

        private GMFlag(final String name) {
            this.name = name;
        }

        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof GMFlag)) return false;

            final GMFlag gmFlag = (GMFlag)o;
            return name.equals(gmFlag.name);
        }

        public int hashCode() {
            return name.hashCode();
        }
    }
}
