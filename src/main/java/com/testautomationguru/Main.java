package com.testautomationguru;

import java.io.IOException;
import java.util.logging.Logger;

import com.testautomationguru.utility.CompareMode;
import com.testautomationguru.utility.PDFUtil;

public final class Main {

	static Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws IOException {

		if(args.length<2){
			showUsage();
		}else{

			PDFUtil pdfutil = new PDFUtil();
			pdfutil.setCompareMode(CompareMode.VISUAL_MODE);

			if(args.length>2){
				pdfutil.highlightPdfDifference(true);

				pdfutil.setImageDestinationPath(args[2]);
				if(args.length>3 && "true".equals(args[3])){
				pdfutil.savePDF(true);
				}
			}

			pdfutil.compare(args[0], args[1]);
		}

	}

	private static void showUsage(){
		System.out.println("Usage: java -jar pdf-util.jar file1.pdf file2.pdf [Optional:image-destination-path [Optional:create-pdf-instead]]\n"
				+ "image-destination-path - path to output directory\n"
				+ "create-pdf-instead - if set to 'true' generates a PDF instead of png's, png for any other value, requires image-destination-path to be set");
	}
}
