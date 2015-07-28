/*
 * TransformBase.java
 *
 * Copyright (C) Roger Millington 2009-2010
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package fft;

/**
 * A base class for DFT agent implementations that transform real data and
 * providing convenience methods to deal with common data organisations.
 * <p>
 * For real data, C(s) = C'(N-s) so that only the first N/2 + 1 complex coefficients (0 to N/2)
 * are required to completely define the transform.
 * <p>
 * The result will (nearly) fit into the same space as the original data and if
 * one uses the first N locations of the original data to store the first N/2 complex terms then
 * has to find room for coeficient C(N/2)
 * <p>
 * In the DFT of real data, complex part of C(0) is always zero and the real part of C(N/2) is
 * always zero so this class stores the real part of C(N/2) in the imag part of C(0).
 * <p>
 * Obviously there has to be an even number of data points to get an integeral number of
 * (real, imag) pairs.
 * <p>
 * Several of the transform() methods take an 'offset' as a parameter. The
 * 'offset' is the index of the first data value in the array.
 * <p>
 * Also, several of the transform() methods take a 'stride' as a parameter. The
 * 'stride' the logical distance between two data values in an array. This means
 * that the data values are at indexes
 * 'offset', 'offset+stride', 'offset+2*stride', 'offset+3*stride' etc.
 * <p>
 * All the transform() methods are 'in place' in that the transformed values
 * replace the original values.
 * <p>
 * This class is thread safe and though derived classes may be thread safe, they
 * cannot be assumed thread safe.
 *
 * @author Roger Millington
 */
abstract public class RealTransformBase
{
    /**
     * Constructor.
     *
     * @param order the transform order.
     * @throws IllegalArgumentException if the order is not greater than 1 or
     * not a multiple of 2.
     */
    protected RealTransformBase(int order)
    {
        if (order < 2)
            throw new IllegalArgumentException("The transform order must be greater than one");
        if (order % 2 != 0)
            throw new IllegalArgumentException("The transform order must be a multiple of 2");
        order_ = order;
    }

    /**
     * Template method to compute the FFT for real data.
     * 
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param x the array containing the data.
     * @param offset the offset into the data of the first point.
     * @param stride the distance between data points.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    abstract public double transform(TransformDirection direction, final double[] x, int offset, int stride);

    /**
     * Performs the transform assuming that the data stored sequentially
     * with a stride of 1 and no offset.
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param x the array containing the data.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    public double transform(TransformDirection direction, final double[] x)
    {
        return transform(direction, x, 0);
    }

    /**
     * Performs the transform assuming that the data stored sequentially
     * with a stride of 1.
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param x the array containing the data.
     * @param offset the offset into the data array of the first point.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    public double transform(TransformDirection direction, final double[] x, int offset)
    {
        return transform(direction, x, offset, 1);
    }

    /**
     * Access method to get the transform order.
     *
     * @return the transform order.
     */
    public int getOrder()
    {
        return order_;
    }
    /**
     * The transform order
     */
    protected final int order_;
}
