package com.testautomationguru.utility;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.text.PDFTextStripper;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import lombok.val;

public class PDFUtilTest {

	private PDFUtil pdfutil = new PDFUtil();

	@Test(priority = 1)
	public void checkForPDFPageCount() throws IOException {
		val actual = pdfutil.getPageCount(getFilePath("image-extract/sample.pdf"));
		assertEquals(actual, 6);
	}

	@Test(priority = 2)
	public void checkForFileContent() throws IOException {
		val actual = pdfutil.getText(getFilePath("text-extract/sample.pdf"));
		val expected = Files.readFile(new File(getFilePath("text-extract/expected.txt")));
		assertEquals(actual.trim(), expected.trim());
	}

	@Test(priority = 2)
	public void checkForFileContentOnPosition() throws IOException {
		val actual = pdfutil.getText(getFilePath("text-extract-position/sample.pdf"));
		val expected = Files.readFile(new File(getFilePath("text-extract-position/expected.txt")));
		assertNotEquals(actual.trim(), expected.trim());
	}

	@Test(priority = 3)
	public void checkForFileContentUsingStripper() throws IOException {
		// should match with stripper
		val stripper = new PDFTextStripper();
		stripper.setSortByPosition(true);
		pdfutil.useStripper(stripper);
		val actual = pdfutil.getText(getFilePath("text-extract-position/sample.pdf"));
		val expected = Files.readFile(new File(getFilePath("text-extract-position/expected.txt")));
		assertEquals(actual.trim(), expected.trim());
		pdfutil.useStripper(null);
	}

	@Test(priority = 4)
	public void extractImages() throws IOException {
		val actualExtractedImages = pdfutil.extractImages(getFilePath("image-extract/sample.pdf"));
		assertEquals(actualExtractedImages.size(), 7);
	}

	@Test(priority = 5)
	public void saveAsImages() throws IOException {
		val actualExtractedImages = pdfutil.savePdfAsImage(getFilePath("image-extract/sample.pdf"));
		assertEquals(actualExtractedImages.size(), 6);
	}

	@Test(priority = 6)
	public void comparePDFTextModeDiff() throws IOException {
		val file1 = getFilePath("text-compare/sample1.pdf");
		val file2 = getFilePath("text-compare/sample2.pdf");
		pdfutil.setCompareMode(CompareMode.TEXT_MODE);
		val result = pdfutil.compare(file1, file2);
		assertFalse(result);
	}

	@Test(priority = 7)
	public void comparePDFTextModeSameAfterExcludePattern() throws IOException {
		val file1 = getFilePath("text-compare/sample1.pdf");
		val file2 = getFilePath("text-compare/sample2.pdf");
		pdfutil.setCompareMode(CompareMode.TEXT_MODE);
		pdfutil.excludeText("\\d+");
		// pdfutil.excludeText("1999","1998");
		val result = pdfutil.compare(file1, file2);
		assertTrue(result);
	}

	@Test(priority = 8)
	public void comparePDFImageModeSame() throws IOException {
		val file1 = getFilePath("image-compare-same/sample1.pdf");
		val file2 = getFilePath("image-compare-same/sample2.pdf");
		pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
		val result = pdfutil.compare(file1, file2);
		assertTrue(result);
	}

	@Test(priority = 9)
	public void comparePDFImageModeDiff() throws IOException {
		pdfutil.highlightPdfDifference(true);
		val file1 = getFilePath("image-compare-diff/sample1.pdf");
		val file2 = getFilePath("image-compare-diff/sample2.pdf");
		val result = pdfutil.compare(file1, file2);
		assertFalse(result);
	}

	@Test(priority = 10)
	public void comparePDFImageModeDiffSpecificPage() throws IOException {
		pdfutil.highlightPdfDifference(true);
		val file1 = getFilePath("image-compare-diff/sample1.pdf");
		val file2 = getFilePath("image-compare-diff/sample2.pdf");
		val result = pdfutil.compare(file1, file2, 3);
		assertTrue(result);
	}

	private String getFilePath(String filename) {
		return new File(getClass().getClassLoader().getResource(filename).getFile()).getAbsolutePath();
	}
}
