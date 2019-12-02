package com.testautomationguru.utility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

class ImageUtil {
	static java.awt.Color imgColor;
	
	static Logger logger = Logger.getLogger(ImageUtil.class.getName());
	
	static boolean compareAndHighlight(final BufferedImage img1, final BufferedImage img2, String fileName, boolean highlight, int colorCode) throws IOException {

	    final int w = img1.getWidth();
	    final int h = img1.getHeight();
	    final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
	    final int[] p2 = img2.getRGB(0, 0, w, h, null, 0, w);

	    if(!(java.util.Arrays.equals(p1, p2))){
	    	logger.warning("Image compared - does not match");
	    	if(highlight){
	    	    for (int i = 0; i < p1.length; i++) {
	    	        if (p1[i] != p2[i]){
	    	            p1[i] = colorCode;
	    	        } 
	    	    }
	    	    final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    	    out.setRGB(0, 0, w, h, p1, 0, w);
	    	    saveImage(out, fileName);
	    	}
	    	return false;
	    }
	    return true;
	}

	static boolean compareAndHighlightWithShiftCheck(final BufferedImage img1, final BufferedImage img2, String fileName, boolean highlight, int colorCode) throws IOException {

		final int w = img1.getWidth();
		int[] p1;
		int[] p2;
		ArrayList<Integer> colorNo = new ArrayList<>();
		p1 = img1.getRGB(0, 0, w, h, null, 0, w);
		p2 = img2.getRGB(0, 0, w, h, null, 0, w);
		int[] pageReal = p1.clone();
		for (int s = 0; s < p1.length; s++) {
			if(p1[s] != -1){
				p1[s] = Color.BLACK.getRGB();
			}
		}

		for (int z = 0; z < p2.length; z++) {
			if(p2[z] != -1){
				p2[z] =  Color.BLACK.getRGB();
			}
		}

		if(!(java.util.Arrays.equals(p1, p2))){
			if(highlight){
				int realDifference = 0;
				A: for (int i = 0; i < p1.length; i++) {
					if (p1[i] != p2[i]){

						//Check for downer and upper pixels
						boolean isShift = false;
						if(p1[i] != -1) {
							B:for (int j = 1; j < 6; j++) {
								if (shiftChecker(p1, p2, i, w, (i)) || shiftChecker(p1, p2, i, w, (i - j * w)) || shiftChecker(p1, p2, i, w, (i + j * w)) || shiftChecker(p1, p2, i, w, (i + j)) || shiftChecker(p1, p2, i, w, (i - j))) {
									isShift = true;
									break B;
								} else {
									C:
									for (int c = 1; c < 6; c++) {
										if (shiftChecker(p1, p2, i, w, (i - j * w - c)) || shiftChecker(p1, p2, i, w, (i + j * w - c)) || shiftChecker(p1, p2, i, w, (i - j * w + c)) || shiftChecker(p1, p2, i, w, (i + j * w + c))) {
											isShift = true;
											break B;
										}
									}
								}
								if (isShift) {
									break B;
								}
							}
						} else {
							B:for (int j = 1; j < 6; j++) {
								if (shiftChecker(p2, p1, i, w, (i)) || shiftChecker(p2, p1, i, w, (i - j * w)) || shiftChecker(p2, p1, i, w, (i + j * w)) || shiftChecker(p2, p1, i, w, (i + j)) || shiftChecker(p2, p1, i, w, (i - j))) {
									isShift = true;
									break B;
								} else {
									C:
									for (int c = 1; c < 6; c++) {
										if (shiftChecker(p2, p1, i, w, (i - j * w - c)) || shiftChecker(p2, p1, i, w, (i + j * w - c)) || shiftChecker(p2, p1, i, w, (i - j * w + c)) || shiftChecker(p2, p1, i, w, (i + j * w + c))) {
											isShift = true;
											break B;
										}
									}
								}
								if (isShift) {
									break B;
								}
							}
						}

						if(!isShift){
							realDifference++;
							colorNo.add(i);
						}
					}
				}

				if(colorNo.size() > 0) {
					for (int v = 0; v < colorNo.size(); v++) {
						p1[colorNo.get(v)] = colorCode;
					}
				}

				ArrayList<Integer> trueDots = removeExtras(p1,w,colorNo);

				if(trueDots.size() > 0) {
					for (int v = 0; v < trueDots.size(); v++) {
						pageReal[trueDots.get(v)] = colorCode;
					}
				}

				if(trueDots.size() > 50) {
					logger.warning("Image compared - does not match");
					final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					out.setRGB(0, 0, w, h, pageReal, 0, w);
					saveImage(out, fileName);
					return false;
				}
			}
		}
		return true;
	}



	private static void saveImage(BufferedImage image, String file){
		try{
			File outputfile = new File(file);
			ImageIO.write(image, "png", outputfile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static boolean shiftChecker(int[] c1, int[] c2, int n, int w, int n2){
		int[] actual = {(c1[n-w-1]), (c1[n-w]), (c1[n-w+1]), (c1[n-1]), (c1[n]), (c1[n+1]), (c1[n+w-1]), (c1[n+w]), (c1[n+w+1])};
		int[] diff = {(c2[n2-w-1]), (c2[n2-w]), (c2[n2-w+1]), (c2[n2-1]), (c2[n2]), (c2[n2+1]), (c2[n2+w-1]), (c2[n2+w]), (c2[n2+w+1])};
		if(java.util.Arrays.equals(actual, diff)){
			return true;
		} else {
			return false;
		}
	}
	static ArrayList<Integer> removeExtras(int[] p, int w, ArrayList<Integer> arr){
		ArrayList<Integer> result = new ArrayList<>();
		for(int i = 0; i<arr.size(); i++){
			if(p[arr.get(i)] == Color.MAGENTA.getRGB()){
				if(checkSurround(p, arr.get(i), w)){
					result.add(arr.get(i));
				}
			}
		}
		return result;
	}

	private static boolean checkSurround(int[] c, int i, int w){
		int[] surround = {(c[i-w-1]), (c[i-w]), (c[i-w+1]), (c[i-1]), (c[i+1]), (c[i+w-1]), (c[i+w]), (c[i+w+1])};
		int howMany = 0;
		for(int g=0; g<surround.length; g++){
			if(surround[g] == Color.MAGENTA.getRGB()){
				howMany++;
			}
		}
		if (howMany > 1){
			return true;
		} else {
			return false;
		}
	}
}
