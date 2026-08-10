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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.api.dataeditor.rulesetmanagement.StructuralElementViewInterface;
import org.kitodo.api.dataformat.LogicalDivision;
import org.kitodo.api.dataformat.PhysicalDivision;
import org.kitodo.api.dataformat.View;
import org.kitodo.production.enums.MediaContentType;
import org.kitodo.production.helper.Helper;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class GalleryPanelTest {

    private DataEditorForm dataEditorForm;
    private GalleryPanel galleryPanel;

    @BeforeEach
    public void initTest() {
        dataEditorForm = mock(DataEditorForm.class);
        galleryPanel = new GalleryPanel(dataEditorForm);
    }

    @Test
    public void testIsSelectedWithMediaNotAssignedToStructure() throws Exception {
        GalleryMediaContent galleryMediaContent = createGalleryMediaContent();
        setStripes(new ArrayList<>());
        assertFalse(galleryPanel.isSelected(galleryMediaContent, null),
                "Unassigned media must not be reported as selected");
    }

    @Test
    public void testIsSelectedDelegatesToDataEditorWhenMediaIsAssignedToStructure() throws Exception {
        GalleryMediaContent galleryMediaContent = createGalleryMediaContent();
        RulesetManagementInterface ruleset = mock(RulesetManagementInterface.class);
        StructuralElementViewInterface divisionView = mock(StructuralElementViewInterface.class);
        when(dataEditorForm.getRulesetManagement()).thenReturn(ruleset);
        when(divisionView.getLabel()).thenReturn("Monograph");
        when(ruleset.getStructuralElementView(any(), any(), any())).thenReturn(divisionView);
        when(dataEditorForm.isSelected(any(), any())).thenReturn(true);

        GalleryStripe stripe = new GalleryStripe(galleryPanel, new LogicalDivision(), "0");
        stripe.getMedias().add(galleryMediaContent);
        List<GalleryStripe> stripes = new ArrayList<>();
        stripes.add(stripe);
        setStripes(stripes);

        assertTrue(galleryPanel.isSelected(galleryMediaContent, null),
                "Media assigned to a structure must be reported as selected");
    }

    @Test
    public void testSelectWithNullCurrentSelection() throws Exception {
        try (MockedStatic<Helper> helperMockedStatic = Mockito.mockStatic(Helper.class)) {
            Method select = GalleryPanel.class.getDeclaredMethod("select", GalleryMediaContent.class,
                    GalleryStripe.class, String.class);
            select.setAccessible(true);
            assertDoesNotThrow(() -> select.invoke(galleryPanel, null, null, "single"));
            helperMockedStatic.verify(() -> Helper.setErrorMessage("Passed GalleryMediaContent must not be null."));
        }
    }

    private GalleryMediaContent createGalleryMediaContent() {
        View view = new View();
        view.setPhysicalDivision(new PhysicalDivision());
        return new GalleryMediaContent(MediaContentType.IMAGE, view, "1", "image/jpeg", null, "image/jpeg", null, "0");
    }

    private void setStripes(List<GalleryStripe> stripes) throws Exception {
        Field stripesField = GalleryPanel.class.getDeclaredField("stripes");
        stripesField.setAccessible(true);
        stripesField.set(galleryPanel, stripes);
    }
}
