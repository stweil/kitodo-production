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

package org.kitodo.production.version;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class KitodoVersion {

    private static String version = "N/A";
    private static String buildVersion = "N/A";
    private static String buildDate = "N/A";

    /**
     * Private constructor to hide the implicit public one.
     */
    private KitodoVersion() {

    }

    /**
     * Setup KitodoVersion form manifest.
     *
     * @param manifest as Manifest
     */
    public static void setupFromManifest(Manifest manifest) {
        Attributes mainAttributes = manifest.getMainAttributes();

        version = getValueOrThrowException(mainAttributes, "Implementation-Version");
        buildVersion = version;
        buildDate = getValueOrThrowException(mainAttributes, "Implementation-Build-Date");
    }

    private static String getValueOrThrowException(Attributes attributes, String attributeName) {
        String value = attributes.getValue(attributeName);
        if (null == value) {
            throw new IllegalArgumentException(
                    "Manifest does not contain " + attributeName + ". The build may be corrupted.");
        }
        return value;
    }

    public static String getVersion() {
        return version;
    }

    public static String getBuildVersion() {
        return buildVersion;
    }

    public static String getBuildDate() {
        return buildDate;
    }
}
