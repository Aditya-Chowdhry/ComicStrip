package com.example.comicstrip;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class grayScale {

	 public Bitmap convertToGrayscale(Bitmap bmpOriginal)
	 {       
	             int width, height;
	             height = bmpOriginal.getHeight();
	             width = bmpOriginal.getWidth();   
	             Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	             Canvas canvas = new Canvas(bmpGrayscale);
	             Paint paint = new Paint();
	             ColorMatrix colorMatrix = new ColorMatrix();
	             colorMatrix.setSaturation(0);
	             ColorMatrixColorFilter f = new ColorMatrixColorFilter(colorMatrix);
	             paint.setColorFilter(f);
	             canvas.drawBitmap(bmpOriginal, 0, 0, paint);
	             return bmpGrayscale;
	 }
}
