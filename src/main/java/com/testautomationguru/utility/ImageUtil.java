package com.testautomationguru.utility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


import javax.imageio.ImageIO;

public class ImageUtil {


	private ImageUtil() {
	    throw new IllegalStateException("Utility class");
	}
	static Logger logger = Logger.getLogger(ImageUtil.class.getName());

	static boolean compareAndHighlight(final BufferedImage img1, final BufferedImage img2, String fileName, boolean highlight, int colorCode)
    {

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
    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, format, baos);
            byte[] bytes = baos.toByteArray();
            return bytes;

        }
	static byte[] compareAndHighlight(final BufferedImage img1, final BufferedImage img2, boolean highlight, int colorCode)
	throws IOException
    {
	    final int w = img1.getWidth();
	    final int h = img1.getHeight();
	    final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
	    final int[] p2 = img2.getRGB(0, 0, w, h, null, 0, w);
	    BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

	    if(!(java.util.Arrays.equals(p1, p2))){
	    	logger.warning("Image compared - does not match");
	    	if(highlight){
	    	    for (int i = 0; i < p1.length; i++) {
	    	        if (p1[i] != p2[i]){
	    	            p1[i] = colorCode;
	    	        }
	    	    }

	    	  out.setRGB(0, 0, w, h, p1, 0, w);
	    	  return toByteArray(out, "png");
	    	}

	    }
    	return new byte[0];
	}
	static void saveImage(BufferedImage image, String file){
		try{
			File outputfile = new File(file);
			ImageIO.write(image, "png", outputfile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
