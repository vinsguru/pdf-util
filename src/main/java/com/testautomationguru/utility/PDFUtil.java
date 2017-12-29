package com.testautomationguru.utility;

/*
 * Copyright [2015] [www.testautomationguru.com]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* <h1>PDF Utility</h1>
* A simple pdf utility using apache pdfbox to get the text, 
* compare files using plain text or pixel by pixel comparison, extract all the images from the pdf
*
* @author  www.testautomationguru.com
* @version 1.0
* @since   2015-06-13
*/

public class PDFUtil {

	private final static Logger logger = Logger.getLogger(PDFUtil.class.getName());
	private String imageDestinationPath;
	private PdfUtilImageListener imageListener;
	private boolean bTrimWhiteSpace;
	private boolean bHighlightPdfDifference;
	private Color imgColor;
	private PDFTextStripper stripper;
	private boolean bCompareAllPages;
	private CompareMode compareMode;
	private String[] excludePattern;
	
	/*
	 * Constructor
	 */
	
	public PDFUtil(){
		this.bTrimWhiteSpace = true;
		this.bHighlightPdfDifference = false;
		this.imgColor = Color.MAGENTA;
		this.bCompareAllPages = false;
		this.compareMode = CompareMode.TEXT_MODE;
		logger.setLevel(Level.OFF);
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
	}
	
   /**
   * This method is used to show log in the console. Level.INFO
   * It is set to Level.OFF by default.
   */
	public void enableLog(){
		logger.setLevel(Level.INFO);
	}

   /**
   * This method is used to change the file comparison mode text/visual
   * @param mode CompareMode
   */
	public void setCompareMode(CompareMode mode){
		this.compareMode = mode;
	}
	
   /**
   * This method is used to get the current comparison mode text/visual
   * @return CompareMode
   */
	public CompareMode getCompareMode(){
		return this.compareMode;
	}	
		
   /**
   * This method is used to change the level
   * @param level java.util.logging.Level 
   */
	public void setLogLevel(java.util.logging.Level level){
		logger.setLevel(level);
	}
		
   /**
   * getText method by default replaces all the white spaces and compares.
   * This method is used to enable/disable the feature.
   * 
   * @param flag true to enable;  false otherwise
   */
	public void trimWhiteSpace(boolean flag){
		this.bTrimWhiteSpace = flag;
	}
	
   /**
   * Path where images are stored
   * when the savePdfAsImage or extractPdfImages methods are invoked.
   * 
   * @return String Absolute path where images are stored
   */	
	public String getImageDestinationPath(){
		return this.imageDestinationPath;
	}

   /**
   * Set the path where images to be stored
   * when the savePdfAsImage or extractPdfImages methods are invoked.
   * 
   * @param path Absolute path to store the images
   */	
	public void setImageDestinationPath(String path){
		this.imageDestinationPath = path;
	}

	/**
	 * Listener invoked each time an image is created using the savePdfAsImage
	 * or extractPdfImages.
	 *
	 * @return
	 */
	public PdfUtilImageListener getImageListener() {
		return imageListener;
	}

	/**
	 * Set the Listener invoked each time an image is created
	 * using the savePdfAsImages or extractPdfImages.
	 *
	 * @return
	 */
	public void setImageListener(PdfUtilImageListener imageListener) {
		this.imageListener = imageListener;
	}

	/**
   * Highlight the difference when 2 pdf files are compared in Binary mode.
   * The result is saved as an image.
   * 
   * @param flag true - enable ; false - disable (default);
   */	
	public void highlightPdfDifference(boolean flag){
		this.bHighlightPdfDifference = flag;
	}	

   /**
   * Color in which pdf difference can be highlighted.
   * MAGENTA is the default color.
   * 
   * @param colorCode color code to highlight the difference
   */	
	public void highlightPdfDifference(Color colorCode){
		this.bHighlightPdfDifference = true;
		this.imgColor = colorCode;
	}	
		
   /**
   * To compare all the pages of the PDF files. By default as soon as a mismatch is found, the method returns false and exits.
   * 
   * @param flag true to enable; false otherwise
   */	
	public void compareAllPages(boolean flag){
		this.bCompareAllPages = flag;
	}	
	
   /**
   * To modify the text extracting strategy using PDFTextStripper
   * 
   * @param stripper Stripper with user strategy
   */   
    public void useStripper(PDFTextStripper stripper){
        this.stripper = stripper;
    }   	
				
   /**
   * Get the page count of the document.
   * 
   * @param file Absolute file path
   * @return int No of pages in the document.
   * @throws java.io.IOException when file is not found.
   */	
	public int getPageCount(String file) throws IOException{
		logger.info("file :" + file);
		try(FileInputStream fis = new FileInputStream(file)) {
			return getPageCount(fis);
		}
	}

	/**
	 * Get the page count of the document.
	 *
	 * @param file InputStream of the pdf-file
	 * @return int No of pages in the document.
	 * @throws java.io.IOException when file is not found.
	 */
	public int getPageCount(InputStream file) throws IOException{
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
			return getPageCount(doc);
		} finally {
			closeDocument(doc);
		}
	}

	private int getPageCount(PDDocument document) {
		int pageCount = document.getNumberOfPages();
		logger.info("pageCount :" + pageCount);
		return pageCount;
	}
				
   /**
   * Get the content of the document as plain text.
   *  
   * @param file Absolute file path
   * @return String document content in plain text.
   * @throws java.io.IOException when file is not found.
   */
	public String getText(String file) throws IOException{
		return this.getPDFText(file,-1, -1);
	}
	
   /**
   * Get the content of the document as plain text.
   *  
   * @param file Absolute file path
   * @param startPage Starting page number of the document
   * @return String document content in plain text.
   * @throws java.io.IOException when file is not found.
   */
	public String getText(String file, int startPage) throws IOException{
		return this.getPDFText(file,startPage, -1);
	}
	
   /**
   * Get the content of the document as plain text.
   *  
   * @param file Absolute file path
   * @param startPage Starting page number of the document
   * @param endPage Ending page number of the document
   * @return String document content in plain text.
   * @throws java.io.IOException when file is not found.
   */
	public String getText(String file, int startPage, int endPage) throws IOException{
		return this.getPDFText(file,startPage, endPage);
	}

	/**
	 * Get the content of the document as plain text.
	 *
	 * @param file InputStream of the pdf-file
	 * @return String document content in plain text.
	 * @throws java.io.IOException when file is not found.
	 */
	public String getText(InputStream file) throws IOException{
		return this.getPDFText(file,-1, -1);
	}

	/**
	 * Get the content of the document as plain text.
	 *
	 * @param file InputStream of the pdf-file
	 * @param startPage Starting page number of the document
	 * @return String document content in plain text.
	 * @throws java.io.IOException when file is not found.
	 */
	public String getText(InputStream file, int startPage) throws IOException{
		return this.getPDFText(file,startPage, -1);
	}

	/**
	 * Get the content of the document as plain text.
	 *
	 * @param file InputStream of the pdf-file
	 * @param startPage Starting page number of the document
	 * @param endPage Ending page number of the document
	 * @return String document content in plain text.
	 * @throws java.io.IOException when file is not found.
	 */
	public String getText(InputStream file, int startPage, int endPage) throws IOException{
		return this.getPDFText(file,startPage, endPage);
	}

	private String getPDFText(String file, int startPage, int endPage) throws IOException {
		logger.info("file : " + file);
		try(FileInputStream fis1 = new FileInputStream(file)) {
			return getPDFText(fis1, startPage, endPage);
		}
	}

	private String getPDFText(InputStream file, int startPage, int endPage) throws IOException {
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
			return getPDFText(doc, startPage, endPage);
		} finally {
			closeDocument(doc);
		}
	}

	private String getPDFText(PDDocument document, int startPage, int endPage) throws IOException{
		logger.info("startPage : " + startPage);
		logger.info("endPage : " + endPage);

		PDFTextStripper localStripper = new PDFTextStripper();
		if(null!=this.stripper){
		    localStripper = this.stripper;
		}

		PageBounds pageBounds = this.getStartAndEndPages(document, startPage, endPage);
		localStripper.setStartPage(pageBounds.startPage);
		localStripper.setEndPage(pageBounds.endPage);

		String txt = localStripper.getText(document);
		logger.info("PDF Text before trimming : " + txt);
		if(this.bTrimWhiteSpace){
			txt = txt.trim().replaceAll("\\s+", " ").trim();
			logger.info("PDF Text after  trimming : " + txt);	
		}
		
		return txt;
	}
	
	
	public void excludeText(String... regexs){
		this.excludePattern = regexs;
	}
	
	
   /**
   * Compares two given pdf documents.
   *
   * <b>Note :</b> <b>TEXT_MODE</b> : Compare 2 pdf documents contents with no formatting.
   * 			   <b>VISUAL_MODE</b> : Compare 2 pdf documents pixel by pixel for the content and format.
   * @param file1 Absolute file path of the expected file
   * @param file2 Absolute file path of the actual file
   * @return boolean true if matches, false otherwise
   * @throws java.io.IOException when file is not found.
   */
	public boolean compare(String file1, String file2) throws IOException{
		return this.comparePdfFiles(file1, file2, -1, -1);
	}
	
   /**
   * Compares two given pdf documents.
   *
   * <b>Note :</b> <b>TEXT_MODE</b> : Compare 2 pdf documents contents with no formatting.
   * 			   <b>VISUAL_MODE</b> : Compare 2 pdf documents pixel by pixel for the content and format.
   *
   * @param file1 Absolute file path of the expected file
   * @param file2 Absolute file path of the actual file
   * @param startPage Starting page number of the document
   * @param endPage Ending page number of the document
   * @return boolean true if matches, false otherwise
   * @throws java.io.IOException when file is not found.
   */
	public boolean compare(String file1, String file2, int startPage, int endPage) throws IOException{
		return this.comparePdfFiles(file1, file2, startPage, endPage);
	}
	
   /**
   * Compares two given pdf documents.
   * 
   * <b>Note :</b> <b>TEXT_MODE</b> : Compare 2 pdf documents contents with no formatting. 
   * 			   <b>VISUAL_MODE</b> : Compare 2 pdf documents pixel by pixel for the content and format.
   * 
   * @param file1 Absolute file path of the expected file
   * @param file2 Absolute file path of the actual file
   * @param startPage Starting page number of the document
   * @return boolean true if matches, false otherwise
   * @throws java.io.IOException when file is not found.
   */	
	public boolean compare(String file1, String file2, int startPage) throws IOException{
		return this.comparePdfFiles(file1, file2, startPage, -1);
	}

	/**
	 * Compares two given pdf documents.
	 *
	 * <b>Note :</b> <b>TEXT_MODE</b> : Compare 2 pdf documents contents with no formatting.
	 * 			   <b>VISUAL_MODE</b> : Compare 2 pdf documents pixel by pixel for the content and format.
	 * @param file1 InputStream of the expected pdf-file
	 * @param file2 InputStream of the actual pdf-file
	 * @param identifier Identifier for resulting images
	 * @return boolean true if matches, false otherwise
	 * @throws java.io.IOException when file is not found.
	 */
	public boolean compare(InputStream file1, InputStream file2, String identifier) throws IOException{
		return this.comparePdfFiles(file1, file2, -1, -1, identifier);
	}

	/**
	 * Compares two given pdf documents.
	 *
	 * <b>Note :</b> <b>TEXT_MODE</b> : Compare 2 pdf documents contents with no formatting.
	 * 			   <b>VISUAL_MODE</b> : Compare 2 pdf documents pixel by pixel for the content and format.
	 *
	 * @param file1 InputStream of the expected pdf-file
	 * @param file2 InputStream of the actual pdf-file
	 * @param startPage Starting page number of the document
	 * @param endPage Ending page number of the document
	 * @param identifier Identifier for resulting images
	 * @return boolean true if matches, false otherwise
	 * @throws java.io.IOException when file is not found.
	 */
	public boolean compare(InputStream file1, InputStream file2, int startPage, int endPage, String identifier) throws IOException{
		return this.comparePdfFiles(file1, file2, startPage, endPage, identifier);
	}

	/**
	 * Compares two given pdf documents.
	 *
	 * <b>Note :</b> <b>TEXT_MODE</b> : Compare 2 pdf documents contents with no formatting.
	 * 			   <b>VISUAL_MODE</b> : Compare 2 pdf documents pixel by pixel for the content and format.
	 *
	 * @param file1 InputStream of the expected pdf-file
	 * @param file2 InputStream of the actual pdf-file
	 * @param startPage Starting page number of the document
	 * @param identifier Identifier for resulting images
	 * @return boolean true if matches, false otherwise
	 * @throws java.io.IOException when file is not found.
	 */
	public boolean compare(InputStream file1, InputStream file2, int startPage, String identifier) throws IOException{
		return this.comparePdfFiles(file1, file2, startPage, -1, identifier);
	}

	private boolean comparePdfFiles(String file1, String file2, int startPage, int endPage)throws IOException{
		File file1AsFile = new File(file1);
		String identifier = fileNameToIdentifier(file1AsFile);
		if (this.bHighlightPdfDifference) {
			this.createImageDestinationDirectory(file2);
		}
		try (FileInputStream fis1 = new FileInputStream(file1AsFile);
			 FileInputStream fis2 = new FileInputStream(file2)) {
			return comparePdfFiles(fis1, fis2, startPage, endPage, identifier);
		}
	}

	private boolean comparePdfFiles(InputStream file1, InputStream file2, int startPage, int endPage, String identifier) throws IOException{
		if(CompareMode.TEXT_MODE==this.compareMode)
			return comparepdfFilesWithTextMode(file1, file2, startPage, endPage);
		else
			return comparePdfByImage(file1, file2, startPage, endPage, identifier);
	}

	private boolean comparepdfFilesWithTextMode(InputStream file1, InputStream file2, int startPage, int endPage) throws IOException{

		String file1Txt = this.getPDFText(file1, startPage, endPage).trim();
		String file2Txt = this.getPDFText(file2, startPage, endPage).trim();

		if(null!=this.excludePattern && this.excludePattern.length>0){
			for(int i=0; i<this.excludePattern.length; i++){
				file1Txt = file1Txt.replaceAll(this.excludePattern[i], "");
				file2Txt = file2Txt.replaceAll(this.excludePattern[i], "");
			}
		}

		logger.info("File 1 Txt : " + file1Txt);
		logger.info("File 2 Txt : " + file2Txt);

		boolean result = file1Txt.equalsIgnoreCase(file2Txt);

		if(!result){
			logger.warning("PDF content does not match");
		}

		return result;
	}
	
   /**
   * Save each page of the pdf as image
   * 
   * @param file Absolute file path of the file
   * @param startPage Starting page number of the document
   * @return List list of image file names with absolute path
   * @throws java.io.IOException when file is not found.
   */
	public List<String> savePdfAsImage(String file, int startPage) throws IOException{
		return this.saveAsImage(file, startPage, -1);
	}
	
   /**
   * Save each page of the pdf as image
   * 
   * @param file Absolute file path of the file
   * @param startPage Starting page number of the document
   * @param endPage Ending page number of the document
   * @return List list of image file names with absolute path
   * @throws java.io.IOException when file is not found.
   */
	public List<String> savePdfAsImage(String file, int startPage, int endPage) throws IOException{
		return this.saveAsImage(file, startPage, endPage);
	}
	
   /**
   * Save each page of the pdf as image
   * 
   * @param file Absolute file path of the file
   * @return List list of image file names with absolute path
   * @throws java.io.IOException when file is not found.
   */	
	public List<String> savePdfAsImage(String file) throws IOException{
		return this.saveAsImage(file, -1, -1);
	}

	/**
	 * Save each page of the pdf as image
	 *
	 * @param file InputStream of the pdf-file
	 * @param startPage Starting page number of the document
	 * @param identifier Identifier for resulting images
	 * @return List list of image file names with absolute path
	 * @throws java.io.IOException when file is not found.
	 */
	public List<String> savePdfAsImage(InputStream file, int startPage, String identifier) throws IOException{
		return this.saveAsImage(file, startPage, -1, identifier);
	}

	/**
	 * Save each page of the pdf as image
	 *
	 * @param file InputStream of the pdf-file
	 * @param startPage Starting page number of the document
	 * @param endPage Ending page number of the document
	 * @param identifier Identifier for resulting images
	 * @return List list of image file names with absolute path
	 * @throws java.io.IOException when file is not found.
	 */
	public List<String> savePdfAsImage(InputStream file, int startPage, int endPage, String identifier) throws IOException{
		return this.saveAsImage(file, startPage, endPage, identifier);
	}

	/**
	 * Save each page of the pdf as image
	 *
	 * @param file InputStream of the pdf-file
	 * @param identifier Identifier for resulting images
	 * @return List list of image file names with absolute path
	 * @throws java.io.IOException when file is not found.
	 */
	public List<String> savePdfAsImage(InputStream file, String identifier) throws IOException{
		return this.saveAsImage(file, -1, -1, identifier);
	}

	/**
     * This method saves the each page of the pdf as image
     */
    private List<String> saveAsImage(String file, int startPage, int endPage) throws IOException {
        logger.info("file : " + file);
        File sourceFile = new File(file);
        String identifier = fileNameToIdentifier(sourceFile);
        try(FileInputStream fis1 = new FileInputStream(sourceFile)) {
            return saveAsImage(fis1, startPage, endPage, identifier);
        }
    }

    private List<String> saveAsImage(InputStream file, int startPage, int endPage, String identifier) throws IOException {
        PDDocument doc = null;
        try {
            doc = PDDocument.load(file);
            return saveAsImage(doc, startPage, endPage, identifier);
        } finally {
			closeDocument(doc);
		}
    }

    private List<String> saveAsImage(PDDocument document, int startPage, int endPage, String identifier) throws IOException{

		logger.info("startPage : " + startPage);
		logger.info("endPage : " + endPage);

		ArrayList<String> imgNames = new ArrayList<String>();

		try {
			PageBounds pageBounds = this.getStartAndEndPages(document, startPage, endPage);

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for(int iPage=pageBounds.startPage-1;iPage<pageBounds.endPage;iPage++){
				logger.info("Page No : " + (iPage+1));
				// TODO
				String pageIdentifier = identifier + "_" + (iPage + 1) + ".png";
				BufferedImage image = pdfRenderer.renderImageWithDPI(iPage, 300, ImageType.RGB);
				getPdfUtilImageListener().imageGenerated(image, pageIdentifier);
//				ImageIOUtil.writeImage(image, fname , 300);
				imgNames.add((getImageDestinationPath() != null ? getImageDestinationPath() : "") + pageIdentifier);
				if(getImageDestinationPath() != null) {
					logger.info("PDf Page saved as image : " + getImageDestinationPath() + pageIdentifier);
				}
			}
			document.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return imgNames;
	}

	/**
	 * Compare 2 pdf documents pixel by pixel for the content and format.
	 *
	 * @param file1 Absolute file path of the expected file
	 * @param file2 Absolute file path of the actual file
	 * @param startPage Starting page number of the document
	 * @param endPage Ending page number of the document
	 * @param highlightImageDifferences To highlight differences in the images
	 * @param showAllDifferences To compare all the pages of the PDF (by default as soon as a mismatch is found in a page, this method exits)
	 * @return boolean true if matches, false otherwise
	 * @throws java.io.IOException when file is not found.
	 */
	public boolean compare(String file1, String file2,int startPage, int endPage, boolean highlightImageDifferences, boolean showAllDifferences) throws IOException{
		this.compareMode = CompareMode.VISUAL_MODE;
		this.bHighlightPdfDifference = highlightImageDifferences;
		this.bCompareAllPages = showAllDifferences;
		return this.comparePdfByImage(file1, file2, startPage, endPage);
	}

	/**
	 * Compare 2 pdf documents pixel by pixel for the content and format.
	 *
	 * @param file1 InputStream of the expected pdf-file
	 * @param file2 InputStream of the actual pdf-file
	 * @param startPage Starting page number of the document
	 * @param endPage Ending page number of the document
	 * @param highlightImageDifferences To highlight differences in the images
	 * @param showAllDifferences To compare all the pages of the PDF (by default as soon as a mismatch is found in a page, this method exits)
	 * @param identifier Identifier for resulting images
	 * @return boolean true if matches, false otherwise
	 * @throws java.io.IOException when file is not found.
	 */
	public boolean compare(InputStream file1, InputStream file2,int startPage, int endPage, boolean highlightImageDifferences, boolean showAllDifferences, String identifier) throws IOException{
		this.compareMode = CompareMode.VISUAL_MODE;
		this.bHighlightPdfDifference = highlightImageDifferences;
		this.bCompareAllPages = showAllDifferences;
		return this.comparePdfByImage(file1, file2, startPage, endPage, identifier);
	}

	/**
   * This method reads each page of a given doc, converts to image
   * compare. If it fails, exits immediately.
   */
   private boolean comparePdfByImage(String file1, String file2, int startPage, int endPage) throws IOException {
	   File file1AsFile = new File(file1);
	   String identifier = fileNameToIdentifier(file1AsFile);
   	   if (this.bHighlightPdfDifference) {
           this.createImageDestinationDirectory(file2);
       }
       try (FileInputStream fis1 = new FileInputStream(file1AsFile);
            FileInputStream fis2 = new FileInputStream(file2)) {
           return comparePdfByImage(fis1, fis2, startPage, endPage, identifier);
       }
   }

	private String fileNameToIdentifier(File file1AsFile) {
		return file1AsFile.getName().replace(".pdf", "");
	}

	private boolean comparePdfByImage(InputStream file1, InputStream file2, int startPage, int endPage, String identifier) throws IOException {
        PDDocument document1 = null;
        PDDocument document2 = null;
        try {
            document1 = PDDocument.load(file1);
            document2 = PDDocument.load(file2);
            return comparePdfByImage(document1, document2, startPage, endPage, identifier);
        } finally {
			closeDocuments(document1, document2);
		}
    }

    private boolean comparePdfByImage(PDDocument document1, PDDocument document2, int startPage, int endPage, String identifier) throws IOException {
        logger.info("file1 : " + document1);
        logger.info("file2 : " + document2);

        int pgCount1 = this.getPageCount(document1);
        int pgCount2 = this.getPageCount(document2);

        if (pgCount1 != pgCount2) {
            logger.warning("files page counts do not match - returning false");
            return false;
        }

        PageBounds pageBounds = this.getStartAndEndPages(document1, startPage, endPage);

        return this.convertToImageAndCompare(document1, document2, pageBounds.startPage, pageBounds.endPage, identifier);
    }

	private boolean convertToImageAndCompare(String file1, String file2, int startPage, int endPage) throws IOException {
		File file1AsFile = new File(file1);
		String identifier = fileNameToIdentifier(file1AsFile);
		try(FileInputStream fis1 = new FileInputStream(file1AsFile);
				FileInputStream fis2 = new FileInputStream(new File(file2))) {
			return convertToImageAndCompare(fis1, fis2, startPage, endPage, identifier);
		}
	}

	private boolean convertToImageAndCompare(InputStream file1, InputStream file2, int startPage, int endPage, String identifier) throws IOException {
		PDDocument document1 =  null;
		PDDocument document2 =  null;
		try {
			document1 = PDDocument.load(file1);
			document2 = PDDocument.load(file2);
			return convertToImageAndCompare(document1, document2, startPage, endPage, identifier);
		} finally {
			closeDocuments(document1, document2);
		}
	}

	private void closeDocuments(PDDocument document1, PDDocument document2) throws IOException {
		try {
            closeDocument(document1);
        } finally {
            closeDocument(document2);
        }
	}

	private void closeDocument(PDDocument document) throws IOException {
		if (document != null)
            document.close();
	}

	private boolean convertToImageAndCompare(PDDocument document1, PDDocument document2, int startPage, int endPage, String identifier) throws IOException{
        boolean result = true;

        PDFRenderer pdfRenderer1 = new PDFRenderer(document1);
        PDFRenderer pdfRenderer2 = new PDFRenderer(document2);

            for(int iPage=startPage-1;iPage<endPage;iPage++){

                String pageIdentifier = identifier + "_" + (iPage + 1) + "_diff.png";

                logger.info("Comparing Page No : " + (iPage+1));
                BufferedImage image1 = pdfRenderer1.renderImageWithDPI(iPage, 300, ImageType.RGB);
                BufferedImage image2 = pdfRenderer2.renderImageWithDPI(iPage, 300, ImageType.RGB);
                result = ImageUtil.compareAndHighlight(image1, image2, pageIdentifier, getPdfUtilImageListener(), this.bHighlightPdfDifference, this.imgColor.getRGB()) && result;
                if(!this.bCompareAllPages && !result){
                    break;
                }
            }

		return result;
	}

	private PdfUtilImageListener getPdfUtilImageListener() {
		return new PdfUtilImageListener() {
            @Override
            public void imageGenerated(BufferedImage bufferedImage, String fileName) {
                if(getImageDestinationPath() != null) {
                    ImageUtil.saveImage(bufferedImage, getImageDestinationPath() + "/" + fileName);
                }
                if(getImageListener() != null &&
                    getImageListener() != this) {
                    getImageListener().imageGenerated(bufferedImage, fileName);
                }
            }
        };
	}


   /**
   * Extract all the embedded images from the pdf document
   *
   * @param file Absolute file path of the file
   * @param startPage Starting page number of the document
   * @return List list of image file names with absolute path
   * @throws java.io.IOException when file is not found.
   */
	public List<String> extractImages(String file, int startPage) throws IOException{
		return this.extractimages(file, startPage, -1);
	}

   /**
   * Extract all the embedded images from the pdf document
   *
   * @param file Absolute file path of the file
   * @param startPage Starting page number of the document
   * @param endPage Ending page number of the document
   * @return List list of image file names with absolute path
   * @throws java.io.IOException when file is not found.
   */
	public List<String> extractImages(String file, int startPage, int endPage) throws IOException{
		return this.extractimages(file, startPage, endPage);
	}

   /**
   * Extract all the embedded images from the pdf document
   *
   * @param file Absolute file path of the file
   * @return List list of image file names with absolute path
   * @throws java.io.IOException when file is not found.
   */
	public List<String> extractImages(String file) throws IOException{
		return this.extractimages(file, -1, -1);
	}

	/**
	 * Extract all the embedded images from the pdf document
	 *
	 * @param file InputStream of the pdf-file
	 * @param startPage Starting page number of the document
	 * @param identifier Identifier for resulting images
	 * @return List list of image file names with absolute path
	 * @throws java.io.IOException when file is not found.
	 */
	public List<String> extractImages(InputStream file, int startPage, String identifier) throws IOException{
		return this.extractimages(file, startPage, -1, identifier);
	}

	/**
	 * Extract all the embedded images from the pdf document
	 *
	 * @param file InputStream of the pdf-file
	 * @param startPage Starting page number of the document
	 * @param endPage Ending page number of the document
	 * @param identifier Identifier for resulting images
	 * @return List list of image file names with absolute path
	 * @throws java.io.IOException when file is not found.
	 */
	public List<String> extractImages(InputStream file, int startPage, int endPage, String identifier) throws IOException{
		return this.extractimages(file, startPage, endPage, identifier);
	}

	/**
	 * Extract all the embedded images from the pdf document
	 *
	 * @param file InputStream of the pdf-file
	 * @param identifier Identifier for resulting images
	 * @return List list of image file names with absolute path
	 * @throws java.io.IOException when file is not found.
	 */
	public List<String> extractImages(InputStream file, String identifier) throws IOException{
		return this.extractimages(file, -1, -1, identifier);
	}


	/**
   * This method extracts all the embedded images of the pdf document
   */
	private List<String> extractimages(String file, int startPage, int endPage) throws IOException {
		File fileAsFile = new File(file);
		logger.info("file : " + file);
		this.createImageDestinationDirectory(file);
		try(FileInputStream fis1 = new FileInputStream(fileAsFile)) {
			return extractimages(fis1, startPage, endPage, fileNameToIdentifier(fileAsFile) + "_resource");
		}
	}

	private List<String> extractimages(InputStream file, int startPage, int endPage, String identifier) throws IOException {
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
			return extractimages(doc, startPage, endPage, identifier);
		} finally {
			closeDocument(doc);
		}
	}

	private List<String> extractimages(PDDocument document, int startPage, int endPage, String identifier) throws IOException {
		logger.info("startPage : " + startPage);
		logger.info("endPage : " + endPage);

		ArrayList<String> imgNames = new ArrayList<String>();
		boolean bImageFound = false;

		PDPageTree list = document.getPages();

		PageBounds pageBounds = this.getStartAndEndPages(document, startPage, endPage);

		int totalImages = 1;
			for(int iPage=pageBounds.startPage-1;iPage<pageBounds.endPage;iPage++){
				logger.info("Page No : " + (iPage+1));
				PDResources pdResources = list.get(iPage).getResources();
				for (COSName c : pdResources.getXObjectNames()) {
		            PDXObject o = pdResources.getXObject(c);
		            if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
		            	bImageFound = true;
						String imageIdentifier = identifier + "_" + totalImages + ".png";
						try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
							ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) o).getImage(), "png", baos);
							baos.flush();
							try(InputStream in = new ByteArrayInputStream(baos.toByteArray())) {
								BufferedImage image = ImageIO.read(in);
								getPdfUtilImageListener().imageGenerated(image, imageIdentifier);
							}
						}
		                imgNames.add((getImageDestinationPath() != null ? getImageDestinationPath() : "") + imageIdentifier);
		                totalImages++;
		            }
		        }
			}
			document.close();
			if(bImageFound) {
				if(getImageDestinationPath() != null) {
					logger.info("Images are saved @ " + getImageDestinationPath());
				}
			} else
				logger.info("No images were found in the PDF");
		return imgNames;
	}

	private void createImageDestinationDirectory(String file) throws IOException{
		if(null==this.imageDestinationPath){
			File sourceFile = new File(file);
			String destinationDir = sourceFile.getParent() + "/temp/";
			this.imageDestinationPath=destinationDir;
			this.createFolder(destinationDir);
		}
	}

	private boolean createFolder(String dir) throws IOException{
	    FileUtils.deleteDirectory(new File(dir));
		return new File(dir).mkdir();
	}

	private PageBounds getStartAndEndPages(InputStream file, int start, int end) throws IOException{
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
			return getStartAndEndPages(doc, start, end);
		} finally {
			closeDocument(doc);
		}
	}

	private PageBounds getStartAndEndPages(PDDocument document, int start, int end) throws IOException{
		int pagecount = document.getNumberOfPages();
		logger.info("Page Count : " + pagecount);
		logger.info("Given start page:" + start);
		logger.info("Given end   page:" + end);

		PageBounds pageBounds = new PageBounds(start, end);
		if((start > 0 && start <= pagecount)){
			pageBounds.startPage = start;
		}else{
			pageBounds.startPage = 1;
		}
		if((end > 0 && end >= start && end <= pagecount)){
			pageBounds.endPage = end;
		}else{
			pageBounds.endPage = pagecount;
		}
		logger.info("Updated start page:" + pageBounds.startPage);
		logger.info("Updated end   page:" + pageBounds.endPage);
		return pageBounds;
	}

	private class PageBounds {
		int startPage;
		int endPage;

		public PageBounds(int startPage, int endPage) {
			this.startPage = startPage;
			this.endPage = endPage;
		}
	}
}