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

package edu.ufl.osg.gatormail.server.state;

import java.io.Serializable;

/**
 * Interface for an object that can encrypt and decrypt session state that is in the clients
 * posession but should be kept private from them.
 *
 * <p>
 * This should be saved under the
 * {@link javax.servlet.ServletConfig#getServletContext() servlet context} under the
 * PrivateStateCipher full class name. eg:
 * <code>PrivateStateCipher psc = (PrivateStateCipher)getServletContext().getAttribute(PrivateStateCipher.class.getName())</code> 
 * </p>
 *
 * @author Sandy McArthur
 */
public interface PrivateStateCipher {
    /**
     * Take an Object and serialize and encrypt and encode it so it is opaque to the client.
     *
     * <p>
     * The returned String must be in a form that is safe to make it part of a URL.
     * This means no new lines or other special charactgers that need to be esapced.
     * The Base64 URL-Safe dialect described in RFC3548 is suggested.
     * </p>
     *
     * <p>
     * Note: Just because <code>obj</code> must implement Serializable does not mean
     * you are limited to the normal serialization methods.
     * </p>
     *
     * @param obj the object to be encoded and serialized.
     * @return an encoded String that is safe to be part of a URL.
     */
    String encode(Serializable obj);

    /**
     * 
     *
     * @param encodedToken an encoded token from the encode method.
     * @return the unserialized object from <code>encodedToken</code>.
     */
    Object decode(String encodedToken);
}
