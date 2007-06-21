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

import java.util.Arrays;

/**
 * Implements a differencing engine that works on arrays of
 * {@link Object Object}.
 * <p/>
 * <p/>
 * Within this library, the word <i>text</i> means a unit of information
 * subject to version control.
 * <p/>
 * <p/>
 * Text is represented as <code>Object[]</code> because the diff engine is
 * capable of handling more than plain ascci. In fact, arrays of any type that
 * implements {@link java.lang.Object#hashCode hashCode()} and
 * {@link java.lang.Object#equals equals()} correctly can be subject to
 * differencing using this library.
 * </p>
 * <p/>
 * <p/>
 * This library provides a framework in which different differencing algorithms
 * may be used. If no algorithm is specififed, a default algorithm is used.
 * </p>
 *
 * @author <a href="mailto:juanco@suigeneris.org">Juanco Anez</a>
 * @version $Revision: 1.1 $ $Date: 2006/03/12 00:24:21 $
 * @see Delta
 * @see DiffAlgorithm
 */
public final class Diff extends ToString {
    /**
     * The standard line separator.
     */
    static final String NL = "\n";

    /**
     * The line separator to use in RCS format output.
     */
    static final String RCS_EOL = "\n";

    /**
     * The original sequence.
     */
    private final Object[] orig;

    /**
     * The differencing algorithm to use.
     */
    private DiffAlgorithm algorithm;

    /**
     * Create a differencing object using the default algorithm
     *
     * @param original the
     *                 original text that will be compared
     */
    public Diff(final Object[] original) {
        this(original, null);
    }

    /**
     * Create a differencing object using the given algorithm
     *
     * @param original  the original text which will be compared against
     * @param algorithm the difference algorithm to use.
     */
    public Diff(final Object[] original, final DiffAlgorithm algorithm) {
        if (original == null) {
            throw new IllegalArgumentException();
        }

        this.orig = original;
        if (algorithm != null)
            this.algorithm = algorithm;
        else
            this.algorithm = defaultAlgorithm();
    }

    private DiffAlgorithm defaultAlgorithm() {
        return new SimpleDiff();
    }

    /**
     * compute the difference between an original and a revision.
     *
     * @param orig the original
     * @param rev  the revision to compare with the original.
     * @return a Revision describing the differences
     */
    public static Revision diff(final Object[] orig, final Object[] rev)
            throws DifferentiationFailedException {
        if (orig == null || rev == null) {
            throw new IllegalArgumentException();
        }

        return diff(orig, rev, null);
    }

    /**
     * compute the difference between an original and a revision.
     *
     * @param orig      the original
     * @param rev       the revision to compare with the original.
     * @param algorithm the difference algorithm to use
     * @return a Revision describing the differences
     */
    public static Revision diff(final Object[] orig, final Object[] rev,
                                final DiffAlgorithm algorithm) throws DifferentiationFailedException {
        if (orig == null || rev == null) {
            throw new IllegalArgumentException();
        }

        return new Diff(orig, algorithm).diff(rev);
    }

    /**
     * compute the difference between the original and a revision.
     *
     * @param rev the revision to compare with the original.
     * @return a Revision describing the differences
     */
    public Revision diff(final Object[] rev) throws DifferentiationFailedException {
        if (orig.length == 0 && rev.length == 0)
            return new Revision();
        else
            return algorithm.diff(orig, rev);
    }

    /**
     * Compares the two input sequences.
     *
     * @param orig The original sequence.
     * @param rev  The revised sequence.
     * @return true if the sequences are identical. False otherwise.
     */
    public static boolean compare(final Object[] orig, final Object[] rev) {
        if (orig.length != rev.length) {
            return false;
        } else {
            for (int i = 0; i < orig.length; i++) {
                if (!orig[i].equals(rev[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(final String[] args) throws DifferentiationFailedException {
        final Number[] a = new Number[]{new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Integer(6), new Integer(7), new Integer(8), new Integer(9)};
        final Number[] b = new Number[]{new Integer(1), new Integer(4), new Integer(-5), new Float(5.1f), new Integer(6), new Integer(7), new Integer(8), new Integer(9), new Integer(0)};
        final Diff diff = new Diff(a);
        final Revision revision = diff.diff(b);

        System.out.println("a: " + Arrays.asList(a));
        System.out.println("b: " + Arrays.asList(b));
        System.out.println(revision);

        for (int d = 0; d < revision.size(); d++) {
            final Delta delta = revision.getDelta(d);
            if (delta instanceof DeleteDelta) {
                final DeleteDelta deleteDelta = (DeleteDelta)delta;
                final Chunk original = deleteDelta.getOriginal();
                System.out.println("DELETE: " + original.first() + "," + (original.last() + 1));
                final Chunk revised = deleteDelta.getRevised();
                System.out.println("      : " + revised.first() + "," + (revised.last() + 1));
                System.out.println("D: " + revised.first() + "," + (revised.first() + original.size()));
            } else if (delta instanceof ChangeDelta) {
                final ChangeDelta changeDelta = (ChangeDelta)delta;
                final Chunk original = changeDelta.getOriginal();
                System.out.println("CHANGED: " + original.first() + "," + (original.last() + 1));
                final Chunk revised = changeDelta.getRevised();
                System.out.println("       : " + revised.first() + "," + (revised.last() + 1));
                if (changeDelta.getOriginal().size() == changeDelta.getRevised().size()) {
                    System.out.println("C: " + revised.first() + "," + (revised.first() + original.size()));
                } else {
                    System.out.println("D: " + revised.first() + "," + (revised.first() + original.size()));
                    System.out.println("A: " + revised.first() + "," + (revised.first() + revised.size()));
                }
            } else if (delta instanceof AddDelta) {
                final AddDelta addDelta = (AddDelta)delta;
                final Chunk original = addDelta.getOriginal();
                System.out.println("ADDED: " + original.first() + "," + (original.last() + 1));
                final Chunk revised = addDelta.getRevised();
                System.out.println("     : " + revised.first() + "," + (revised.last() + 1));
                System.out.println("A: " + revised.first() + "," + (revised.first() + revised.size()));
            }
        }

    }

}