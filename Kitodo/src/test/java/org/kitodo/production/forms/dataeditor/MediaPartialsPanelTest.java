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

package org.kitodo.production.forms.dataeditor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.kitodo.api.dataeditor.rulesetmanagement.FunctionalDivision;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.api.dataformat.LogicalDivision;
import org.kitodo.api.dataformat.PhysicalDivision;
import org.kitodo.data.database.beans.Process;
import org.kitodo.production.services.dataeditor.DataEditorService;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import jakarta.faces.model.SelectItem;

public class MediaPartialsPanelTest {

    @Test
    public void testGetMediaPartialChildDivisionsOfSelectionWithNoSelection() {
        DataEditorForm dataEditorForm = mock(DataEditorForm.class);
        GalleryPanel galleryPanel = mock(GalleryPanel.class);
        when(dataEditorForm.getGalleryPanel()).thenReturn(galleryPanel);
        when(galleryPanel.getLastSelection()).thenReturn(null);

        MediaPartialsPanel mediaPartialsPanel = new MediaPartialsPanel(dataEditorForm);
        assertTrue(mediaPartialsPanel.getMediaPartialChildDivisionsOfSelection().isEmpty());
    }

    @Test
    public void testGetMediaPartialChildDivisionsOfSelectionWithMissingMediaContent() {
        DataEditorForm dataEditorForm = mock(DataEditorForm.class);
        GalleryPanel galleryPanel = mock(GalleryPanel.class);
        when(dataEditorForm.getGalleryPanel()).thenReturn(galleryPanel);
        when(galleryPanel.getLastSelection()).thenReturn(new ImmutablePair<>(new PhysicalDivision(), new LogicalDivision()));
        when(galleryPanel.getGalleryMediaContent(any(PhysicalDivision.class))).thenReturn(null);

        MediaPartialsPanel mediaPartialsPanel = new MediaPartialsPanel(dataEditorForm);
        assertTrue(mediaPartialsPanel.getMediaPartialChildDivisionsOfSelection().isEmpty());
    }

    @Test
    public void testGetMediaPartialChildDivisionsOfSelectionWithMediaPartialAllowed() {
        DataEditorForm dataEditorForm = mock(DataEditorForm.class);
        GalleryPanel galleryPanel = mock(GalleryPanel.class);
        when(dataEditorForm.getGalleryPanel()).thenReturn(galleryPanel);
        PhysicalDivision physicalDivision = new PhysicalDivision();
        when(galleryPanel.getLastSelection()).thenReturn(new ImmutablePair<>(physicalDivision, new LogicalDivision()));
        GalleryMediaContent galleryMediaContent = mock(GalleryMediaContent.class);
        when(galleryMediaContent.getMediaViewMimeType()).thenReturn("video/mp4");
        when(galleryPanel.getGalleryMediaContent(physicalDivision)).thenReturn(galleryMediaContent);
        RulesetManagementInterface rulesetManagement = mock(RulesetManagementInterface.class);
        when(dataEditorForm.getRulesetManagement()).thenReturn(rulesetManagement);
        when(rulesetManagement.getFunctionalDivisions(FunctionalDivision.MEDIA_PARTIAL))
                .thenReturn(Set.of("MediaPartial"));
        when(dataEditorForm.getProcess()).thenReturn(new Process());

        try (MockedStatic<DataEditorService> dataEditorServiceMockedStatic = Mockito.mockStatic(DataEditorService.class)) {
            dataEditorServiceMockedStatic.when(() -> DataEditorService.getSortedAllowedSubstructuralElements(any(), any()))
                    .thenReturn(List.of(new SelectItem("MediaPartial", "Media partial"),
                            new SelectItem("Other", "Other")));
            MediaPartialsPanel mediaPartialsPanel = new MediaPartialsPanel(dataEditorForm);
            List<SelectItem> result = mediaPartialsPanel.getMediaPartialChildDivisionsOfSelection();
            assertEquals(1, result.size(), "Only media partial divisions must be returned");
            assertEquals("MediaPartial", result.get(0).getValue());
        }
    }
}
