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

package edu.ufl.osg.gatormail.client.services;

import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.core.client.GWT;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.services.LoginServiceAsync;

/**
 * Login Service API
 */
public interface LoginService extends RemoteService {

    public Account autoLogin();

    public LoginResult doLogin(String username, String password) throws LoginException;

    public static class LoginResult implements IsSerializable {
        private boolean success;
        private Account account;

        public LoginResult() {
            this(false);
        }

        public LoginResult(final boolean success) {
            setSuccess(success);
        }


        public Account getAccount() {
            return account;
        }

        public void setAccount(final Account account) {
            this.account = account;
        }

        public void setSuccess(final boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public static class LoginException extends SerializableException {
        public LoginException() {
        }

        public LoginException(final String message) {
            super(message);
        }

        public LoginException(final String message, final Throwable t) {
            this(message);
        }
    }

    /**
     * Utility/Convinience class.
     * Use LoginService.App.getInstance() to access static instance of LoginServiceAsync
     */
    public static class App {
        private static LoginServiceAsync ourInstance = null;

        public static synchronized LoginServiceAsync getInstance() {
            if (ourInstance == null) {
                ourInstance = (LoginServiceAsync) GWT.create(LoginService.class);
                ((ServiceDefTarget) ourInstance).setServiceEntryPoint(GWT.getModuleBaseURL() + "login");
            }
            return ourInstance;
        }
    }
}
