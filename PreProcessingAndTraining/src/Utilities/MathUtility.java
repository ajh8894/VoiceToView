package Utilities;

import java.util.logging.Logger;

/**
 * 
 * @author davidjones
 *
 */
public class MathUtility
{
        
        static final Logger LOGGER = Logger.getLogger(MathUtility.class.getName());

        /**
         * 
         * @param _floatArray
         * @return {Array} A float array where all of the contents of the input have been changed changed to their absolute value.
         */
    public static float[] getAbs(final float[] _floatArray) {
        float[] floatArray = _floatArray;
        for(int i = 0; i < floatArray.length; i++) {
            floatArray[i] = Math.abs(floatArray[i]);
        }
        return floatArray;
    }

    /**
     * 
     * @param input
     * @return
     */
    public static float[] removeI(final float[] input) {
        float[] output = new float[input.length/2+1];//올래는 올림하는코드로바꿔야함
        int index = 0;
        for(int i = 0; i < input.length-1; i = i + 2) {//올래는 올림하는코드로바꿔야함
            output[index] = input[i];
            index++;
        }
        return output;
    }

    /**
     * 
     * @param input
     * @return
     */
    public static float[] normalise(float[] input) {
        float[] output = new float[input.length];
        final float total = getTotal(input);
        for(int i = 0; i < input.length; i++) { 
                //If statement to prevent NaN problem
            if(total != 0) {
                input[i] = input[i]/total;
            }
            output[i] = input[i];
        }
        return output;
    }

    /**
     * 
     * @param input
     * @return
     */
    public static float[] getConj(final float[] input) {
        float[] output = new float[input.length/2+1]; //올래는 올림하는코드로바꿔야함
        int index = 0;
        float newFloat = 0;
        for (int i = 0; i < input.length-1; i = i+2) {//올래는 올림하는코드로바꿔야함
    		newFloat = (input[i]*input[i]) + (input[i+1]*input[i+1]);
            output[index] = newFloat;
            index++;
        }
        return output;
    }

    /**
     * 
     * @param input
     * @return {Float} The maximum value contained in the input array.
     */
    public static float getMax(final float[] input) {
        float max = -Float.MAX_VALUE;
        for(int i = 0; i < input.length; i++) {
            if(input[i] > max) {
                max = input[i];
            }
        }
        return max;
    }

    /**
     * 
     * @param input
     * @return {float} The index of the maximum value in the input array.
     */
    public static float getMaxIndex(final float[] input) {
        float max = -Float.MAX_VALUE;
        float index = 0;
        for(int i = 0; i < input.length; i++) {
            if(input[i] > max) {
                max = input[i];
                index = i;
            }
        }
        return index;
    }

    /**
     * 
     * @param input
     * @return {Float} The minimum value found in the input array.
     */
    public static float getMin(final float[] input) {
        float min = Float.MAX_VALUE;
        for(int i = 0; i < input.length; i++) {
            if(input[i] < min) {
                min = input[i];
            }
        }
        return min;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return {Array} An array containing the results of the dot-multiply from the two input arrays.
     */
    public static float[] dotMultiply(final float[] inputX, final float[] inputY) {
        float[] output = new float[inputX.length];
        if(inputX.length == inputY.length) {
            for(int i = 0; i < inputX.length; i++) {
                output[i] = inputX[i] * inputY[i];
            }
        } else {
            LOGGER.severe("Arrays not of equal size to allow Dot Multiplication(mathUtilities)");
        }
        return output;
    }

    /**
     * 
     * @param input
     * @param library
     * @return
     */
    public static float getMatchValue(final float[] input, final float[] library) {
        float total = 0;
        if(input.length == library.length) {
            for(int i = 0; i < input.length; i++) {
                total += input[i] * library[i];
            }
        } else {
            LOGGER.severe("Arrays not of equal size to allow getMatchValue (mathUtilities)");
            LOGGER.fine("Input = " + input.length + " vs. " + "Library = " + library.length);
        }
        return total;
    }

    /**
     * 
     * @param x
     * @param y
     * @return {Array} An array containing the results of the dot-divide from the two input arrays.
     */
    public static float[] dotDivide(final float[] inputX, final float[] inputY) {
        float[] output = new float[inputX.length];
        if(inputX.length == inputY.length) {
                for(int i = 0; i < inputX.length; i++) {
                output[i] = inputX[i] / inputY[i];
            }
        } else {
            LOGGER.severe("Arrays not of equal size to allow Dot Divide (mathUtilities)");
        }
        return output;
    }

    /**
     * 
     * @param input
     * @return {Float} A float containing the sum of the contents of the input array.
     */
    public static float getTotal(final float[] input) {
        float output = 0;
        for(int i = 0; i < input.length; i++) {
            output += input[i];
        }
        return output;
    }
}