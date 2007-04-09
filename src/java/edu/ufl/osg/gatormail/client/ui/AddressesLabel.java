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

package edu.ufl.osg.gatormail.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import edu.ufl.osg.gatormail.client.model.GMInternetAddress;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.GMAddress;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Base class for a Label the shows a list of addresses for a field.
 *
 * @author Sandy McArthur
 */
public abstract class AddressesLabel extends Composite {
    private final Label label = new Label();
    private final PropertyChangeListener headersPropertyChangeListener = new HeadersPropertyChangeListener();
    private final GMMessage message;

    public AddressesLabel(final GMMessage message) {
        this.message = message;
        initWidget(label);
    }


    protected void onAttach() {
        super.onAttach();

        getMessage().addPropertyChangeListener("headers", headersPropertyChangeListener);

        updateAddress();
    }

    protected void onDetach() {
        super.onDetach();

        getMessage().removePropertyChangeListener("headers", headersPropertyChangeListener);
    }

    protected GMMessage getMessage() {
        return message;
    }

    protected abstract GMAddress[] getAddresses();

    private void updateAddress() {
        final GMAddress[] addresses = getAddresses();
        if (addresses == null) {
            label.setText(null);
            label.setTitle(null);
            return;
        }

        String text = "";
        String title = "";
        for (int i=0; i < addresses.length; i++) {
            final GMAddress address = addresses[i];
            if (address instanceof GMInternetAddress) {
                final GMInternetAddress internetAddress = (GMInternetAddress)address;
                if (!"".equals(text)) {
                    text += ", ";
                }
                if (!"".equals(title)) {
                    title += ", ";
                }

                if (internetAddress.getPersonal() != null && internetAddress.getPersonal().length() > 0) {
                    // if there is a personal name
                    text += internetAddress.getPersonal();
                    title += internetAddress.getAddress();
                    
                } else if (internetAddress.getAddress() != null) {
                    // use the part before the "@"
                    String s = internetAddress.getAddress();
                    title += s;
                    if (s.indexOf("@") > 0) s = s.substring(0, s.indexOf("@"));
                    text += s;

                } else {
                    assert internetAddress.getPersonal() != null && internetAddress.getAddress() != null : "what's the use of an address with out a personal or address part?";
                }

            } else {
                if (!"".equals(text)) {
                    text += ", ";
                }
                if (!"".equals(title)) {
                    title += ", ";
                }
                text += address.toString();
                title += address.toString();
            }
        }
        label.setText(text);
        label.setTitle(title);
    }

    private class HeadersPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent event) {
            updateAddress();
        }
    }
}
