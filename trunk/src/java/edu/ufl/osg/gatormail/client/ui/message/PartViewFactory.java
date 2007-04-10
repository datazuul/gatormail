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

package edu.ufl.osg.gatormail.client.ui.message;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import edu.ufl.osg.gatormail.client.model.message.GMMessagePart;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMAlternative;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMDigest;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMMixed;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMParallel;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMRelated;
import edu.ufl.osg.gatormail.client.model.message.text.GMHtml;
import edu.ufl.osg.gatormail.client.model.message.text.GMPlain;
import edu.ufl.osg.gatormail.client.ui.message.multipart.AlternativePartView;
import edu.ufl.osg.gatormail.client.ui.message.multipart.MixedPartView;
import edu.ufl.osg.gatormail.client.ui.message.text.HtmlPartView;
import edu.ufl.osg.gatormail.client.ui.message.text.PlainPartView;

/**
 * TODO: Write class JavaDoc.
 *
 * @author Sandy McArthur
 */
public class PartViewFactory {

    private PartViewFactory() {
    }

    public static Widget loadView(final GMMessagePart part) {
        if (part instanceof GMMixed) {
            final GMMixed mixed = (GMMixed)part;

            if (part instanceof GMAlternative) {
                return new AlternativePartView((GMAlternative)mixed);
                
            } else if (part instanceof GMRelated) {
            } else if (part instanceof GMDigest || part instanceof GMParallel) {
                // digest and parallel are effectively identical to multipart/mixed when it comes to rendering.
                return new MixedPartView(mixed);
            }
            /* GMMixed */
            if (!"edu.ufl.osg.gatormail.client.model.message.multipart.GMMixed".equals(GWT.getTypeName(part))) {
                GWT.log("Unexpected Multipart Part type: " + GWT.getTypeName(part), new RuntimeException(GWT.getTypeName(part)));
            }
            return new MixedPartView(mixed);
        } else if (part instanceof GMPlain) {
            return new PlainPartView((GMPlain)part);
        } else if (part instanceof GMHtml) {
            return new HtmlPartView((GMHtml)part);
        }

        GWT.log("Unexpected Message Part type: " + GWT.getTypeName(part), new RuntimeException(GWT.getTypeName(part)));
        return null;
    }
}
