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
 * <code>text/html</code> part.
 *
 * @author Sandy McArthur
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2854.txt">RFC 2854</a>
 */
public class GMHtml extends GMPart {
    private String html;

    public GMHtml() {
    }

    public GMHtml(final String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(final String html) {
        final Object old = getHtml();
        this.html = html;
        firePropertyChange("html", old, html);
    }
}
