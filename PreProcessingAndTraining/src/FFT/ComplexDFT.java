/*
 * ComplexDFT.java
 *
 * Copyright (C) Roger Millington 2006-2010
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
package FFT;

/**
 * Actor to perform the Discrete Fourier Transform for complex data using
 * the naive algorithm. The Discrete Fourier Transform of
 * a sequence of 'n' complex points is define as
 * <blockquote>
 * <pre>
 *          n-1
 * c(s) = sum( x(r) exp(-j*2*pi*r*s/n) )
 *          r=0
 * </pre>
 * </blockquote>
 * with the inverse transform being given by
 * <blockquote>
 * <pre>
 *                n-1
 * x(r) = (1/n) sum( c(s) exp(j*2*pi*r*s/n) )
 *                s=0
 * </pre>
 * </blockquote>
 * where s,r within 0..n-1 .
 * <p>
 * The naive algorithm is very very slow and has been implemented only
 * as a base line to test the Fast Fourier Transforms (FFT) implementations against.
 *
 * @author Roger Millington
 */
public class ComplexDFT extends ComplexTransformBase
{
    /**
     * Constucts a ComplexDFT instance for a given order.
     * <p>
     * Note that, since it uses instance variable temporary arrays, this
     * class is not thread safe.
     *
     * @param order the number of points to be transformed.
     */
    public ComplexDFT(int order)
    {
        super(order);

        cosf_ = new double[order];
        sinf_ = new double[order];
        xr_ = new double[order];
        xi_ = new double[order];

        double theta = -2.0 * Math.PI / order;
        for (int r = 0; r < order; r++)
        {
            cosf_[r] = Math.cos(theta * r);
            sinf_[r] = Math.sin(theta * r);
        }
    }

    /**
     * Computes the 'in place' DFT of a set of complex data points.
     * 
     * @param direction the direction (Forward or Inverse) of the transform.
     * @param xr the array containing the real parts of the data points.
     * @param rOffset the offset into the real parts array of the first point.
     * @param xi the array containing the imaginary parts of the data points
     * @param iOffset the offset into the imaginary parts array of the first point.
     * @param stride the distance between the points.
     * @return the scaling factor to be applied to each point to bring it back
     * into line with the theoretcal value.
     */
    public double transform(TransformDirection direction, double[] xr, int rOffset, double[] xi, int iOffset, int stride)
    {
        boolean inverse = direction == TransformDirection.Inverse;
        for (int s = 0; s < order_; s++)
        {
            xr_[s] = xr[s * stride + rOffset];
            xi_[s] = xi[s * stride + iOffset];
        }

        for (int s = 0; s < order_; s++)
        {
            double sr = 0.0;
            double si = 0.0;
            for (int r = 0, rs = 0; r < order_; r++, rs = (rs + s) % order_)
            {
                final double cosf = cosf_[rs];
                final double sinf = inverse ? -sinf_[rs] : sinf_[rs];
                sr += xr_[r] * cosf - xi_[r] * sinf;
                si += xr_[r] * sinf + xi_[r] * cosf;
            }
            xr[s * stride + rOffset] = sr;
            xi[s * stride + iOffset] = si;
        }
        return inverse ? order_ : 1.0;
    }
    //
    private double[] xr_;
    private double[] xi_;
    private final double[] cosf_;
    private final double[] sinf_;
}
