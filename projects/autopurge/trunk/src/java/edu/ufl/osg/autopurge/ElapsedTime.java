/*
 * This file is part of GatorMail AutoPurge, a tool to purge old messages.
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

package edu.ufl.osg.autopurge;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @see <a href="http://www.javaworld.com/javaworld/jw-03-2001/jw-0330-time.html">Working in Java time</a>
 */
class ElapsedTime {
    public static int getDays(final Calendar g1, final Calendar g2) {
        int elapsed = 0;
        final Calendar gc1;
        final Calendar gc2;
        if (g2.after(g1)) {
            gc2 = (Calendar)g2.clone();
            gc1 = (Calendar)g1.clone();
        } else {
            gc2 = (Calendar)g1.clone();
            gc1 = (Calendar)g2.clone();
        }
        gc1.clear(Calendar.MILLISECOND);
        gc1.clear(Calendar.SECOND);
        gc1.clear(Calendar.MINUTE);
        gc1.clear(Calendar.HOUR_OF_DAY);
        gc1.clear(Calendar.HOUR); // added by sandymac
        gc2.clear(Calendar.MILLISECOND);
        gc2.clear(Calendar.SECOND);
        gc2.clear(Calendar.MINUTE);
        gc2.clear(Calendar.HOUR_OF_DAY);
        gc2.clear(Calendar.HOUR); // added by sandymac
        while (gc1.before(gc2)) {
            gc1.add(Calendar.DATE, 1);
            elapsed++;
        }
        return elapsed;
    }

    public static int getMonths(final GregorianCalendar g1, final GregorianCalendar g2) {
        int elapsed = 0;
        final GregorianCalendar gc1;
        final GregorianCalendar gc2;
        if (g2.after(g1)) {
            gc2 = (GregorianCalendar)g2.clone();
            gc1 = (GregorianCalendar)g1.clone();
        } else {
            gc2 = (GregorianCalendar)g1.clone();
            gc1 = (GregorianCalendar)g2.clone();
        }
        gc1.clear(Calendar.MILLISECOND);
        gc1.clear(Calendar.SECOND);
        gc1.clear(Calendar.MINUTE);
        gc1.clear(Calendar.HOUR_OF_DAY);
        gc1.clear(Calendar.HOUR); // added by sandymac
        gc1.clear(Calendar.DATE);
        gc2.clear(Calendar.MILLISECOND);
        gc2.clear(Calendar.SECOND);
        gc2.clear(Calendar.MINUTE);
        gc2.clear(Calendar.HOUR_OF_DAY);
        gc2.clear(Calendar.HOUR); // added by sandymac
        gc2.clear(Calendar.DATE);
        while (gc1.before(gc2)) {
            gc1.add(Calendar.MONTH, 1);
            elapsed++;
        }
        return elapsed;
    }
}
