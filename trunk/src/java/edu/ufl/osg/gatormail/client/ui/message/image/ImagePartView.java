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

package edu.ufl.osg.gatormail.client.ui.message.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import edu.ufl.osg.gatormail.client.model.message.image.GMImage;

/**
 * View for {@link edu.ufl.osg.gatormail.client.model.message.image.GMImage}.
 *
 * @author Sandy McArthur
 */
public class ImagePartView extends Composite {
    // TODO: Prevent too large of iamge size
    // TODO: Provide a preview mode and full mode
    // TODO: Provide a download link

    private final GMImage imagePart;

    private final Image image = new Image();

    public ImagePartView(final GMImage imagePart) {
        this.imagePart = imagePart;

        initWidget(image);
        addStyleName("gm-ImagePartView");
    }


    protected void onAttach() {
        super.onAttach();

        updateImage();
    }

    private void updateImage() {
        final String filename;
        if (imagePart.getDisposition() != null) {
            filename = "/" + imagePart.getDisposition().getFilename();
        } else {
            filename = "/filename-not-given";
        }
        image.setUrl(GWT.getModuleBaseURL() + "image/" + imagePart.getToken() + filename);
        GWT.log("Set Image URL to: " + image.getUrl(), null);
    }
}
