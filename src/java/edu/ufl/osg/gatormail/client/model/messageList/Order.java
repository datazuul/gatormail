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

package edu.ufl.osg.gatormail.client.model.messageList;

import java.io.Serializable;

/**
 * The sort order of messages in a folder.
 *
 * @author Sandy McArthur
 */
public class Order implements Serializable {
    public static final Order NATURAL = new Order(0);
    public static final Order SENT = new Order(1);
    public static final Order RECEIVED = new Order(2);
    public static final Order FROM = new Order(3);
    public static final Order SUBJECT = new Order(4);

    private int order;

    public Order() {
        this(NATURAL.order);
    }

    public Order(final int order) {
        this.order = order;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o instanceof Order) {
            final Order order = (Order)o;
            return this.order == order.order;
        }
        return false;
    }

    public int hashCode() {
        return order;
    }

    public String toString() {
        String s = "Order{" +
                "order=";
        switch (this.order) {
            case 0:
                s += "NATURAL";
                break;
            case 1:
                s += "SENT";
                break;
            case 2:
                s += "RECEIVED";
                break;
            case 3:
                s += "FROM";
                break;
            case 4:
                s += "SUBJECT";
                break;
            default:
                throw new IllegalStateException("Order type " + order + " not named!");
        }
        s += '}';
        return s;
    }
}
