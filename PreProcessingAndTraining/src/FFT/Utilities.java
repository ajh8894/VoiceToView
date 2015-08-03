/*
 * Utilities.java
 *
 * Created on October 6, 2006, 9:51 AM
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

import java.util.*;

/**
 * Some utility functions useful when working with DFT/FFT
 * 
 * @author Roger Millington
 */
public final class Utilities
{
    /**
     * Made private to stop instanciation
     */
    private Utilities()
    {
    }
    
    public static List<Integer> createListOfValidNumberOfPoints(int[] terms, int min, int max)
    {
        List<Integer> list = new ArrayList<Integer>();
        createListOfValidNumberOfPoints(list, 0, terms, min, max);
        Collections.sort(list);
        return list;
    }
    
    private static void createListOfValidNumberOfPoints(List<Integer> list, int index, int[] terms, int min, int max)
    {
        if (index < terms.length)
        {
            final int factor = terms[index];
            while (min < max)
            {
                createListOfValidNumberOfPoints(list, index+1, terms, min, max);
                min *= factor;
                if (min <= max)
                    list.add(min);
                else
                    break;
            }
        }
    }
    
    public static void multiply(double[] values, int offset, int stride, int length, double multiplier)
    {
        for (int count = 0; count < length; count++)
        {
            values[offset] *= multiplier;
            offset += stride;
        }
    }


    static public int findLowerBoundFromArray(int key, int[] sortedArray)
    {
        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high)
        {
            int mid = (low + high) >>> 1;
            int midVal = sortedArray[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return midVal; // key found
        }

        return sortedArray[high >= 0 ? high : 0];
    }

}
