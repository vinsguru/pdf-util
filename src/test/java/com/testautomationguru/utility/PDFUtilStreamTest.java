package com.testautomationguru.utility;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.text.PDFTextStripper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class PDFUtilStreamTest {

    private static final Charset TEXT_RESOURCE_CHARSET = Charset.forName("UTF-8");
    PDFUtil pdfutil = new PDFUtil();

    @Test(priority = 1)
    public void checkForPDFPageCount() throws IOException {
        String identifier = "image-extract/sample";
        try(FileInputStream fis = getFileInputStream(identifier + ".pdf")) {
            int actual = pdfutil.getPageCount(fis);
            Assert.assertEquals(actual, 6);
        }
    }

    @Test(priority = 2)
    public void checkForFileContent() throws IOException {
        String identifier = "text-extract/sample";
        try(FileInputStream fis = getFileInputStream(identifier + ".pdf")) {
            String actual = pdfutil.getText(fis);
            String expected = getTextResource("text-extract/expected.txt");
            Assert.assertEquals(actual.trim(), expected.trim());
        }
    }

    @Test(priority = 3)
    public void checkForFileContentUsingStripper() throws IOException {
        String identifier = "text-extract-position/sample";
        try(FileInputStream fis = getFileInputStream(identifier + ".pdf")) {
            String actual = pdfutil.getText(fis);
            String expected = getTextResource("text-extract-position/expected.txt");
            Assert.assertNotEquals(actual.trim(), expected.trim());
        }
        //should match with stripper
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        pdfutil.useStripper(stripper);
        try(FileInputStream fis = getFileInputStream(identifier + ".pdf")) {
            String actual = pdfutil.getText(fis);
            String expected = getTextResource("text-extract-position/expected.txt");
            Assert.assertEquals(actual.trim(), expected.trim());
        }
        pdfutil.useStripper(null);
    }

    @Test(priority = 4)
    public void extractImages() throws IOException {
        String identifier = "image-extract/sample";
        try(FileInputStream fis = getFileInputStream(identifier + ".pdf")) {
            List<String> actualExtractedImages = pdfutil.extractImages(fis, identifier);
            Assert.assertEquals(actualExtractedImages.size(), 7);
        }
    }

    @Test(priority = 5)
    public void saveAsImages() throws IOException {
        String identifier = "image-extract/sample";
        try(FileInputStream fis = getFileInputStream(identifier + ".pdf")) {
            List<String> actualExtractedImages = pdfutil.savePdfAsImage(fis, identifier);
            Assert.assertEquals(actualExtractedImages.size(), 6);
        }
    }

    @Test(priority = 6)
    public void comparePDFTextModeDiff() throws IOException {
        pdfutil.setCompareMode(CompareMode.TEXT_MODE);
        String identifier1 = "text-compare/sample1";
        String identifier2 = "text-compare/sample2";
        try(FileInputStream fis1 = getFileInputStream(identifier1 + ".pdf");
            FileInputStream fis2 = getFileInputStream(identifier2 + ".pdf")) {
            boolean result = pdfutil.compare(fis1, fis2, identifier1);
            Assert.assertFalse(result);
        }
    }

    @Test(priority = 7)
    public void comparePDFTextModeSameAfterExcludePattern() throws IOException {
        pdfutil.setCompareMode(CompareMode.TEXT_MODE);
        pdfutil.excludeText("\\d+");
        String identifier1 = "text-compare/sample1";
        String identifier2 = "text-compare/sample2";
        try(FileInputStream fis1 = getFileInputStream(identifier1 + ".pdf");
            FileInputStream fis2 = getFileInputStream(identifier2 + ".pdf")) {
            boolean result = pdfutil.compare(fis1, fis2, identifier1);
            Assert.assertTrue(result);
        }
    }

    @Test(priority = 8)
    public void comparePDFImageModeSame() throws IOException {
        pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
        String identifier1 = "image-compare-same/sample1";
        String identifier2 = "image-compare-same/sample2";
        try(FileInputStream fis1 = getFileInputStream(identifier1 + ".pdf");
            FileInputStream fis2 = getFileInputStream(identifier2 + ".pdf")) {
            boolean result = pdfutil.compare(fis1, fis2, identifier1);
            Assert.assertTrue(result);
        }
    }

    @Test(priority = 9)
    public void comparePDFImageModeDiff() throws IOException {
        pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
        pdfutil.highlightPdfDifference(true);
        String identifier1 = "image-compare-diff/sample1";
        String identifier2 = "image-compare-diff/sample2";
        try(FileInputStream fis1 = getFileInputStream(identifier1 + ".pdf");
            FileInputStream fis2 = getFileInputStream(identifier2 + ".pdf")) {
            boolean result = pdfutil.compare(fis1, fis2, identifier1);
            Assert.assertFalse(result);
        }
    }

    @Test(priority = 10)
    public void comparePDFImageModeDiffSpecificPage() throws IOException {
        pdfutil.highlightPdfDifference(true);
        String identifier1 = "image-compare-diff/sample1";
        String identifier2 = "image-compare-diff/sample2";
        try(FileInputStream fis1 = getFileInputStream(identifier1 + ".pdf");
            FileInputStream fis2 = getFileInputStream(identifier2 + ".pdf")) {
            boolean result = pdfutil.compare(fis1, fis2, 3, identifier1);
            Assert.assertTrue(result);
        }
    }

    private String getTextResource(String resourceName) throws IOException {
        try(FileInputStream fis = getFileInputStream(resourceName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(fis, baos);
            return new String(baos.toByteArray(), TEXT_RESOURCE_CHARSET);
        }
    }

    private FileInputStream getFileInputStream(String s) throws FileNotFoundException {
        return new FileInputStream(getFilePath(s));
    }

    private String getFilePath(String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile()).getAbsolutePath();
    }
}
