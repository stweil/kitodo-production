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

package org.kitodo.production.services.dataformat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.api.MetadataEntry;
import org.kitodo.api.dataformat.LogicalDivision;
import org.kitodo.api.dataformat.Workpiece;
import org.kitodo.api.dataformat.mets.MetsXmlElementAccessInterface;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.serviceloader.KitodoServiceLoader;
import org.w3c.dom.Document;

public class MetsService {
    private static final Logger logger = LogManager.getLogger(MetsService.class);

    private static volatile MetsService instance = null;
    private final MetsXmlElementAccessInterface metsXmlElementAccess;

    /**
     * Return singleton variable of type MetsService.
     *
     * @return unique instance of MetsService
     */
    public static MetsService getInstance() {
        MetsService localReference = instance;
        if (Objects.isNull(localReference)) {
            synchronized (MetsService.class) {
                localReference = instance;
                if (Objects.isNull(localReference)) {
                    localReference = new MetsService();
                    instance = localReference;
                }
            }
        }
        return localReference;
    }

    private MetsService() {
        metsXmlElementAccess = (MetsXmlElementAccessInterface) new KitodoServiceLoader<>(
                MetsXmlElementAccessInterface.class).loadModule();
    }

    /**
     * Returns the type of the top element of the logical structure, and thus the
     * type of the workpiece.
     *
     * @param uri
     *            Address of the METS file of the workpiece
     * @return the type of root element of the logical structure of the workpiece
     * @throws IOException
     *             if the file cannot be read (for example, because the file was
     *             not found)
     */
    public String getBaseType(URI uri) throws IOException {
        return getBaseType(loadWorkpiece(uri));

    }

    /**
     * Returns the type of the top element of the logical structure, and thus the
     * type of the workpiece.
     *
     * @param workpiece
     *            the workpiece
     * @return the type of root element of the logical structure of the workpiece
     */
    public String getBaseType(Workpiece workpiece) {
        LogicalDivision logicalDivision = workpiece.getLogicalStructure();
        String type = logicalDivision.getType();
        while (Objects.isNull(type) && !logicalDivision.getChildren().isEmpty()) {
            logicalDivision = logicalDivision.getChildren().get(0);
            type = logicalDivision.getType();
        }
        return type;
    }

    /**
     * Function for loading METS files from URI.
     *
     * @param uri
     *            address of the file to be loaded
     * @return loaded file
     * @throws IOException
     *             if reading is not working (disk broken, ...)
     */
    public Workpiece loadWorkpiece(URI uri) throws IOException {
        try (InputStream inputStream = ServiceManager.getFileService().read(uri)) {
            logger.info("Reading {}", uri.toString());
            return metsXmlElementAccess.read(inputStream);
        }
    }

    /**
     * Create and return Workpiece from given Document 'document'.
     *
     * @param document Document from which a Workpiece is created
     * @return Workpiece created from given Document
     * @throws TransformerException
     *          thrown when XML transformation fails
     * @throws IOException
     *          thrown when unable to read inputStream
     */
    public Workpiece loadWorkpiece(Document document) throws TransformerException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(document);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return metsXmlElementAccess.read(inputStream);
    }

    /**
     * Function for writing METS files to URI. (URI target must allow writing
     * operation.)
     *
     * @param workpiece
     *            data to be written
     * @param uri
     *            address where should be written
     * @throws IOException
     *             if writing does not work (partition full, or is generally not
     *             supported, ...)
     */
    public void saveWorkpiece(Workpiece workpiece, URI uri) throws IOException {
        try (OutputStream outputStream = ServiceManager.getFileService().write(uri)) {
            logger.info("Saving {}", uri.toString());
            save(workpiece, outputStream);
        }
    }

    public void save(Workpiece workpiece, OutputStream outputStream) throws IOException {
        metsXmlElementAccess.save(workpiece, outputStream);
    }

    /**
     * Counts the logical metadata tags in the workpiece.
     * @param workpiece the workpiece to count tags.
     * @return the number of tags
     */
    public static int countLogicalMetadata(Workpiece workpiece) {
        return Math.toIntExact(Workpiece.treeStream(workpiece.getLogicalStructure())
                .flatMap(logicalDivision -> logicalDivision.getMetadata().parallelStream())
                .filter(metadata -> !(metadata instanceof MetadataEntry)
                        || Objects.nonNull(((MetadataEntry) metadata).getValue())
                                && !((MetadataEntry) metadata).getValue().isEmpty())
                .mapToInt(metadata -> 1).count());
    }
}
