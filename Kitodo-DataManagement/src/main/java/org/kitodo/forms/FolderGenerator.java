/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.kitodo.forms;

import java.util.Objects;

import org.kitodo.data.database.beans.Folder;

/**
 * An encapsulation to access the generator properties of the folder.
 */
public class FolderGenerator {
    /**
     * Generator method that changes the DPI of an image.
     */
    private static final String CHANGE_DPI = "changeDpi";

    /**
     * Generator method that creates a derivative for an image.
     */
    private static final String CREATE_DERIVATIVE = "createDerivative";

    /**
     * Generator method that changes the size (in pixel) of the image.
     */
    private static final String GET_SIZED_WEB_IMAGE = "getSizedWebImage";

    /**
     * Multiplier used to display percentage.
     */
    private static final double PERCENT = 100.0d;

    /**
     * Image resoulution in DPI.
     */
    private int dpi = 300;

    /**
     * Scale factor.
     */
    private double factor = 1.00d;

    /**
     * {@code Folder.this}.
     */
    private final Folder folder;

    /**
     * Image width in pixels.
     */
    private int width = 150;

    /**
     * Creates a new generator for this folder.
     *
     * @param folder
     *            {@code Folder.this}
     */
    public FolderGenerator(Folder folder) {
        this.folder = folder;
        if (Objects.nonNull(folder)) {
            this.width = folder.getImageSize().orElse(width);
            this.factor = folder.getDerivative().orElse(factor);
            this.dpi = folder.getDpi().orElse(dpi);
        }
    }

    /**
     * Returns the image resolution in DPI.
     *
     * @return the DPI
     */
    public int getDpi() {
        return dpi;
    }

    /**
     * Returns the scale factor to set.
     *
     * @return the scale factor
     */
    public double getFactor() {
        return factor * PERCENT;
    }

    /**
     * Returns the selected generator method.
     *
     * @return the generator methodd
     */
    public String getMethod() {
        if (folder.getDerivative().isPresent()) {
            return CREATE_DERIVATIVE;
        } else if (folder.getDpi().isPresent()) {
            return CHANGE_DPI;
        } else if (folder.getImageSize().isPresent()) {
            return GET_SIZED_WEB_IMAGE;
        } else {
            return "";
        }
    }

    /**
     * Sets the image width in pixels.
     *
     * @return the image width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the image resolution in DPI.
     *
     * @param dpi
     *            resolution to set
     */
    public void setDpi(int dpi) {
        this.dpi = dpi;
        setMethod(getMethod());
    }

    /**
     * Sets the scale factor.
     *
     * @param factor
     *            scale factor to set
     */
    public void setFactor(double factor) {
        this.factor = factor / PERCENT;
        setMethod(getMethod());
    }

    /**
     * Sets the generator method.
     *
     * @param method
     *            method to set
     */
    public void setMethod(String method) {
        folder.setDerivative(method.equals(CREATE_DERIVATIVE) ? factor : null);
        folder.setDpi(method.equals(CHANGE_DPI) ? dpi : null);
        folder.setImageSize(method.equals(GET_SIZED_WEB_IMAGE) ? width : null);
    }

    /**
     * Sets the image width in pixels.
     *
     * @param width
     *            image width
     */
    public void setWidth(int width) {
        this.width = width;
        setMethod(getMethod());
    }
}
