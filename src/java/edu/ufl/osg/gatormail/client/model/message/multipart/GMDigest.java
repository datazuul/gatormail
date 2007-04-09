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

package edu.ufl.osg.gatormail.client.model.message.multipart;

import edu.ufl.osg.gatormail.client.model.message.GMMessagePart;

/**
 * <code>multipart/digest</code> part.
 *
 * <p>
 * This type is syntactically identical to "multipart/mixed", but
 * the semantics are different.  In particular, in a digest, the default
 * Content-Type value for a body part is changed from "text/plain" to
 * "message/rfc822".  This is done to allow a more readable digest
 * format that is largely compatible (except for the quoting convention)
 * with RFC 934.
 * </p>
 *
 * @author Sandy McArthur
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2045.txt">RFC 2045</a>
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2046.txt">RFC 2046</a>
 */
public class GMDigest extends GMMixed {
}
