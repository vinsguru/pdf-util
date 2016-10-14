package com.testautomationguru;

import java.io.IOException;

import com.testautomationguru.utility.CompareMode;
import com.testautomationguru.utility.PDFUtil;

public final class Main {

	public static void main(String[] args) throws IOException {
		
		if(args.length<2){
			showUsage();
		}else{
			PDFUtil pdfutil = new PDFUtil();
			pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
			pdfutil.compare(args[0], args[1]);
		}
		
	}

	private static void showUsage(){
		System.out.println("Usage: java -jar pdf-util.jar file1.pdf file2.pdf");
	}
}
