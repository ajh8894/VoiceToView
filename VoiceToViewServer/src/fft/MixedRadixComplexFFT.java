/*
 * MixedRadixComplexFFT.java
 *
 * Created on October 3, 2006, 11:50 AM
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
 * Actor to perform the Discrete Fourier Transform for complex data using
 * the Fast Fourier Transform algorithm. The Discrete Fourier Transform of
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
 *               s-1
 * x(r) = (1/n) sum( c(s) exp(j*2*pi*r*s/n) )
 *               s=0
 * </pre>
 * </blockquote>
 * where s,r within 0..n-1 .
 * <p>
 * There are numerious books and articles on the algorithm. If the
 * reader is really interested then I refer him to the original reference
 * <pre>
 * James W. Cooley and John W. Tukey
 * An Algorithm for the Machine Calculation of Complex Fourier Series
 * Mathematics of Computation, Vol. 19, April 1965
 * </pre>
 *
 * @author Roger Millington
 */
public class MixedRadixComplexFFT extends ComplexTransformBase
{
    /**
     * Construct an FFT agent for a given order.
     * <p>
     * The number of points can only have factors of 2, 3 or 5.
     *
     * @param order the number of complex points pairs to transform.
     */
    public MixedRadixComplexFFT(int order)
    {
        super(order);

        if ((order < 2) || (order > 32768))
            throw new IllegalArgumentException("The 'order'is " + order + " but must be in [2,32768]");

        // Find the factors
        int remainder = order;
        for (int validFactorsIndex = 0; validFactorsIndex < validFactors_.length; validFactorsIndex++)
        {
            int factor = validFactors_[validFactorsIndex];
            for (int temp = 0; (temp = remainder / factor) * factor == remainder; remainder = temp)
            {
                factors_[numberOfFactor_++] = factor;
            }
        }
        factors_[numberOfFactor_] = 1;

        // If we have anything remaining then the number of points is not valid.
        if (remainder != 1)
            throw new IllegalArgumentException("The 'order' must only have factors of 2,3 and 5.");

        reorderChains_ = buildReorderChains();

        // Arrays for twiddle factors
        exr_ = new double[order / 2 + 1];
        exi_ = new double[exr_.length];

        // Compute the twiddle factors
        final double angle = Math.PI * 2.0 / order;
        for (int i = 0; i < exr_.length; i++)
        {
            exr_[i] = Math.cos(i * angle);
            exi_[i] = -Math.sin(i * angle);
        }
    }

    private short[][] buildReorderChains()
    {
        // Build the resort information
        int[] sproducts = new int[numberOfFactor_];
        int[] rproducts = new int[numberOfFactor_];
        for (int factorIndex = 0, u = 1; factorIndex < numberOfFactor_; factorIndex++)
        {
            sproducts[factorIndex] = u;
            u *= factors_[numberOfFactor_ - factorIndex - 1];
            rproducts[factorIndex] = order_ / u;
        }

        // Build a list of chains that define the re-order of the coefficients
        boolean[] visited = new boolean[order_];
        java.util.List<short[]> reorderChainsList = new java.util.ArrayList<short[]>();

        for (int i = 1, n = order_ - 1; i < n; i++)
        {
            if (!visited[i])
            {
                // Work out the chain length
                int chainLength = 1;
                visited[i] = true;
                for (int j = next(i, rproducts, sproducts); j != i; j = next(j, rproducts, sproducts))
                {
                    chainLength++;
                    visited[j] = true;
                }

                if (chainLength > 1)
                {
                    short[] chain = new short[chainLength];
                    int index = 0;
                    chain[index++] = (short) i;
                    for (int j = next(i, rproducts, sproducts); j != i; j = next(j, rproducts, sproducts))
                    {
                        chain[index++] = (short) j;
                    }
                    reorderChainsList.add(chain);
                }
            }
        }
        return reorderChainsList.toArray(new short[0][0]);
    }

    /**
     * Perform an FFT on separate real and imaginary arrays with
     * different starting points into the arrays.
     * 
     * @param xr the array containing the real data points.
     * @param rOffset the offset into the real array of the first point
     * @param xi the array containing the imaginary data poinst.
     * @param iOffset the offset into the imaginary array of the first point.
     * @param stride the distance between points.
     * @return the scaling factor to be applied to each value to make it match
     * the theoretical DFT equation.
     */
    public double transform(TransformDirection direction, final double[] xr, final int rOffset, final double[] xi, final int iOffset, final int stride)
    {
        final boolean inverse = direction == TransformDirection.Inverse;
        final double sin60 = inverse ? -sin60_ : sin60_;
        final double c1i = inverse ? -c1i_ : c1i_;
        final double c2i = inverse ? -c2i_ : c2i_;

        int nb = 1;
        int np = order_ * stride;
        int n = np;

        for (int factorIndex = 0; factorIndex < numberOfFactor_; factorIndex++)
        {
            // Get the next factor to process
            final int factor = factors_[factorIndex];

            if (factor == 2)
            {
                // ---------------------------------------------
                // Radix 2 reduction
                // ---------------------------------------------
                final int m = n / 2;

                // For each element in a block
                for (int blockIndex = 0, twiddleIndex = 0; blockIndex < m; blockIndex += stride, twiddleIndex += nb)
                {
                    // Local variables for the twiddle factors
                    final double w1r = exr(twiddleIndex);
                    final double w1i = exi(twiddleIndex, inverse);

                    // For each block
                    for (int blockStart = blockIndex; blockStart < np; blockStart += n)
                    {
                        // Index variables with offset added in
                        final int i0r = blockStart + rOffset;
                        final int i1r = i0r + m;
                        final int i0i = blockStart + iOffset;
                        final int i1i = i0i + m;

                        // x[j] = x[j] + x[k]
                        double cr = xr[i0r];
                        double ci = xi[i0i];

                        xr[i0r] = cr + xr[i1r];
                        xi[i0i] = ci + xi[i1i];

                        // x[k] = (x[j] - x[k])*w
                        cr -= xr[i1r];
                        ci -= xi[i1i];
                        xr[i1r] = (cr * w1r - ci * w1i);
                        xi[i1i] = (cr * w1i + ci * w1r);
                    }
                }

                nb = nb * 2;    // Number of blocks
                n = m;          // Points per block
            } else if (factor == 3)
            {
                // ---------------------------------------------
                // Radix 3 reduction
                // ---------------------------------------------
                final int m = n / 3;

                // For each element in a block
                for (int i = 0, kk = 0; i < m; i += stride, kk += nb)
                {
                    // Local variables for the twiddle factors
                    final double w1r = exr(kk);
                    final double w1i = exi(kk, inverse);
                    final double w2r = exr(2 * kk);
                    final double w2i = exi(2 * kk, inverse);

                    // For each block
                    for (int blockStart = i; blockStart < np; blockStart += n)
                    {
                        // Index
                        final int i0r = blockStart + rOffset;
                        final int i1r = i0r + m;
                        final int i2r = i1r + m;
                        final int i0i = blockStart + iOffset;
                        final int i1i = i0i + m;
                        final int i2i = i1i + m;

                        // Saved values
                        double cr = xr[i0r];
                        double ci = xi[i0i];
                        double ar = xr[i1r] + xr[i2r];
                        double ai = xi[i1i] + xi[i2i];

                        // x[j] = x[j] + x[k] + x[l]
                        xr[i0r] = cr + ar;
                        xi[i0i] = ci + ai;

                        final double bi = (xr[i2r] - xr[i1r]) * sin60;
                        final double br = (xi[i1i] - xi[i2i]) * sin60;

                        cr = cr - ar * cos60_;
                        ci = ci - ai * cos60_;
                        ar = cr + br;
                        ai = ci + bi;
                        xr[i1r] = ar * w1r - ai * w1i;
                        xi[i1i] = ai * w1r + ar * w1i;
                        ar = cr - br;
                        ai = ci - bi;
                        xr[i2r] = ar * w2r - ai * w2i;
                        xi[i2i] = ai * w2r + ar * w2i;
                    }
                }

                nb = nb * 3;
                n = m;
            } else if (factor == 5)
            {
                // ---------------------------------------------
                // Radix 5 reduction
                // ---------------------------------------------
                final int m = n / 5;


                // For each element in a block
                for (int i = 0, kk = 0; i < m; i += stride, kk += nb)
                {
                    // Local variables for the twiddle factors
                    final double w1r = exr(kk);
                    final double w1i = exi(kk, inverse);
                    final double w2r = exr(2 * kk);
                    final double w2i = exi(2 * kk, inverse);
                    final double w3r = exr(3 * kk);
                    final double w3i = exi(3 * kk, inverse);
                    final double w4r = exr(4 * kk);
                    final double w4i = exi(4 * kk, inverse);

                    // For each block
                    for (int blockStart = i; blockStart < np; blockStart += n)
                    {
                        final int i0r = blockStart + rOffset;
                        final int i1r = i0r + m;
                        final int i2r = i1r + m;
                        final int i3r = i2r + m;
                        final int i4r = i3r + m;
                        final int i0i = blockStart + iOffset;
                        final int i1i = i0i + m;
                        final int i2i = i1i + m;
                        final int i3i = i2i + m;
                        final int i4i = i3i + m;

                        // Pair sums
                        final double x1p4r = xr[i1r] + xr[i4r];
                        final double x1p4i = xi[i1i] + xi[i4i];
                        final double x2p3r = xr[i2r] + xr[i3r];
                        final double x2p3i = xi[i2i] + xi[i3i];

                        // Pair differences
                        final double x4m1i = xi[i4i] - xi[i1i];
                        final double x1m4r = xr[i1r] - xr[i4r];
                        final double x2m3i = xi[i2i] - xi[i3i];
                        final double x3m2r = xr[i3r] - xr[i2r];

                        // 0.5 * (s1+s4)
                        final double s1_p_s4r = xr[i0r] + x1p4r * c1r_ + x2p3r * c2r_;
                        final double s1_p_s4i = xi[i0i] + x1p4i * c1r_ + x2p3i * c2r_;

                        // 0.5 * (s1-s4)
                        final double s1_m_s4r = x4m1i * c1i - x2m3i * c2i;
                        final double s1_m_s4i = x1m4r * c1i - x3m2r * c2i;

                        // 0.5 * (s2+s3)
                        final double s2_p_s3r = xr[i0r] + x1p4r * c2r_ + x2p3r * c1r_;
                        final double s2_p_s3i = xi[i0i] + x1p4i * c2r_ + x2p3i * c1r_;

                        // 0.5 * (s2-s3)
                        final double s2_m_s3r = x4m1i * c2i + x2m3i * c1i;
                        final double s2_m_s3i = x1m4r * c2i + x3m2r * c1i;

                        // Terms before twiddle
                        final double s0r = xr[i0r] + x1p4r + x2p3r;
                        final double s0i = xi[i0i] + x1p4i + x2p3i;

                        final double s1r = s1_p_s4r + s1_m_s4r;
                        final double s1i = s1_p_s4i + s1_m_s4i;

                        final double s2r = s2_p_s3r + s2_m_s3r;
                        final double s2i = s2_p_s3i + s2_m_s3i;

                        final double s3r = s2_p_s3r - s2_m_s3r;
                        final double s3i = s2_p_s3i - s2_m_s3i;

                        final double s4r = s1_p_s4r - s1_m_s4r;
                        final double s4i = s1_p_s4i - s1_m_s4i;

                        // Perform twiddle and put back
                        xr[i0r] = s0r;
                        xi[i0i] = s0i;

                        xr[i1r] = s1r * w1r - s1i * w1i;
                        xi[i1i] = s1r * w1i + s1i * w1r;

                        xr[i2r] = s2r * w2r - s2i * w2i;
                        xi[i2i] = s2r * w2i + s2i * w2r;

                        xr[i3r] = s3r * w3r - s3i * w3i;
                        xi[i3i] = s3r * w3i + s3i * w3r;

                        xr[i4r] = s4r * w4r - s4i * w4i;
                        xi[i4i] = s4r * w4i + s4i * w4r;
                    }
                }
                nb = nb * 5;
                n = m;
            }
        }

        // Re-order the coefficients
        for (short[] reorderChain : reorderChains_)
        {
            int s = reorderChain[0] * stride;
            final double u = xr[s + rOffset];
            final double v = xi[s + iOffset];
            for (int k = 1; k < reorderChain.length; k++)
            {
                final int r = reorderChain[k] * stride;
                xr[s + rOffset] = xr[r + rOffset];
                xi[s + iOffset] = xi[r + iOffset];
                s = r;
            }
            xr[s + rOffset] = u;
            xi[s + iOffset] = v;
        }
        return inverse ? order_ : 1.0;
    }
/*
    private static int binarySearch0(int[] a, int fromIndex, int toIndex,
            int key)
    {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high)
        {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }*/

    static public int findLowerValidNumberOfPoints(int key)
    {
        return Utilities.findLowerBoundFromArray(key, VALID_NUMBER_OF_POINTS);
    }

    /**
     * Internal method to get the real part of the twiddle factor.
     *
     * @param index the index of the twiddle factor.
     * @return the real part of the twiddle factor.
     */
    private double exr(final int index)
    {
        return (index < exr_.length) ? exr_[index] : exr_[order_ - index];
    }

    /**
     * Internal method to get the imaginary part of the twiddle factor.
     *
     * @param index the index of the twiddle factor.
     * @return the imaginary part of the twiddle factor.
     */
    private double exi(final int index, boolean inverse)
    {
        final double exi = (index < exr_.length) ? exi_[index] : -exi_[order_ - index];
        return inverse ? -exi : exi;
    }

    /**
     * Internal method used in resorting the transform.
     *
     * @param r a sort position in the data.
     * @return the next sort position in the array.
     */
    private int next(int r, int[] rproducts, int[] sproducts)
    {
        int s = 0;
        for (int factorIndex = 0; (r > 0) && (factorIndex < numberOfFactor_); factorIndex++)
        {
            final int t = r / rproducts[factorIndex];
            r -= t * rproducts[factorIndex];
            s += t * sproducts[factorIndex];
        }
        return s;
    }
    // To hold the factor information
    private final int[] factors_ = new int[16];
    private int numberOfFactor_ = 0;
    // Arrays to hold the twiddle factor coefficients
    private final double[] exr_;
    private final double[] exi_;
    // The chains required by the reorder
    private final short[][] reorderChains_;
    // The factors that this FFT can handle
    private static final int[] validFactors_ =
    {
        2, 3, 5
    };
    // Radix 3 multipliers
    private static final double sin60_ = Math.sin(Math.PI / 3.0);
    private static final double cos60_ = 0.5;
    // Radix 5 multipliers
    private static final double c1r_ = Math.cos(2.0 * Math.PI / 5.0);
    private static final double c1i_ = Math.sin(-2.0 * Math.PI / 5.0);
    private static final double c2r_ = Math.cos(4.0 * Math.PI / 5.0);
    private static final double c2i_ = Math.sin(-4.0 * Math.PI / 5.0);
    public static final int VALID_NUMBER_OF_POINTS[] =
    {
        2, 3, 4, 5, 6, 8, 9, 10,
        12, 15, 16, 18, 20, 24, 25, 27,
        30, 32, 36, 40, 45, 48, 50, 54,
        60, 64, 72, 75, 80, 81, 90, 96,
        100, 108, 120, 125, 128, 135, 144, 150,
        160, 162, 180, 192, 200, 216, 225, 240,
        243, 250, 256, 270, 288, 300, 320, 324,
        360, 375, 384, 400, 405, 432, 450, 480,
        486, 500, 512, 540, 576, 600, 625, 640,
        648, 675, 720, 729, 750, 768, 800, 810,
        864, 900, 960, 972, 1000, 1024, 1080, 1125,
        1152, 1200, 1215, 1250, 1280, 1296, 1350, 1440,
        1458, 1500, 1536, 1600, 1620, 1728, 1800, 1875,
        1920, 1944, 2000, 2025, 2048, 2160, 2187, 2250,
        2304, 2400, 2430, 2500, 2560, 2592, 2700, 2880,
        2916, 3000, 3072, 3125, 3200, 3240, 3375, 3456,
        3600, 3645, 3750, 3840, 3888, 4000, 4050, 4096,
        4320, 4374, 4500, 4608, 4800, 4860, 5000, 5120,
        5184, 5400, 5625, 5760, 5832, 6000, 6075, 6144,
        6250, 6400, 6480, 6561, 6750, 6912, 7200, 7290,
        7500, 7680, 7776, 8000, 8100, 8192, 8640, 8748,
        9000, 9216, 9375, 9600, 9720, 10000, 10125, 10240,
        10368, 10800, 10935, 11250, 11520, 11664, 12000, 12150,
        12288, 12500, 12800, 12960, 13122, 13500, 13824, 14400,
        14580, 15000, 15360, 15552, 15625, 16000, 16200, 16384,
        16875, 17280, 17496, 18000, 18225, 18432, 18750, 19200,
        19440, 19683, 20000, 20250, 20480, 20736, 21600, 21870,
        22500, 23040, 23328, 24000, 24300, 24576, 25000, 25600,
        25920, 26244, 27000, 27648, 28125, 28800, 29160, 30000,
        30375, 30720, 31104, 31250, 32000, 32400, 32768,
    };
}
