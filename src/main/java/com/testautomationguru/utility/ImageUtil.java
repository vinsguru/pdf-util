package com.testautomationguru.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import lombok.val;

class ImageUtil {
	
	static Logger logger = Logger.getLogger(ImageUtil.class.getName());
	
	static boolean compareAndHighlight(final BufferedImage img1, final BufferedImage img2, String fileName, boolean highlight, int colorCode) throws IOException {

	    val w = img1.getWidth();
	    val h = img1.getHeight();
	    val p1 = img1.getRGB(0, 0, w, h, null, 0, w);
	    val p2 = img2.getRGB(0, 0, w, h, null, 0, w);

	    if(!(java.util.Arrays.equals(p1, p2))){
	    	logger.warning("Image compared - does not match");
	    	if(highlight){
	    	    for (int i = 0; i < p1.length; i++) {
	    	        if (p1[i] != p2[i]){
	    	            p1[i] = colorCode;
	    	        } 
	    	    }
	    	    val out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    	    out.setRGB(0, 0, w, h, p1, 0, w);
	    	    saveImage(out, fileName);
	    	}
	    	return false;
	    }
	    return true;
	}

	static void saveImage(BufferedImage image, String file){
		try{
			val outputfile = new File(file);
			ImageIO.write(image, "png", outputfile);	
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
