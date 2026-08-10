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

package org.kitodo.production.forms.massimport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.primefaces.event.FileUploadEvent;

public class MassImportFormTest {

    @Test
    public void testHandleFileUploadWithNoFile() {
        MassImportForm massImportForm = mock(MassImportForm.class, Mockito.CALLS_REAL_METHODS);
        FileUploadEvent event = mock(FileUploadEvent.class);
        when(event.getFile()).thenReturn(null);
        assertDoesNotThrow(() -> massImportForm.handleFileUpload(event));
        assertNull(massImportForm.getFile(), "File must not be set when no file was uploaded");
    }
}
