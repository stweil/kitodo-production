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

package org.kitodo.production.forms.massimport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kitodo.api.Metadata;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.api.dataeditor.rulesetmanagement.StructuralElementViewInterface;
import org.kitodo.production.forms.CsvCell;
import org.kitodo.production.forms.createprocess.ProcessDetail;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.data.ImportService;

public class AddMetadataDialog {

    private final MassImportForm massImportForm;
    private ProcessDetail metadataDetail;
    private List<ProcessDetail> metadataTypes;
    private RulesetManagementInterface rulesetManagement;
    private final List<Locale.LanguageRange> priorityList = ServiceManager.getUserService()
            .getCurrentMetadataLanguage();
    private final String acquisitionStage = "create";
    private List<StructuralElementViewInterface> allRulesetDivisions = new ArrayList<>();

    AddMetadataDialog(MassImportForm massImportForm) {
        this.massImportForm = massImportForm;
    }

    /**
     * Gets metadataDetail.
     *
     * @return value of metadataDetail
     */
    public ProcessDetail getMetadataDetail() {
        return metadataDetail;
    }

    /**
     * Sets metadataDetail.
     *
     * @param metadataDetail value of metadataDetail
     */
    public void setMetadataDetail(ProcessDetail metadataDetail) {
        this.metadataDetail = metadataDetail;
    }

    /**
     * Sets rulesetManagement.
     *
     * @param rulesetManagement value of rulesetManagement
     */
    public void setRulesetManagement(RulesetManagementInterface rulesetManagement) {
        this.rulesetManagement = rulesetManagement;
    }

    /**
     * Returns the list of selectable metadata types.
     *
     * @return the map of metadata types
     */
    public List<ProcessDetail> getAllMetadataTypes() {
        if (Objects.isNull(metadataTypes)) {
            prepareMetadataTypes();
        }
        return metadataTypes;
    }

    /**
     * prepare the list of metadata types.
     */
    public void prepareMetadataTypes() {
        if (allRulesetDivisions.isEmpty()) {
            allRulesetDivisions = getDivisions();
        }
        metadataTypes = ServiceManager.getMassImportService().getAddableMetadataTable(allRulesetDivisions, getPresetMetadata());
        if (!metadataTypes.isEmpty()) {
            metadataDetail = metadataTypes.get(0);
        }

    }

    private List<StructuralElementViewInterface> getDivisions() {
        return rulesetManagement.getStructuralElements(priorityList).keySet().stream()
                .map(key -> rulesetManagement.getStructuralElementView(key, acquisitionStage, priorityList))
                .collect(Collectors.toList());
    }

    private Collection<Metadata> getPresetMetadata() {
        Collection<Metadata> presetMetadata = new ArrayList<>();
        for (int i = 1; i < massImportForm.getMetadataKeys().size(); i++) {
            Metadata metadata = new Metadata();
            metadata.setKey(massImportForm.getMetadataKeys().get(i));
            presetMetadata.add(metadata);
        }
        return presetMetadata;
    }

    /**
     * Add metadata column.
     */
    public void addMetadata() {
        massImportForm.getMetadataKeys().add(metadataDetail.getMetadataID());
        massImportForm.getRecords()
                .forEach(csvRecord -> csvRecord.getCsvCells().add(new CsvCell(ImportService.getProcessDetailValue(metadataDetail))));
    }
}
