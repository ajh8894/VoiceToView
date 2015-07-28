/*
 * ComplexToReal.java
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
package fft;
/**
 * Converts an DFT of real data treated as complex i.e. (real,imag),(real,imag)... to
 * the DFT of real data.
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
 * Use of this routine can typically speed up the FFT of real data by a fact of 2.
 *
 * @author Roger Millington
 */
public class ComplexToReal
{
    /**
     * Constructs an agent that processes complex pairs (real,imag),(real,imag)...
     * obtained from a complex data FFT into the complex pairs of the equivalent
     * real data FFT.
     *
     * @param order the number of real data points transformed.
     */
    public ComplexToReal(int order)
    {
        if ((order & 1) != 0)
            throw new IllegalArgumentException("The order must be even");

        order_ = order;

        exr_ = new double[order / 2];
        exi_ = new double[exr_.length];

        double angle = Math.PI * 2.0 / order;
        for (int i = 0; i < exr_.length; i++)
        {
            exr_[i] = Math.cos(i * angle);
            exi_[i] = -Math.sin(i * angle);
        }
    }

    /*
     * Transforms the resultant (real,imag),(real,imaj)... pairs of the complex FFT
     * of the real data into the (real,imag),(real,imaj)... pairs of the result.
     * <p>
     * The real part of C(N/2) is stored in the location associated with the imag part of C(0).
     *
     * @param x the array containing the data to process.
     * @param offset the offset into array of the first point of data.
     * @param stride the distance between the points.
     * @return the scaling to be applied to bring the resultant transform
     * values back into line with the theoretical equations.
     */
    public double transform(final double[] x, int offset, int stride)
    {
        // Convert to real transform
        // First deal with zero and N/2
        final double x0r = x[offset] + x[offset + stride];    // Stores the real part of x[0]
        final double x0i = x[offset] - x[offset + stride];    // Stores the real part of x[n/2]

        x[offset] = x0r * 2;
        x[offset + stride] = x0i * 2;

        // Now the rest of the terms
        for (int i = 2 * stride + offset, j = (order_ - 2) * stride + offset, index = 1; i <= j; i += 2 * stride, j -= 2 * stride, index++)
        {
            // Compute w(s)
            final double wr = getReal(index);
            final double wi = getImag(index);

            // A(s) = T(s) + T'(N/2-s)
            final double ar = x[i] + x[j];
            final double ai = x[i + stride] - x[j + stride];

            // B(s) = (T(s) - T'(N/2-s))*exp(-j*2*p*s/N)/j
            final double bqr = x[i] - x[j];
            final double bqi = x[i + stride] + x[j + stride];
            final double bi = bqi * wi - bqr * wr;
            final double br = bqr * wi + bqi * wr;

            // Compute C(s) = A(s) + B(s)
            x[i] = ar + br;
            x[i + stride] = ai + bi;

            if (i != j)
            {
                // Compute C'(N/2-s) = A(s) - B(s)
                x[j] = ar - br;
                x[j + stride] = -(ai - bi);
            }
        }
        return 2.0;
    }

    /**
     * Performs the inverse of the transformation generated though
     * the transform() method.
     *
     * @param x the array containing the data to process.
     * @param offset the offset into array of the first point of data.
     * @param stride the distance between the points.
     */
    public double inverse(double[] x, int offset, int stride)
    {
        for (int i = 2 * stride + offset, j = (order_ - 2) * stride + offset, index = 1; i <= j; i += 2 * stride, j -= 2 * stride, index++)
        {
            // Compute w(s)
            final double wr = getReal(index);
            final double wi = getImag(index);

            // Compute A(s) & jB(s) from C(s)
            final double ar = x[i] + x[j];
            double ai = x[i + stride] - x[j + stride];

            // Compute A'(s) & jB'(s)
            final double bqr = x[i] - x[j];
            final double bqi = x[i + stride] + x[j + stride];
            final double bi = bqr * wr + bqi * wi;
            final double br = bqr * wi - bqi * wr;

            // Compute T'(s) = A(s) + jB(s)
            x[i] = ar + br;
            x[i + stride] = ai + bi;

            if (i != j)
            {
                // Compute T(N/2-s) = A(s) - jB(s)
                x[j] = ar - br;
                x[j + stride] = bi - ai;
            }
        }

        // Deal with zero
        final double x0r = x[offset] + x[offset + stride];
        final double x0i = x[offset] - x[offset + stride];

        x[offset] = x0r;
        x[offset + stride] = x0i;
        return 2.0;
    }

    /**
     * Returns the transform order.
     *
     * @return the transform order.
     */
    public int getOrder()
    {
        return this.order_;
    }

    private double getReal(int index)
    {
        return exr_[index];
    }

    private double getImag(int index)
    {
        return exi_[index];
    }
    // The Sin and Cos coefficients used in the transformation.
    protected final double[] exr_;  // Real part of exp(-j*2*pi*s/n)
    protected final double[] exi_;  // Imag part of exp(-j*2*pi*s/n)
    // The order of the transformation
    private final int order_;
}
