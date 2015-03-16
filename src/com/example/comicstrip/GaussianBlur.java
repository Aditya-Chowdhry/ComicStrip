package com.example.comicstrip;

import android.graphics.Bitmap;
import android.graphics.Color;

public class GaussianBlur {

	public static Bitmap applyGaussianBlur(Bitmap src) {
		  //set gaussian blur configuration
		  double[][] GaussianBlurConfig = new double[][] {
		    { 1, 2, 1 },
		    { 2, 4, 2 },
		    { 1, 2, 1 }
		  };
		  // create instance of Convolution matrix
		  ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		  // Apply Configuration
		  convMatrix.applyConfig(GaussianBlurConfig);
		  convMatrix.Factor = 16;
		  convMatrix.Offset = 0;
		  //return out put bitmap
		  return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
		 }
	public  Bitmap sharpen(Bitmap src, int weight) {
	    double[][] SharpConfig = new double[][] {
	        { 0 , -2    , 0  },
	        { -2, weight, -2 },
	        { 0 , -2    , 0  }
	    };
	    ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
	    convMatrix.applyConfig(SharpConfig);
	    convMatrix.Factor = weight - 8;
	    return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}


}

 class ConvolutionMatrix {
	  public static final int SIZE = 3;
	   
	     public double[][] Matrix;
	     public double Factor = 1;
	     public double Offset = 1;
	   
	    //Constructor with argument of size
	     public ConvolutionMatrix(int size) {
	         Matrix = new double[size][size];
	     }
	   
	     public void setAll(double value) {
	         for (int x = 0; x < SIZE; ++x) {
	             for (int y = 0; y < SIZE; ++y) {
	                 Matrix[x][y] = value;
	             }
	         }
	     }
	   
	     public void applyConfig(double[][] config) {
	         for(int x = 0; x < SIZE; ++x) {
	             for(int y = 0; y < SIZE; ++y) {
	                 Matrix[x][y] = config[x][y];
	             }
	         }
	     }
	   
	     public static Bitmap computeConvolution3x3(Bitmap src, ConvolutionMatrix matrix) {
	         int width = src.getWidth();
	         int height = src.getHeight();
	         Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
	   
	         int A, R, G, B;
	         int sumR, sumG, sumB;
	         int[][] pixels = new int[SIZE][SIZE];
	   
	         for(int y = 0; y < height - 2; ++y) {
	             for(int x = 0; x < width - 2; ++x) {
	   
	                 // get pixel matrix
	                 for(int i = 0; i < SIZE; ++i) {
	                     for(int j = 0; j < SIZE; ++j) {
	                         pixels[i][j] = src.getPixel(x + i, y + j);
	                     }
	                 }
	   
	                 // get alpha of center pixel
	                 A = Color.alpha(pixels[1][1]);
	   
	                 // init color sum
	                 sumR = sumG = sumB = 0;
	   
	                 // get sum of RGB on matrix
	                 for(int i = 0; i < SIZE; ++i) {
	                     for(int j = 0; j < SIZE; ++j) {
	                         sumR += (Color.red(pixels[i][j]) * matrix.Matrix[i][j]);
	                         sumG += (Color.green(pixels[i][j]) * matrix.Matrix[i][j]);
	                         sumB += (Color.blue(pixels[i][j]) * matrix.Matrix[i][j]);
	                     
	                     }
	                 }
	   
	                 // get final Red
	                 R = (int)(sumR / matrix.Factor + matrix.Offset);
	                 if(R < 0) { R = 0; }
	                 else if(R > 255) { R = 255; }
	   
	                 // get final Green
	                 G = (int)(sumG / matrix.Factor + matrix.Offset);
	                 if(G < 0) { G = 0; }
	                 else if(G > 255) { G = 255; }
	   
	                 // get final Blue
	                 B = (int)(sumB / matrix.Factor + matrix.Offset);
	                 if(B < 0) { B = 0; }
	                 else if(B > 255) { B = 255; }
	   
	                 // apply new pixel
	                 result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
	             }
	         }
	   
	         // final image
	         return result;
	     }
	 }
