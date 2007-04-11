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

/**
 * <code>multipart/alternative</code> part.
 *
 * <p>
 * The "multipart/alternative" type is syntactically identical to
 * "multipart/mixed", but the semantics are different. In particular,
 * each of the body parts is an "alternative" version of the same
 * information.
 * </p>
 * <p>
 * Systems should recognize that the content of the various parts are
 * interchangeable.  Systems should choose the "best" type based on the
 * local environment and references, in some cases even through user
 * interaction.  As with "multipart/mixed", the order of body parts is
 * significant.  In this case, the alternatives appear in an order of
 * increasing faithfulness to the original content.  In general, the
 * best choice is the LAST part of a type supported by the recipient
 * system's local environment.
 * </p>
 *
 * @author Sandy McArthur
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2045.txt">RFC 2045</a>
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2046.txt">RFC 2046</a>
 */
public class GMAlternative extends GMMixed {
}
