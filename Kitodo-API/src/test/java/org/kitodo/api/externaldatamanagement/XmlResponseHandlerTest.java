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

package org.kitodo.api.externaldatamanagement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.kitodo.exceptions.ConfigException;

class XmlResponseHandlerTest {

    private static final String VALID_SRU_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<srw:searchRetrieveResponse xmlns:srw=\"http://www.loc.gov/zing/srw/\">\n"
            + "    <srw:numberOfRecords>1</srw:numberOfRecords>\n"
            + "    <srw:records>\n"
            + "        <srw:record>\n"
            + "            <srw:recordSchema>mods</srw:recordSchema>\n"
            + "            <srw:recordData>data</srw:recordData>\n"
            + "        </srw:record>\n"
            + "    </srw:records>\n"
            + "</srw:searchRetrieveResponse>";

    private static final String XML_WITH_DOCTYPE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>\n<root>&xxe;</root>";

    @Test
    void checkRecordFoundShouldAcceptValidSruResponse() {
        assertDoesNotThrow(() -> XmlResponseHandler.checkRecordFound(SearchInterfaceType.SRU, VALID_SRU_RESPONSE, "1"));
    }

    @Test
    void checkRecordFoundShouldRejectDocTypeDeclaration() {
        assertThrows(ConfigException.class,
                () -> XmlResponseHandler.checkRecordFound(SearchInterfaceType.SRU, XML_WITH_DOCTYPE, "1"));
    }
}
