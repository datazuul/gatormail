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

package edu.ufl.osg.gatormail.server;

import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.server.state.PrivateStateCipher;

import javax.mail.Message;
import javax.mail.Part;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Serves images from email messages.
 *
 * @author Sandy McArthur
 */
public class ImagePartServlet extends HttpServlet {


    public void init(final ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        // Make sure the PrivateStateCipher is setup first.
        final PrivateStateCipher psc;
        try {
            psc = (PrivateStateCipher)servletConfig.getServletContext().getAttribute(PrivateStateCipher.class.getName());
        } catch (ClassCastException cce) {
            throw new ServletException("Unknown Encryption services provider.", cce);
        }
        if (psc == null) {
            // PrivateStateCipher hasn't been set
            throw new UnavailableException("Encryption Services not yet initialized.", 10); // try again in 10 seconds
        }
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final PrivateStateCipher psc = (PrivateStateCipher)getServletContext().getAttribute(PrivateStateCipher.class.getName());
        assert psc != null : "Webapp not properly initialized, PrivateStateCipher missing.";

        final String requestedUri = request.getRequestURI();
        final StringTokenizer st = new StringTokenizer(requestedUri, "/");
        String token = null;
        Object p = null;
        while (p == null && st.hasMoreTokens()) {
            token = st.nextToken();
            try {
                p = psc.decode(token);
            } catch (Exception e) {
                // ignore
            }
        }

        if (!(p instanceof ImagePartBean)) {
            throw new IllegalArgumentException("not a legal token: " + token);
        }

        final ImagePartBean part = (ImagePartBean)p;
        // TODO: find the needed part
    }

    public static ImagePartBean createImagePart(final Account account, final Message message, final Part part) {
        // TODO: extract image part information
        return null;
    }

    private static class ImagePartBean implements Serializable {

    }
    
}
