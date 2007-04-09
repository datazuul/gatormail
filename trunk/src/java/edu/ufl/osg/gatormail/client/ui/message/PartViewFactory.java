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

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import edu.ufl.osg.gatormail.client.model.message.GMMessagePart;
import edu.ufl.osg.gatormail.client.model.message.text.GMPlain;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMMixed;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMAlternative;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMDigest;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMParallel;
import edu.ufl.osg.gatormail.client.model.message.multipart.GMRelated;
import edu.ufl.osg.gatormail.client.ui.message.multipart.MixedPartView;
import edu.ufl.osg.gatormail.client.ui.message.text.PlainTextView;

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

            /*
            if (part instanceof GMAlternative) {
            } else if (part instanceof GMDigest) {
            } else if (part instanceof GMParallel) {
            } else if (part instanceof GMRelated) {
            }
            */
            /* GMMixed */
            if (!"edu.ufl.osg.gatormail.client.model.message.multipart.GMMixed".equals(GWT.getTypeName(part))) {
                GWT.log("Unexpected Message Part type: " + GWT.getTypeName(part), new RuntimeException(GWT.getTypeName(part)));
            }
            return new MixedPartView(mixed);
        } else if (part instanceof GMPlain) {
            return new PlainTextView((GMPlain)part);
        }

        GWT.log("Unexpected Message Part type: " + GWT.getTypeName(part), new RuntimeException(GWT.getTypeName(part)));
        return null;
    }
}
