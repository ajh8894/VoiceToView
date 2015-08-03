/*
 * MixedRadixRealFFT.java
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
 * Computes the mixed radix (2,3 and 5) DFT/FFT for real data.
 * 
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
 * Use of this routine can typically speed up the FFT of real data by a 
 * factor of about 2.
 *
 * @author Roger Millington
 */
public class MixedRadixRealFFT extends RealTransformBase
{
    /**
     * Construct an MixedRadixRealFFT agent for a given order.
     * <p>
     * The number of points can only have factors of 2, 3 or 5 but there
     * must be at least one factor of two.
     *
     * @param order the number of real points to transform.
     */
    public MixedRadixRealFFT(int order)
    {
        super(order);

        if ((order & 1) != 0)
            throw new IllegalArgumentException("The order must be even");

        mixedRadixFFT_ = new MixedRadixComplexFFT(order / 2);
        c2r_ = new ComplexToReal(order);
    }

    /**
     * Template method implementation to compute the FFT for real data.
     *
     * @param direction indicates whether to perform the 'forward' or 'inverse' transform.
     * @param x the array containing the data.
     * @param offset the offset into the data of the first point.
     * @param stride the distance between data points.
     * @return the scaling to be applied to the resulting values to bring them
     * into line with the theoretical values.
     */
    public double transform(TransformDirection direction, final double[] x, int offset, int stride)
    {
        if (direction == TransformDirection.Inverse)
        {
            double scale = c2r_.inverse(x, offset, stride);
            scale *= mixedRadixFFT_.transform(direction, x, offset, stride);
            return scale;
        } else
        {
            double scale = mixedRadixFFT_.transform(direction, x, offset, stride);
            scale *= c2r_.transform(x, offset, stride);
            return scale;
        }
    }

    /**
     * Finds a valid number of points for the FFT that is the largest less
     * than or equal to a given value.
     *
     * @param key the reference values to start with.
     * @return a valid number of points for the FFT that is the largest less
     * than or equal to that supplied as the argument.
     */
    static public int findLowerValidNumberOfPoints(int key)
    {
        return Utilities.findLowerBoundFromArray(key, VALID_NUMBER_OF_POINTS);
    }
    //
    public static final int[] VALID_NUMBER_OF_POINTS =
    {
        4, 6, 8, 10, 12, 16, 18, 20,
        24, 30, 32, 36, 40, 48, 50, 54,
        60, 64, 72, 80, 90, 96, 100, 108,
        120, 128, 144, 150, 160, 162, 180, 192,
        200, 216, 240, 250, 256, 270, 288, 300,
        320, 324, 360, 384, 400, 432, 450, 480,
        486, 500, 512, 540, 576, 600, 640, 648,
        720, 750, 768, 800, 810, 864, 900, 960,
        972, 1000, 1024, 1080, 1152, 1200, 1250, 1280,
        1296, 1350, 1440, 1458, 1500, 1536, 1600, 1620,
        1728, 1800, 1920, 1944, 2000, 2048, 2160, 2250,
        2304, 2400, 2430, 2500, 2560, 2592, 2700, 2880,
        2916, 3000, 3072, 3200, 3240, 3456, 3600, 3750,
        3840, 3888, 4000, 4050, 4096, 4320, 4374, 4500,
        4608, 4800, 4860, 5000, 5120, 5184, 5400, 5760,
        5832, 6000, 6144, 6250, 6400, 6480, 6750, 6912,
        7200, 7290, 7500, 7680, 7776, 8000, 8100, 8192,
        8640, 8748, 9000, 9216, 9600, 9720, 10000, 10240,
        10368, 10800, 11250, 11520, 11664, 12000, 12150, 12288,
        12500, 12800, 12960, 13122, 13500, 13824, 14400, 14580,
        15000, 15360, 15552, 16000, 16200, 16384, 17280, 17496,
        18000, 18432, 18750, 19200, 19440, 20000, 20250, 20480,
        20736, 21600, 21870, 22500, 23040, 23328, 24000, 24300,
        24576, 25000, 25600, 25920, 26244, 27000, 27648, 28800,
        29160, 30000, 30720, 31104, 31250, 32000, 32400, 32768,
        33750, 34560, 34992, 36000, 36450, 36864, 37500, 38400,
        38880, 39366, 40000, 40500, 40960, 41472, 43200, 43740,
        45000, 46080, 46656, 48000, 48600, 49152, 50000, 51200,
        51840, 52488, 54000, 55296, 56250, 57600, 58320, 60000,
        60750, 61440, 62208, 62500, 64000, 64800, 65536
    };
    private final MixedRadixComplexFFT mixedRadixFFT_;
    private final ComplexToReal c2r_;
}
