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

package edu.ufl.osg.gatormail.client.ui.messageList;

import com.google.gwt.core.client.JavaScriptObject;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;

/**
 * Cache of {@link edu.ufl.osg.gatormail.client.model.message.GMMessage}s keyed by uid.
 *
 * @author Sandy McArthur
 */
final class MessageCache {

   private final JavaScriptObject cacheJSO;

    private final GatorMailWidget client;

    /**
     * All messages in this cacheMap must belong to this folder.
     */
    private final GMFolder folder;

    public MessageCache(final GatorMailWidget client, final GMFolder folder) {
        this.client = client;
        this.folder = folder;

        cacheJSO = createJSOArray();
    }

    public GMMessage getMessage(final long uid) {
        GMMessage message = get(uid);
        if (message == null) {
            message = new GMMessage();
            message.setFolder(folder);
            message.setUid(uid);
            put(uid, message);
        }
        return message;
    }

    public void removeMessage(final GMMessage message) {
        remove(message.getUid());
    }

    public void prune() {

    }

    private static native JavaScriptObject createJSOArray() /*-{
        return [];
    }-*/;

    private native GMMessage get(final long uid) /*-{
        var cache = this.@edu.ufl.osg.gatormail.client.ui.messageList.MessageCache::cacheJSO;
        return cache[uid] || null;
    }-*/;

    private native void put(final long uid, final GMMessage message) /*-{
        var cache = this.@edu.ufl.osg.gatormail.client.ui.messageList.MessageCache::cacheJSO;
        cache[uid] = message;
    }-*/;

    private native void remove(final long uid) /*-{
        var cache = this.@edu.ufl.osg.gatormail.client.ui.messageList.MessageCache::cacheJSO;
        cache[uid] = null;
    }-*/;
}
