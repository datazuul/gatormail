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
 * Holds a "delta" difference between to revisions of a text.
 *
 * @author <a href="mailto:juanco@suigeneris.org">Juanco Anez</a>
 * @author <a href="mailto:bwm@hplb.hpl.hp.com">Brian McBride</a>
 * @version $Revision: 1.1 $ $Date: 2006/03/12 00:24:21 $
 * @see Diff
 * @see Chunk
 * @see Revision
 *      <p/>
 *      modifications
 *      <p/>
 *      27 Apr 2003 bwm
 *      <p/>
 *      Added getOriginal() and getRevised() accessor methods Added visitor pattern
 *      accept() method
 */

public abstract class Delta extends ToString {

    protected Chunk original;

    protected Chunk revised;

    /**
     * Returns a Delta that corresponds to the given chunks in the original and
     * revised text respectively.
     *
     * @param orig the chunk in the original text.
     * @param rev  the chunk in the revised text.
     */
    public static Delta newDelta(final Chunk orig, final Chunk rev) {
        final Delta result;
        if (orig.size() > 0) {
            // [1]
            if (rev.size() > 0) {
                // [1]
                result = new ChangeDelta();
            } else {
                // [0]
                result = new DeleteDelta();
            }
        } else {
            // [0]
            if (rev.size() > 0) {
                // [1]
                result = new AddDelta();
            } else {
                // [0]
                result = new ChangeDelta();
            }
        }
        result.init(orig, rev);
        return result;
    }

    /**
     * Creates an uninitialized delta.
     */
    protected Delta() {
    }

    /**
     * Creates a delta object with the given chunks from the original and
     * revised texts.
     */
    protected Delta(final Chunk orig, final Chunk rev) {
        init(orig, rev);
    }

    /**
     * Initializaes the delta with the given chunks from the original and
     * revised texts.
     */
    protected void init(final Chunk orig, final Chunk rev) {
        original = orig;
        revised = rev;
    }

    /**
     * Verifies that this delta can be used to patch the given text.
     *
     * @param target the text to patch.
     * @throws PatchFailedException if the patch cannot be applied.
     */
    public abstract void verify(List target) throws PatchFailedException;

    /**
     * Applies this delta as a patch to the given text.
     *
     * @param target the text to patch.
     * @throws PatchFailedException if the patch cannot be applied.
     */
    public final void patch(final List target) throws PatchFailedException {
        verify(target);
        try {
            applyTo(target);
        }
        catch (Exception e) {
            throw new PatchFailedException(e.getMessage());
        }
    }

    /**
     * Applies this delta as a patch to the given text.
     *
     * @param target the text to patch.
     * @throws PatchFailedException if the patch cannot be applied.
     */
    public abstract void applyTo(List target);

    /**
     * Converts this delta into its Unix diff style string representation.
     *
     * @param s a {@link StringBuffer StringBuffer} to which the string
     *          representation will be appended.
     */
    public void toString(final StringBuffer s) {
        original.rangeString(s);
        s.append("x");
        revised.rangeString(s);
        s.append(Diff.NL);
        original.toString(s, "> ", "\n");
        s.append("---");
        s.append(Diff.NL);
        revised.toString(s, "< ", "\n");
    }

    /**
     * Converts this delta into its RCS style string representation.
     *
     * @param s   a {@link StringBuffer StringBuffer} to which the string
     *            representation will be appended.
     * @param EOL the string to use as line separator.
     */
    public abstract void toRCSString(StringBuffer s, String EOL);

    /**
     * Accessor method to return the chunk representing the original sequence of
     * items
     *
     * @return the original sequence
     */
    public Chunk getOriginal() {
        return original;
    }

    /**
     * Accessor method to return the chunk representing the updated sequence of
     * items.
     *
     * @return the updated sequence
     */
    public Chunk getRevised() {
        return revised;
    }

    /**
     * Accepts a visitor.
     * <p/>
     * See the Visitor pattern in "Design Patterns" by the GOF4.
     *
     * @param visitor The visitor.
     */
    public abstract void accept(RevisionVisitor visitor);
}
