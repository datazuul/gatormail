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
/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package edu.ufl.osg.gatormail.client.util.diff;

import java.util.List;

/**
 * Holds an change-delta between to revisions of a text.
 *
 * @author <a href="mailto:juanco@suigeneris.org">Juanco Anez</a>
 * @version $Id: ChangeDelta.java,v 1.1 2006/03/12 00:24:21 juanca Exp $
 * @see Delta
 * @see Diff
 * @see Chunk
 */
public class ChangeDelta extends Delta {

    ChangeDelta() {
        super();
    }

    public ChangeDelta(final Chunk orig, final Chunk rev) {
        init(orig, rev);
    }

    public void verify(final List target) throws PatchFailedException {
        if (!original.verify(target)) {
            throw new PatchFailedException();
        }
        if (original.first() > target.size()) {
            throw new PatchFailedException("original.first() > target.size()");
        }
    }

    public void applyTo(final List target) {
        original.applyDelete(target);
        revised.applyAdd(original.first(), target);
    }

    public void toString(final StringBuffer s) {
        original.rangeString(s);
        s.append("c");
        revised.rangeString(s);
        s.append(Diff.NL);
        original.toString(s, "< ", "\n");
        s.append("---");
        s.append(Diff.NL);
        revised.toString(s, "> ", "\n");
    }

    public void toRCSString(final StringBuffer s, final String EOL) {
        s.append("d");
        s.append(original.rcsfrom());
        s.append(" ");
        s.append(original.size());
        s.append(EOL);
        s.append("a");
        s.append(original.rcsto());
        s.append(" ");
        s.append(revised.size());
        s.append(EOL);
        revised.toString(s, "", EOL);
    }

    public void accept(final RevisionVisitor visitor) {
        visitor.visit(this);
    }
}
