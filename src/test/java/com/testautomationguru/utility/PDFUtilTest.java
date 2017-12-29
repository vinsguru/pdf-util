package com.testautomationguru.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.text.PDFTextStripper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PDFUtilTest {

    private static final Charset TEXT_RESOURCE_CHARSET = Charset.forName("UTF-8");
    PDFUtil pdfutil = new PDFUtil();

    @Test(priority = 1)
    public void checkForPDFPageCount() throws IOException {
        int actual = pdfutil.getPageCount(getFilePath("image-extract/sample.pdf"));
        Assert.assertEquals(actual, 6);
    }

    @Test(priority = 2)
    public void checkForFileContent() throws IOException {
        String actual = pdfutil.getText(getFilePath("text-extract/sample.pdf"));
        String expected = getTextResource("text-extract/expected.txt");
        Assert.assertEquals(actual.trim(), expected.trim());
    }

    @Test(priority = 3)
    public void checkForFileContentUsingStripper() throws IOException {
        String actual = pdfutil.getText(getFilePath("text-extract-position/sample.pdf"));
        String expected = getTextResource("text-extract-position/expected.txt");
        Assert.assertNotEquals(actual.trim(), expected.trim());
        
        //should match with stripper
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        pdfutil.useStripper(stripper);
        actual = pdfutil.getText(getFilePath("text-extract-position/sample.pdf"));
        expected = getTextResource("text-extract-position/expected.txt");
        Assert.assertEquals(actual.trim(), expected.trim());
        pdfutil.useStripper(null);   
    }

    @Test(priority = 4)
    public void extractImages() throws IOException {
        List<String> actualExtractedImages = pdfutil.extractImages(getFilePath("image-extract/sample.pdf"));
        Assert.assertEquals(actualExtractedImages.size(), 7);
    }

    @Test(priority = 5)
    public void saveAsImages() throws IOException {
        List<String> actualExtractedImages = pdfutil.savePdfAsImage(getFilePath("image-extract/sample.pdf"));
        Assert.assertEquals(actualExtractedImages.size(), 6);
    }

    @Test(priority = 6)
    public void comparePDFTextModeDiff() throws IOException {
        String file1 = getFilePath("text-compare/sample1.pdf");
        String file2 = getFilePath("text-compare/sample2.pdf");
        pdfutil.setCompareMode(CompareMode.TEXT_MODE);

        boolean result = pdfutil.compare(file1, file2);
        Assert.assertFalse(result);
    }

    @Test(priority = 7)
    public void comparePDFTextModeSameAfterExcludePattern() throws IOException {
        String file1 = getFilePath("text-compare/sample1.pdf");
        String file2 = getFilePath("text-compare/sample2.pdf");
        pdfutil.setCompareMode(CompareMode.TEXT_MODE);
        pdfutil.excludeText("\\d+");
        // pdfutil.excludeText("1999","1998");
        boolean result = pdfutil.compare(file1, file2);
        Assert.assertTrue(result);
    }

    @Test(priority = 8)
    public void comparePDFImageModeSame() throws IOException {
        String file1 = getFilePath("image-compare-same/sample1.pdf");
        String file2 = getFilePath("image-compare-same/sample2.pdf");
        pdfutil.setCompareMode(CompareMode.VISUAL_MODE);

        boolean result = pdfutil.compare(file1, file2);
        Assert.assertTrue(result);
    }

    @Test(priority = 9)
    public void comparePDFImageModeDiff() throws IOException {
        pdfutil.highlightPdfDifference(true);
        String file1 = getFilePath("image-compare-diff/sample1.pdf");
        String file2 = getFilePath("image-compare-diff/sample2.pdf");
        boolean result = pdfutil.compare(file1, file2);
        Assert.assertFalse(result);
    }

    @Test(priority = 10)
    public void comparePDFImageModeDiffSpecificPage() throws IOException {
        pdfutil.highlightPdfDifference(true);
        String file1 = getFilePath("image-compare-diff/sample1.pdf");
        String file2 = getFilePath("image-compare-diff/sample2.pdf");
        boolean result = pdfutil.compare(file1, file2, 3);
        Assert.assertTrue(result);
    }

    private String getTextResource(String resourceName) throws IOException {
        try(FileInputStream fis = new FileInputStream(getFilePath(resourceName));
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(fis, baos);
            return new String(baos.toByteArray(), TEXT_RESOURCE_CHARSET);
        }
    }

    private String getFilePath(String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile()).getAbsolutePath();
    }
}
