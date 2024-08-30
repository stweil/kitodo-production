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

package org.kitodo.production.forms.createprocess;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.kitodo.api.dataeditor.rulesetmanagement.ComplexMetadataViewInterface;
import org.kitodo.api.dataeditor.rulesetmanagement.MetadataViewInterface;
import org.kitodo.api.dataeditor.rulesetmanagement.SimpleMetadataViewInterface;
import org.kitodo.api.dataeditor.rulesetmanagement.StructuralElementViewInterface;
import org.kitodo.api.dataformat.Division;
import org.kitodo.api.dataformat.LogicalDivision;
import org.kitodo.api.dataformat.PhysicalDivision;
import org.kitodo.exceptions.NoSuchMetadataFieldException;

abstract class ProcessSimpleMetadata extends ProcessDetail implements Serializable {

    static final List<Class<? extends Division<?>>> PARENT_CLASSES = Arrays.asList(LogicalDivision.class,
        PhysicalDivision.class);

    /**
     * Container to store the ruleset settings.
     */
    protected SimpleMetadataViewInterface settings;

    /**
     * Constructor, must be called from the subclass.
     *
     * @param settings
     *            the ruleset settings for this field.
     */
    protected ProcessSimpleMetadata(ProcessFieldedMetadata container, SimpleMetadataViewInterface settings,
            String label) {
        super(container, label);
        this.settings = settings;
    }

    /**
     * Returns an independently mutable copy of this.
     *
     * @return an independently mutable copy
     */
    abstract ProcessSimpleMetadata getClone();

    protected BiConsumer<Division<?>, String> getStructureFieldSetters(MetadataViewInterface field)
            throws NoSuchMetadataFieldException {
        String key = field.getId();

        switch (key.toUpperCase()) {
            case "LABEL":
                return Division::setLabel;
            case "ORDERLABEL":
                return Division::setOrderlabel;
            case "CONTENTIDS":
                return (division, value) -> division.getContentIds().add(URI.create(value));
            default:
                throw new NoSuchMetadataFieldException(key, field.getLabel());
        }
    }

    /**
     * Returns if the field may be edited. Some fields may be disallowed to be
     * edit from the rule set.
     *
     * @return whether the field is editable
     */
    public boolean isEditable() {
        return Objects.isNull(settings) || settings.isEditable();
    }

    @Override
    public boolean isUndefined() {
        return Objects.isNull(settings) || settings.isUndefined();
    }

    @Override
    public boolean isRequired() {
        ComplexMetadataViewInterface containerSettings = container.getMetadataView();
        if (!(containerSettings instanceof StructuralElementViewInterface) && container.getChildMetadata().isEmpty()
                && containerSettings.getMinOccurs() == 0) {
            return false;
        }
        return settings.getMinOccurs() > 0;
    }

    @Override
    public int getMinOccurs() {
        return settings.getMinOccurs();
    }
}
