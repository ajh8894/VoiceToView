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
 * A base class for DFT agent implementations providing convenience methods
 * to deal with common data organisations.
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
abstract public class ComplexTransformBase
{
    /**
     * Constructor.
     * 
     * @param order the transform order.
     * @throws IllegalArgumentException if the order is not greater than zero.
     */
    protected ComplexTransformBase(int order)
    {
        if (order < 1)
            throw new IllegalArgumentException("The transform order must be greater than zero");
        order_ = order;
    }

    /**
     * Abstract template method implementated by derived classes to perform the DFT and
     * used by the convenience methods implemented in this class.
     *
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param xr the array containing the real parts of the data points.
     * @param rOffset the offset into the array of real parts of the first real point.
     * @param xi the array containing the imaginary parts of the data points.
     * @param iOffset the offset into the array of imaginary parts of the first omaginary part.
     * @param stride the distance between the real and imaginary values.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    abstract public double transform(TransformDirection direction, double[] xr, int rOffset, double[] xi, int iOffset, int stride);

    /**
     * Convenience method to perform the DFT with the 'stride' defined as being
     * just 1.
     * <p>
     * The transformation is 'in place' in that the transformed values replace the
     * original values.
     *
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param xr the array containing the real parts of the data points.
     * @param rOffset the offset into the array of real parts of the first real point.
     * @param xi the array containing the imaginary parts of the data points.
     * @param iOffset the offset into the array of imaginary parts of the first omaginary part.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    public double transform(TransformDirection direction, double[] xr, int rOffset, double[] xi, int iOffset)
    {
        return transform(direction, xr, rOffset, xi, iOffset, 1);
    }

    /**
     * Convenience method to perform the DFT with both the real and imaginary values
     * being stored (alternating) in the same array but separated by a 'stride'.
     * <p>
     * The transformation is 'in place' in that the transformed values replace the
     * original values.
     *
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param x the array containing the data points.
     * @param offset the offset into the array of the first real point.
     * @param stride the distance between each of the real and imaginary values.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    public double transform(TransformDirection direction, double[] x, int offset, int stride)
    {
        return transform(direction, x, offset, x, offset + stride, 2 * stride);
    }

    /**
     * Convenience method to perform the DFT with both the real and imaginary values
     * being stored (alternating) in the same array with the stride being unity.
     * <p>
     * The transformation is 'in place' in that the transformed values replace the
     * original values.
     *
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param x the array containing the alternating (real, imag) parts of the data.
     * @param offset the offset into the array of the first data point.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    public double transform(TransformDirection direction, double[] x, int offset)
    {
        return transform(direction, x, offset, x, offset + 1, 2);
    }

    /**
     * Convenience method to compute the DFT with the real and imaginary data
     * values being stored in separate arrays.
     * <p>
     * The transformation is 'in place' in that the transformed values replace the
     * original values.
     *
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param xr the array containing the real data values.
     * @param xi the array containing the imaginary data values.
     * @param stride the distance between data points.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    public double transform(TransformDirection direction, double[] xr, double[] xi, int stride)
    {
        return transform(direction, xr, 0, xi, 0, stride);
    }

    /**
     * Convenience method to compute the DFT with the real and imaginary data
     * values being stored in separate arrays.
     * <p>
     * The transformation is 'in place' in that the transformed values replace the
     * original values.
     * 
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param xr the array containing the real data values.
     * @param xi the array containing the imaginary data values.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    public double transform(TransformDirection direction, double[] xr, double[] xi)
    {
        return transform(direction, xr, xi, 1);
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
