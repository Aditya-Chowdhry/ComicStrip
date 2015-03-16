package com.example.comicstrip;

import java.nio.IntBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class ColorDodgeBlendMerge {

	private int colordodge(int in1, int in2) {
	    float image = (float)in2;
	    float mask = (float)in1;
	    return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));

	}/**
	 * Blends 2 bitmaps to one and adds the color dodge blend mode to it.
	 */
	public Bitmap ColorDodgeBlend(Bitmap source, Bitmap layer) {
	    Bitmap base = source.copy(Config.ARGB_8888, true);
	    Bitmap blend = layer.copy(Config.ARGB_8888, false);

	    IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
	    base.copyPixelsToBuffer(buffBase);
	    buffBase.rewind();

	    IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
	    blend.copyPixelsToBuffer(buffBlend);
	    buffBlend.rewind();

	    IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
	    buffOut.rewind();

	    while (buffOut.position() < buffOut.limit()) {
	        int filterInt = buffBlend.get();
	        int srcInt = buffBase.get();

	        int redValueFilter = Color.red(filterInt);
	        int greenValueFilter = Color.green(filterInt);
	        int blueValueFilter = Color.blue(filterInt);

	        int redValueSrc = Color.red(srcInt);
	        int greenValueSrc = Color.green(srcInt);
	        int blueValueSrc = Color.blue(srcInt);

	        int redValueFinal = colordodge(redValueFilter, redValueSrc);
	        int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
	        int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);

	        int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);

	        buffOut.put(pixel);
	    }

	    buffOut.rewind();

	    base.copyPixelsFromBuffer(buffOut);
	    blend.recycle();

	    return base;
	}
}
