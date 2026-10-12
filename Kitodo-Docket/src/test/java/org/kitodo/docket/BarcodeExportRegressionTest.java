/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.docket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Regression test for the barcode4j FOP extension plugin.
 *
 * <p>The docket export renders code128 barcodes by delegating the
 * {@code <barcode:barcode>} elements of the XSLT to the barcode4j plugin,
 * which is loaded by FOP at runtime. If the bundled Apache FOP version
 * breaks the plugin (e.g. because the FOP internal APIs the plugin hooks
 * into change), the barcodes silently disappear from the generated run
 * notes. This test renders a docket containing two barcodes and asserts
 * that both are present in the resulting PDF.</p>
 *
 * <p>Barcode detection is done on the rasterized page: a barcode shows up
 * as a tall band of rows with many dark/light transitions, which text
 * lines and rule borders do not reach.</p>
 */
public class BarcodeExportRegressionTest {

    private static final int RENDER_DPI = 150;
    private static final int MIN_TRANSITIONS = 40;
    private static final int MIN_BARCODE_BAND_HEIGHT = 40;

    private DocketDataGenerator docketDataGenerator;

    @BeforeEach
    public void initialize() {
        this.docketDataGenerator = new DocketDataGenerator();
    }

    @Test
    public void testBothBarcodesAreRendered() throws Exception {
        URI xslFile = new File("src/test/resources/docket_barcode.xsl").toURI();
        Docket docket = new Docket();
        File generated = docket.generateDocket(
                docketDataGenerator.createDocketData("processID", "AZ-234", "manuscript"), xslFile);

        int barcodeBands = countBarcodeBands(generated);

        assertEquals(2, barcodeBands, "expected 2 rendered barcodes in the docket");
    }

    /**
     * Renders the first page of the given PDF and counts the number of
     * vertical bands that look like a barcode.
     *
     * @param pdf
     *            the generated docket PDF
     * @return the number of barcode-like bands
     */
    private int countBarcodeBands(File pdf) throws Exception {
        int height;
        BufferedImage image;
        try (PDDocument document = Loader.loadPDF(pdf)) {
            PDFRenderer renderer = new PDFRenderer(document);
            image = renderer.renderImageWithDPI(0, RENDER_DPI);
            height = image.getHeight();
        }

        int[] transitions = new int[height];
        for (int y = 0; y < height; y++) {
            int previous = -1;
            int count = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                int gray = (image.getRGB(x, y) >> 16) & 0xFF;
                int value = gray < 128 ? 0 : 1;
                if (previous >= 0 && value != previous) {
                    count++;
                }
                previous = value;
            }
            transitions[y] = count;
        }

        int bands = 0;
        int bandStart = -1;
        for (int y = 0; y <= height; y++) {
            boolean high = y < height && transitions[y] >= MIN_TRANSITIONS;
            if (high && bandStart < 0) {
                bandStart = y;
            } else if (!high && bandStart >= 0) {
                if (y - bandStart >= MIN_BARCODE_BAND_HEIGHT) {
                    bands++;
                }
                bandStart = -1;
            }
        }
        return bands;
    }
}
