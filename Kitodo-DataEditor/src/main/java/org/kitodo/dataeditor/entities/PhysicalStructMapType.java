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

package org.kitodo.dataeditor.entities;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.kitodo.config.KitodoConfig;
import org.kitodo.config.enums.ParameterDataEditor;
import org.kitodo.dataeditor.MetsKitodoObjectFactory;
import org.kitodo.dataformat.metskitodo.DivType;
import org.kitodo.dataformat.metskitodo.FileType;
import org.kitodo.dataformat.metskitodo.StructMapType;
import org.kitodo.utils.MediaUtil;

public class PhysicalStructMapType extends StructMapType {

    private MetsKitodoObjectFactory objectFactory = new MetsKitodoObjectFactory();

    /**
     * Constructor to copy the data from parent class.
     *
     * @param structMapType
     *            The structMapType object.
     */
    public PhysicalStructMapType(StructMapType structMapType) {
        super.div = structMapType.getDiv();
        super.id = structMapType.getID();
        super.label = structMapType.getLABEL();
        super.type = structMapType.getTYPE();
    }

    /**
     * Reads the FileSec and inserts corresponding divType objects.
     *
     * @param fileSec
     *            The FileSec object.
     */
    public void createDivsByFileSec(FileSec fileSec) {
        DivType rootDiv = objectFactory.createRootDivTypeForPhysicalStructMap();
        rootDiv.getDiv()
            .addAll(getDivTypesByFileTypes(fileSec.getLocalFileGroup().getFile()));
        this.setDiv(rootDiv);
    }

    private List<DivType> getDivTypesByFileTypes(List<FileType> fileTypes) {
        List<DivType> divTypes = new ArrayList<>();
        int counter = 1;
        for (FileType file : fileTypes) {
            DivType div = objectFactory.createDivType();
            div.setID("PHYS_" + String.format("%04d", counter));
            div.setORDER(BigInteger.valueOf(counter));
            div.setORDERLABEL(KitodoConfig.getParameter(ParameterDataEditor.METS_EDITOR_DEFAULT_PAGINATION));
            div.setTYPE(MediaUtil.getPhysicalDivisionTypeOfMimeType(file.getMIMETYPE()));
            DivType.Fptr divTypeFptr = objectFactory.createDivTypeFptr();
            divTypeFptr.setFILEID(file);
            div.getFptr().add(divTypeFptr);

            divTypes.add(div);

            counter++;
        }
        return divTypes;
    }

    private DivType getDivById(String id) {
        for (DivType div : this.getDiv().getDiv()) {
            if (Objects.equals(div.getID(),id)) {
                return div;
            }
        }
        throw new NoSuchElementException("Div with id " + id + " does not exist at physical struct map");
    }

    /**
     * Returns a list of divs with the given ids.
     *
     * @param ids
     *            The list of ids as String objects.
     * @return The list of DivType objects.
     */
    public List<DivType> getDivsByIds(List<String> ids) {
        List<DivType> divs = new ArrayList<>();
        for (String id : ids) {
            divs.add(getDivById(id));
        }
        return divs;
    }
}
