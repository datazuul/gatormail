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

import com.google.gwt.user.client.rpc.SerializableException;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.services.LoginService;
import edu.ufl.osg.gatormail.server.state.PrivateStateCipher;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.ContentDisposition;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
        // XXX: If the servlet that serves images changes names, this needs to be updated.
        while (st.hasMoreTokens() && !"image".equals(st.nextToken())) {
            // n/a
        }
        final String token = st.nextToken();
        final Object p = psc.decode(token);

        if (!(p instanceof ImagePartBean)) {
            throw new IllegalArgumentException("not a legal token: " + token);
        }

        final ImagePartBean imagePart = (ImagePartBean)p;

        final Session session;
        try {
            session = MessageServiceImpl.fetchSession(imagePart.getAccount());
        } catch (SerializableException e) {
            throw new ServletException("Unable to create mail store session.", e);
        }
        final Store store;
        try {
            store = MessageServiceImpl.fetchConnectedStore(session);
        } catch (LoginService.LoginException e) {
            throw new ServletException("Unable to connect to mail store.", e);
        }

        final Folder folder;
        try {
            folder = store.getFolder(imagePart.getFolder());
        } catch (MessagingException e) {
            throw new ServletException(e.getMessage(), e);
        }

        final UIDFolder uidFolder = (UIDFolder)folder;
        try {
            if (imagePart.getUidValidity() != uidFolder.getUIDValidity()) {
                response.sendError(HttpServletResponse.SC_GONE, "Token no longer valid for this image.");
                return;
            }
        } catch (MessagingException e) {
            throw new ServletException("Unable to verify token validity.", e);
        }

        try {
            folder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            throw new ServletException("Unable to open folder in mail store.", e);
        }

        try {
            final Message message;

            try {
                message = uidFolder.getMessageByUID(imagePart.getUid());

            } catch (MessagingException e) {
                throw new ServletException("Unable to mail store.", e);
            }

            if (message == null) {
                response.sendError(HttpServletResponse.SC_GONE, "Image no longer available.");
                return;
            }

            final Part part;
            try {
                part = findPart(message, imagePart.getPartPath());
            } catch (MessagingException e) {
                throw new ServletException("Problem locating image", e);
            }

            // Set headers
            final ContentType contentType = new ContentType(part.getContentType());

            final ContentDisposition contentDisposition = new ContentDisposition("inline"); // alt: attachment
            String fileName = part.getFileName();
            if (fileName != null) {
                if (fileName.startsWith("=?")) {
                    fileName = MimeUtility.decodeWord(fileName);
                }
                contentDisposition.setParameter("filename", fileName);
            }

            response.setContentType(contentType.getBaseType());
            response.setHeader("Content-Disposition", contentDisposition.toString());

            // Output the part
            final InputStream in = part.getInputStream();
            final ServletOutputStream out = response.getOutputStream();

            final byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
                    
        } catch (MessagingException e) {
            throw new ServletException("Unexpected failure loading image.", e);

        } finally {
            try {
                folder.close(false);
            } catch (MessagingException e) {
                // ignored
            }
            try {
                store.close();
            } catch (MessagingException e) {
                // ignored
            }
        }
    }

    private Part findPart(final Part part, final String path) throws MessagingException, IOException {
        if ("".equals(path)) {
            return part;
        } else {
            final Multipart multipart = (Multipart)part.getContent();
            final BodyPart nextPart = multipart.getBodyPart(nextPart(path));
            return findPart(nextPart, nextPath(path));
        }
    }

    private int nextPart(final String path) {
        final StringTokenizer st = new StringTokenizer(path, ".");
        return Integer.valueOf(st.nextToken());
    }

    private String nextPath(final String path) {
        final StringTokenizer st = new StringTokenizer(path, ".");
        st.nextToken(); // skip the current one
        final StringBuffer sb = new StringBuffer(path.length()-2);
        while (st.hasMoreTokens()) {
            sb.append(".").append(st.nextToken());
        }
        return sb.toString();
    }

    public static ImagePartBean createImagePart(final Account account, final Folder folder, final Message message, final String partPath) throws MessagingException {
        final UIDFolder uidFolder = (UIDFolder)folder;
        return new ImagePartBean(account, folder.getFullName(), uidFolder.getUIDValidity(), uidFolder.getUID(message), partPath);
    }

    public static class ImagePartBean implements Serializable {
        private final Account account;
        private final String folder;
        private final long uidValidity;
        private final long uid;
        private final String partPath;

        public ImagePartBean(final Account account, final String folder, final long uidValidity, final long uid, final String partPath) {
            this.account = account;
            this.folder = folder;
            this.uid = uid;
            this.uidValidity = uidValidity;
            this.partPath = partPath;
        }

        public Account getAccount() {
            return account;
        }

        public String getFolder() {
            return folder;
        }

        public String getPartPath() {
            return partPath;
        }

        public long getUid() {
            return uid;
        }

        public long getUidValidity() {
            return uidValidity;
        }


        public String toString() {
            return "ImagePartBean{" +
                    "account=" + account +
                    ", folder='" + folder + '\'' +
                    ", uidValidity=" + uidValidity +
                    ", uid=" + uid +
                    ", partPath='" + partPath + '\'' +
                    '}';
        }
    }
}
