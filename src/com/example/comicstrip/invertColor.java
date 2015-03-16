package com.example.comicstrip;

import android.graphics.Bitmap;
import android.graphics.Color;

public class invertColor {

	public  Bitmap invert(Bitmap src) {
        Bitmap output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        int A, R, G, B;
        int pixelColor;
        int height = src.getHeight();
        int width = src.getWidth();

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            pixelColor = src.getPixel(x, y);
            A = Color.alpha(pixelColor);
            
            R = 255 - Color.red(pixelColor);
            G = 255 - Color.green(pixelColor);
            B = 255 - Color.blue(pixelColor);
            
            output.setPixel(x, y, Color.argb(A, R, G, B));
        }
    }

    return output;
}  
}
