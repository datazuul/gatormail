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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.impl.GatorLinkAccount;
import edu.ufl.osg.gatormail.client.services.LoginService;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Properties;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

    public Account autoLogin() {
        final String autoUser = System.getProperty("gm.user", null);
        final String autoPass = System.getProperty("gm.pass", null);
        if (autoUser != null && autoPass != null) {
            final GatorLinkAccount account = new GatorLinkAccount();
            account.setUsername(autoUser);
            account.setPassword(decode(autoPass));
            return account;
        } else {
            return null;
        }
    }

    public LoginResult doLogin(final String username, final String password) throws LoginException {
        final HttpServletRequest request = getThreadLocalRequest();
        final HttpSession httpSession = request.getSession();

        final Properties props = new Properties(System.getProperties());
        //props.setProperty("mail.debug", "true");

        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", "imap.ufl.edu");
        props.setProperty("mail.imap.port", "143");
        //props.setProperty("", "");

        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "smtp.ufl.edu");
        props.setProperty("mail.smtp.port", "587");
        //props.setProperty("", "");


        final Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        final Store store;
        try {
            store = session.getStore();
        } catch (NoSuchProviderException e) {
            throw new LoginException(e.getMessage(), e);
        } catch (Exception e) {
            throw new LoginException(e.getMessage(), e);
        }
        httpSession.setAttribute("javamail.session", session);

        try {
            store.connect();
        } catch (MessagingException e) {
            throw new LoginException(e.getMessage(), e);
        } catch (Exception e) {
            throw new LoginException(e.getMessage(), e);
        }


        final Folder folder;
        try {
            folder = store.getFolder("INBOX");
        } catch (MessagingException e) {
            throw new LoginException(e.getMessage(), e);
        } catch (Exception e) {
            throw new LoginException(e.getMessage(), e);
        }

        try {
            folder.getMessageCount();
        } catch (MessagingException e) {
            throw new LoginException(e.getMessage(), e);
        } catch (Exception e) {
            throw new LoginException(e.getMessage(), e);
        }

        final LoginResult result = new LoginResult(true);
        final GatorLinkAccount account = new GatorLinkAccount();
        account.setUsername(username);
        account.setPassword(password);
        result.setAccount(account);
        return result;
    }

    private static String decode(final String code) {
        return new String(Base64.decode(code));
    }

    /*
    public static void main(String[] args) {
        System.out.println(Base64.encode("".getBytes()));
    }
    */

}