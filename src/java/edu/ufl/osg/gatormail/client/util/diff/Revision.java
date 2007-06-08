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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A Revision holds the series of deltas that describe the differences between
 * two sequences.
 *
 * @author <a href="mailto:juanco@suigeneris.org">Juanco Anez</a>
 * @author <a href="mailto:bwm@hplb.hpl.hp.com">Brian McBride</a>
 * @version $Revision: 1.1 $ $Date: 2006/03/12 00:24:21 $
 * @see Delta
 * @see Diff
 * @see Chunk
 */
public class Revision extends ToString {

    private List deltas = new ArrayList();

    /**
     * Creates an empty Revision.
     */
    public Revision() {
    }

    /**
     * Adds a delta to this revision.
     *
     * @param delta the {@link Delta Delta} to add.
     */
    public synchronized void addDelta(final Delta delta) {
        if (delta == null) {
            throw new IllegalArgumentException("new delta is null");
        }
        deltas.add(delta);
    }

    /**
     * Adds a delta to the start of this revision.
     *
     * @param delta the {@link Delta Delta} to add.
     */
    public synchronized void insertDelta(final Delta delta) {
        if (delta == null) {
            throw new IllegalArgumentException("new delta is null");
        }
        deltas.add(0, delta);
    }

    /**
     * Retrieves a delta from this revision by position.
     *
     * @param i the position of the delta to retrieve.
     * @return the specified delta
     */
    public Delta getDelta(final int i) {
        return (Delta)deltas.get(i);
    }

    /**
     * Returns the number of deltas in this revision.
     *
     * @return the number of deltas.
     */
    public int size() {
        return deltas.size();
    }

    /**
     * Applies the series of deltas in this revision as patches to the given
     * text.
     *
     * @param src the text to patch, which the method doesn't change.
     * @return the resulting text after the patches have been applied.
     * @throws PatchFailedException if any of the patches cannot be applied.
     */
    public Object[] patch(final Object[] src) throws PatchFailedException {
        final List target = new ArrayList(Arrays.asList(src));
        applyTo(target);
        return target.toArray();
    }

    /**
     * Applies the series of deltas in this revision as patches to the given
     * text.
     *
     * @param target the text to patch.
     * @throws PatchFailedException if any of the patches cannot be applied.
     */
    public synchronized void applyTo(final List target) throws PatchFailedException {
        final ListIterator i = deltas.listIterator(deltas.size());
        while (i.hasPrevious()) {
            final Delta delta = (Delta)i.previous();
            delta.patch(target);
        }
    }

    /**
     * Converts this revision into its Unix diff style string representation.
     *
     * @param s a {@link StringBuffer StringBuffer} to which the string
     *          representation will be appended.
     */
    public synchronized void toString(final StringBuffer s) {
        final Iterator i = deltas.iterator();
        while (i.hasNext()) {
            ((Delta)i.next()).toString(s);
        }
    }

    /**
     * Accepts a visitor.
     *
     * @param visitor the {@link RevisionVisitor} visiting this instance
     */
    public void accept(final RevisionVisitor visitor) {
        visitor.visit(this);
        final Iterator iter = deltas.iterator();
        while (iter.hasNext()) {
            ((Delta)iter.next()).accept(visitor);
        }
    }

}
