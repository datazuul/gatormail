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

package edu.ufl.osg.gatormail.client.model.message.multipart;

import edu.ufl.osg.gatormail.client.model.message.GMPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <code>multipart/mixed</code> part.
 *
 * <p>
 * The "mixed" subtype of "multipart" is intended for use when the body
 * parts are independent and need to be bundled in a particular order.
 * Any "multipart" subtypes that an implementation does not recognize
 * must be treated as being of subtype "mixed".
 * </p>
 *
 * @author Sandy McArthur
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2045.txt">RFC 2045</a>
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2046.txt">RFC 2046</a>
 */
public class GMMixed extends GMPart {
    /**
     * @gwt.typeArgs <edu.ufl.osg.gatormail.client.model.message.GMPart>
     */
    private List/*<GMPart>*/ parts;

    public void addPart(final GMPart part) {
        if (parts == null) {
            parts = new ArrayList/*<GMPart>*/();
        }
        parts.add(part);
    }

    public List/*<GMPart>*/ getParts() {
        return parts != null ? parts : Collections.EMPTY_LIST;
    }

    public void setParts(final List/*<GMPart>*/ parts) {
        this.parts = parts;
    }
}
