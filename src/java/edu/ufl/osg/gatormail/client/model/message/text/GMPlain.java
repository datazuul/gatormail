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

package edu.ufl.osg.gatormail.client.model.message.text;

import edu.ufl.osg.gatormail.client.model.message.GMPart;

/**
 * <code>text/plain</code> part.
 *
 * @author Sandy McArthur
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2046.txt">RFC 2046</a>
 * @see <a href="http://www.rfc-editor.org/rfc/rfc3676.txt">RFC 3676</a>
 */
public class GMPlain extends GMPart {
    private String plain;
    private String format;

    public GMPlain() {
    }

    public GMPlain(final String plain) {
        this.plain = plain;
    }

    public boolean isFlowed() {
        return "flowed".equalsIgnoreCase(getFormat());
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        final Object old = getFormat();
        this.format = format;
        firePropertyChange("format", old, format);
    }

    public String getPlain() {
        return plain;
    }

    public void setPlain(final String plain) {
        final Object old = getPlain();
        this.plain = plain;
        firePropertyChange("plain", old, plain);
    }
}
