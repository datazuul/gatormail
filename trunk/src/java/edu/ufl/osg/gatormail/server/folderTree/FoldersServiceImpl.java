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

package edu.ufl.osg.gatormail.server.folderTree;

import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.mail.imap.IMAPFolder;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.services.FoldersService;
import edu.ufl.osg.gatormail.server.MessageServiceImpl;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.URLName;

/**
 * Basic implementation of {@link edu.ufl.osg.gatormail.client.services.FoldersService}.
 *
 * @author Sandy McArthur
 */
public class FoldersServiceImpl extends RemoteServiceServlet implements FoldersService {

    public GMFolder updateFolder(final Account account, final GMFolder gmFolder) throws SerializableException {
        final Session session = MessageServiceImpl.fetchSession(account);
        final Store store = MessageServiceImpl.fetchConnectedStore(session);

        final Folder folder;
        try {
            folder = store.getFolder(gmFolder.getFullName());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        final String urlName;
        try {
            urlName = folder.getURLName().toString();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        //final GMFolder gmFolder = new GMFolder();

        gmFolder.setUrlName(urlName);
        gmFolder.setName(folder.getName());
        gmFolder.setFullName(folder.getFullName());

        if (folder instanceof UIDFolder) {
            final UIDFolder uidFolder = (UIDFolder)folder;
            try {
                gmFolder.setUidValidity(uidFolder.getUIDValidity());
            } catch (MessagingException e) {
                e.printStackTrace();
                //throw new SerializableException(e.getMessage());
            }
        }

        try {
            if (!folder.exists()) {
                throw new SerializableException("Folder " + gmFolder + " does not exist!");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        int type = 0;
        try {
            type = folder.getType();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        gmFolder.setHoldsFolders((type & IMAPFolder.HOLDS_FOLDERS) != 0);
        gmFolder.setHoldsMessages((type & IMAPFolder.HOLDS_MESSAGES) != 0);

        try {
            gmFolder.setType(folder.getType());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            gmFolder.setSeparator(folder.getSeparator());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            for (final javax.mail.Folder subFolder : folder.list()) {
                gmFolder.addSubFolder(subFolder.getURLName().toString());
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        if (!gmFolder.isHoldsMessages()) {
            // TODO init rest of props to zero.
            return gmFolder;
        }

        try {
            gmFolder.setMessageCount(folder.getMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        try {
            gmFolder.setNewMessageCount(folder.getNewMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        try {
            gmFolder.setUnreadMessageCount(folder.getUnreadMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        try {
            gmFolder.setDeletedMessageCount(folder.getDeletedMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        return gmFolder;
    }

    public GMFolder getFolderInfo(final Account account, final String folderFullName) throws SerializableException {
        final Session session = MessageServiceImpl.fetchSession(account);
        final Store store = MessageServiceImpl.fetchConnectedStore(session);

        final Folder folder;
        try {
            System.err.println(folderFullName);
            //folder = store.getFolder(folderFullName);
            folder = store.getFolder(new URLName(folderFullName));
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        final String urlName;
        try {
            urlName = folder.getURLName().toString();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        final GMFolder gmFolder = new GMFolder();

        gmFolder.setUrlName(urlName);
        gmFolder.setName(folder.getName());
        gmFolder.setFullName(folder.getFullName());

        try {
            if (!folder.exists()) {
                throw new SerializableException("Folder " + folderFullName + " does not exist!");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        int type = 0;
        try {
            type = folder.getType();
            gmFolder.setType(type);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        gmFolder.setHoldsFolders((type & IMAPFolder.HOLDS_FOLDERS) != 0);
        gmFolder.setHoldsMessages((type & IMAPFolder.HOLDS_MESSAGES) != 0);


        if (gmFolder.isHoldsMessages() && folder instanceof UIDFolder) {
            final UIDFolder uidFolder = (UIDFolder)folder;
            try {
                gmFolder.setUidValidity(uidFolder.getUIDValidity());
            } catch (MessagingException e) {
                System.err.println(folder);
                e.printStackTrace();
                //throw new SerializableException(e.getMessage());
            }
        }

        try {
            gmFolder.setSeparator(folder.getSeparator());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        try {
            for (final javax.mail.Folder subFolder : folder.list()) {
                gmFolder.addSubFolder(subFolder.getURLName().toString());
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        
        if (!gmFolder.isHoldsMessages()) {
            // TODO init rest of props to zero.
            return gmFolder;
        }

        try {
            gmFolder.setMessageCount(folder.getMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        try {
            gmFolder.setNewMessageCount(folder.getNewMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        try {
            gmFolder.setUnreadMessageCount(folder.getUnreadMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        try {
            gmFolder.setDeletedMessageCount(folder.getDeletedMessageCount());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        return gmFolder;
    }

    public String[] getRootFolders(final Account account) throws SerializableException {
        final Session session = MessageServiceImpl.fetchSession(account);
        final Store store = MessageServiceImpl.fetchConnectedStore(session);

        final Folder defaultFolders;
        try {
            defaultFolders = store.getDefaultFolder();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }

        final Folder[] rootFolders;
        try {
            //rootFolders = defaultFolders.list();
            rootFolders = defaultFolders.listSubscribed();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SerializableException(e.getMessage());
        }
        final String[] folders = new String[rootFolders.length];
        for (int i=0; i < rootFolders.length; i++) {
            try {
                folders[i] = rootFolders[i].getURLName().toString();
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new SerializableException(e.getMessage());
            }
        }
        return folders;
    }


}