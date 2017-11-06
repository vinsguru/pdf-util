# PDF Compare Utility

MVN Dependency:
===============

```
<dependency>
   <groupId>com.testautomationguru.pdfutil</groupId>
   <artifactId>pdf-util</artifactId>
   <version>0.0.2</version>
</dependency>
```


Getting `pdfutil.jar`
====================

Download this jar [here](http://www.testautomationguru.com/introducing-pdfutil-to-compare-pdf-files-extract-resources/).


# Usage

* To get page count

```
import com.testautomationguru.utility.PDFUtil;
 
PDFUtil pdfUtil = new PDFUtil();
pdfUtil.getPageCount("c:/sample.pdf"); //returns the page count
```

* To get page content as plain text

```
//returns the pdf content - all pages
pdfUtil.getText("c:/sample.pdf");
 
// returns the pdf content from page number 2
pdfUtil.getText("c:/sample.pdf",2);
 
// returns the pdf content from page number 5 to 8
pdfUtil.getText("c:/sample.pdf", 5, 8);

```

* To extract attached images from PDF
```
//set the path where we need to store the images
 pdfUtil.setImageDestinationPath("c:/imgpath");
 pdfUtil.extractImages("c:/sample.pdf");
 
// extracts &amp; saves the pdf content from page number 3
pdfUtil.extractImages("c:/sample.pdf", 3);
 
// extracts &amp; saves the pdf content from page 2
pdfUtil.extractImages("c:/sample.pdf", 2, 2);

```


* To store PDF pages as images

```
//set the path where we need to store the images
 pdfUtil.setImageDestinationPath("c:/imgpath");
 pdfUtil.savePdfAsImage("c:/sample.pdf");
```

* To compare PDF files in text mode (faster – But it does not compare the format, images etc in the PDF)

```
String file1="c:/files/doc1.pdf";
String file1="c:/files/doc2.pdf";
 
// compares the pdf documents &amp; returns a boolean
// true if both files have same content. false otherwise.
pdfUtil.compare(file1, file2);
 
// compare the 3rd page alone
pdfUtil.compare(file1, file2, 3, 3);
 
// compare the pages from 1 to 5
pdfUtil.compare(file1, file2, 1, 5);
```
* To exclude certain text while comparing PDF files in text mode

```
String file1="c:/files/doc1.pdf";
String file1="c:/files/doc2.pdf";
 
//pass all the possible texts to be removed before comparing
pdfutil.excludeText("1998", "testautomation");
 
//pass regex patterns to be removed before comparing
// \\d+ removes all the numbers in the pdf before comparing
pdfutil.excludeText("\\d+");
 
// compares the pdf documents &amp; returns a boolean
// true if both files have same content. false otherwise.
pdfUtil.compare(file1, file2);
 
// compare the 3rd page alone
pdfUtil.compare(file1, file2, 3, 3);
 
// compare the pages from 1 to 5
pdfUtil.compare(file1, file2, 1, 5);
```
* To compare PDF files in Visual mode (slower – compares PDF documents pixel by pixel – highlights pdf difference & store the result as image)

```
String file1="c:/files/doc1.pdf";
String file1="c:/files/doc2.pdf";
 
// compares the pdf documents &amp; returns a boolean
// true if both files have same content. false otherwise.
// Default is CompareMode.TEXT_MODE
pdfUtil.setCompareMode(CompareMode.VISUAL_MODE);
pdfUtil.compare(file1, file2);
 
// compare the 3rd page alone
pdfUtil.compare(file1, file2, 3, 3);
 
// compare the pages from 1 to 5
pdfUtil.compare(file1, file2, 1, 5);
 
//if you need to store the result
pdfUtil.highlightPdfDifference(true);
pdfUtil.setImageDestinationPath("c:/imgpath");
pdfUtil.compare(file1, file2);
```


* For example, I have 2 PDF documents which have exact same content except the below differences in the charts.
![pdf1](http://i0.wp.com/www.testautomationguru.com/wp-content/uploads/2015/06/pdfu001.png) ![pdf2](http://i2.wp.com/www.testautomationguru.com/wp-content/uploads/2015/06/pdfu002.png)

The difference is shown as 
![diff](http://i1.wp.com/www.testautomationguru.com/wp-content/uploads/2015/06/pdfu003.png)
