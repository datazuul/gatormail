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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFolder;

/**
 * RPC service methods to fetch information about a folder.
 *
 * @author Sandy McArthur
 */
public interface FoldersService extends RemoteService {

    public GMFolder updateFolder(Account account, GMFolder folder) throws SerializableException;

    /**
     * TODO? move this into the Account object?
     */
    public String[] getRootFolders(Account account) throws SerializableException;

    /**
     * Utility/Convinience class.
     * Use FoldersService.App.getInstance() to access static instance of FoldersServiceAsync
     */
    public static class App {
        private static FoldersServiceAsync ourInstance = null;

        public static synchronized FoldersServiceAsync getInstance() {
            if (ourInstance == null) {
                ourInstance = (FoldersServiceAsync) GWT.create(FoldersService.class);
                ((ServiceDefTarget) ourInstance).setServiceEntryPoint(GWT.getModuleBaseURL() + "folders");
            }
            return ourInstance;
        }
    }
}
