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
 * A container object for the order and filter associated with a list of messages.
 *
 * @author Sandy McArthur
 */
public class Prescript implements Serializable {
    private Order order;
    private Filter filter;

    public Prescript() {
        this(Order.NATURAL, Filter.ALL);
    }

    public Prescript(final Order order, final Filter filter) {
        this.filter = filter;
        this.order = order;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(final Filter filter) {
        this.filter = filter;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(final Order order) {
        this.order = order;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o instanceof Prescript) {
            final Prescript prescript = (Prescript)o;
            return filter.equals(prescript.filter) && order.equals(prescript.order);
        }
        return false;
    }

    public int hashCode() {
        int result;
        result = order.hashCode();
        result = 31 * result + filter.hashCode();
        return result;
    }

    public String toString() {
        return "Prescript{" + "order=" + order + ", filter=" + filter + '}';
    }
}
