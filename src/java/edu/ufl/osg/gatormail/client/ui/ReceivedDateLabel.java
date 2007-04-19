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

import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;

import java.util.Date;

/**
 * Date of when a message was received.
 *
 * @author Sandy McArthur
 */
public class ReceivedDateLabel extends DateLabel {

    public ReceivedDateLabel(final GMMessage message) {
        super(message);
    }

    protected Date getDate() {
        final GMMessageHeaders headers = getMessage().getHeaders();
        if (headers != null) {
            return headers.getReceivedDate();
        }
        return null;
    }
}
