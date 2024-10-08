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

package org.kitodo.production.services.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.kitodo.api.dataeditor.rulesetmanagement.FunctionalDivision;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.config.ConfigCore;
import org.kitodo.config.enums.ParameterCore;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.database.persistence.RulesetDAO;
import org.kitodo.data.elasticsearch.index.Indexer;
import org.kitodo.data.elasticsearch.index.type.RulesetType;
import org.kitodo.data.elasticsearch.index.type.enums.DocketTypeField;
import org.kitodo.data.elasticsearch.index.type.enums.RulesetTypeField;
import org.kitodo.data.elasticsearch.search.Searcher;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.exceptions.RulesetNotFoundException;
import org.kitodo.production.dto.ClientDTO;
import org.kitodo.production.dto.RulesetDTO;
import org.kitodo.production.helper.Helper;
import org.kitodo.production.helper.metadata.legacytypeimplementations.LegacyPrefsHelper;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.data.base.ClientSearchService;
import org.primefaces.model.SortOrder;

public class RulesetService extends ClientSearchService<Ruleset, RulesetDTO, RulesetDAO> {

    private static final Logger logger = LogManager.getLogger(RulesetService.class);
    private static volatile RulesetService instance = null;

    /**
     * Constructor with Searcher and Indexer assigning.
     */
    private RulesetService() {
        super(new RulesetDAO(), new RulesetType(), new Indexer<>(Ruleset.class), new Searcher(Ruleset.class),
                RulesetTypeField.CLIENT_ID.getKey());
    }

    /**
     * Return singleton variable of type RulesetService.
     *
     * @return unique instance of RulesetService
     */
    public static RulesetService getInstance() {
        RulesetService localReference = instance;
        if (Objects.isNull(localReference)) {
            synchronized (RulesetService.class) {
                localReference = instance;
                if (Objects.isNull(localReference)) {
                    localReference = new RulesetService();
                    instance = localReference;
                }
            }
        }
        return localReference;
    }

    @Override
    public Long countDatabaseRows() throws DAOException {
        return countDatabaseRows("SELECT COUNT(*) FROM Ruleset");
    }

    @Override
    public Long countNotIndexedDatabaseRows() throws DAOException {
        return countDatabaseRows("SELECT COUNT(*) FROM Ruleset WHERE indexAction = 'INDEX' OR indexAction IS NULL");
    }

    @Override
    public Long countResults(Map filters) throws DataException {
        return countDocuments(getRulesetsForCurrentUserQuery());
    }

    @Override
    public List<RulesetDTO> loadData(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters)
            throws DataException {
        return findByQuery(getRulesetsForCurrentUserQuery(), getSortBuilder(sortField, sortOrder), first, pageSize,
            false);
    }

    @Override
    public List<Ruleset> getAllNotIndexed() {
        return getByQuery("FROM Ruleset WHERE indexAction = 'INDEX' OR indexAction IS NULL");
    }

    @Override
    public List<Ruleset> getAllForSelectedClient() {
        return dao.getByQuery("SELECT r FROM Ruleset AS r INNER JOIN r.client AS c WITH c.id = :clientId",
            Collections.singletonMap("clientId", ServiceManager.getUserService().getSessionClientId()));
    }

    @Override
    public RulesetDTO convertJSONObjectToDTO(Map<String, Object> jsonObject, boolean related) throws DataException {
        RulesetDTO rulesetDTO = new RulesetDTO();
        rulesetDTO.setId(getIdFromJSONObject(jsonObject));
        rulesetDTO.setTitle(RulesetTypeField.TITLE.getStringValue(jsonObject));
        rulesetDTO.setFile(RulesetTypeField.FILE.getStringValue(jsonObject));
        rulesetDTO.setOrderMetadataByRuleset(
            RulesetTypeField.ORDER_METADATA_BY_RULESET.getBooleanValue(jsonObject));

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(RulesetTypeField.CLIENT_ID.getIntValue(jsonObject));
        clientDTO.setName(RulesetTypeField.CLIENT_NAME.getStringValue(jsonObject));

        rulesetDTO.setClientDTO(clientDTO);
        return rulesetDTO;
    }

    /**
     * Get list of rulesets for given title.
     *
     * @param title
     *            for get from database
     * @return list of rulesets
     */
    public List<Ruleset> getByTitle(String title) {
        return dao.getByQuery("FROM Ruleset WHERE title = :title", Collections.singletonMap("title", title));
    }

    /**
     * Find ruleset with exact file.
     *
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public Map<String, Object> findByFile(String file) throws DataException {
        QueryBuilder queryBuilder = createSimpleQuery(RulesetTypeField.FILE.getKey(), file, true);
        return findDocument(queryBuilder);
    }

    /**
     * Find rulesets for client id.
     *
     * @param clientId
     *            of the searched rulesets
     * @return search result
     */
    List<Map<String, Object>> findByClientId(Integer clientId) throws DataException {
        QueryBuilder query = createSimpleQuery(DocketTypeField.CLIENT_ID.getKey(), clientId, true);
        return findDocuments(query);
    }

    /**
     * Find ruleset with exact title and file name.
     *
     * @param title
     *            of the searched ruleset
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public Map<String, Object> findByTitleAndFile(String title, String file) throws DataException {
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(createSimpleQuery(RulesetTypeField.TITLE.getKey(), title, true, Operator.AND));
        query.must(createSimpleQuery(RulesetTypeField.FILE.getKey(), file, true, Operator.AND));
        return findDocument(query);
    }

    /**
     * Find ruleset with exact title or file name.
     *
     * @param title
     *            of the searched ruleset
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public List<Map<String, Object>> findByTitleOrFile(String title, String file) throws DataException {
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.should(createSimpleQuery(RulesetTypeField.TITLE.getKey(), title, true));
        query.should(createSimpleQuery(RulesetTypeField.FILE.getKey(), file, true));
        return findDocuments(query);
    }

    /**
     * Get preferences.
     *
     * @param ruleset
     *            object
     * @return preferences
     */
    public LegacyPrefsHelper getPreferences(Ruleset ruleset) {
        LegacyPrefsHelper myPreferences = new LegacyPrefsHelper();
        try {
            myPreferences.loadPrefs(ConfigCore.getParameter(ParameterCore.DIR_RULESETS) + ruleset.getFile());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return myPreferences;
    }

    private QueryBuilder getRulesetsForCurrentUserQuery() {
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(createSimpleQuery(RulesetTypeField.CLIENT_ID.getKey(),
                ServiceManager.getUserService().getSessionClientId(), true));
        return query;
    }

    /**
     * Acquires a ruleset Management and loads a ruleset into it.
     *
     * @param ruleset
     *            database object that references the ruleset
     * @return a Ruleset Management in which the ruleset has been loaded
     */
    public RulesetManagementInterface openRuleset(Ruleset ruleset) throws IOException {
        final long begin = System.nanoTime();
        RulesetManagementInterface rulesetManagement = ServiceManager.getRulesetManagementService()
                .getRulesetManagement();
        String fileName = ruleset.getFile();
        try {
            rulesetManagement.load(Paths.get(ConfigCore.getParameter(ParameterCore.DIR_RULESETS), fileName).toFile());
        } catch (FileNotFoundException | IllegalArgumentException e) {
            throw new RulesetNotFoundException(fileName);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Reading ruleset took {} ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
        }
        return rulesetManagement;
    }

    /**
     * Returns the names of those divisions that fulfill a given function.
     * 
     * @param rulesetId
     *            ruleset database number
     * @param function
     *            function that the divisions are supposed to fulfill
     * @return collection of identifiers for divisions that fulfill this
     *         function
     */
    public Collection<String> getFunctionalDivisions(Integer rulesetId, FunctionalDivision function) {
        try {
            Ruleset ruleset = ServiceManager.getRulesetService().getById(rulesetId);
            RulesetManagementInterface rulesetManagement;
            rulesetManagement = ServiceManager.getRulesetService().openRuleset(ruleset);
            return rulesetManagement.getFunctionalDivisions(function);
        } catch (DAOException | IOException e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
            return Collections.emptySet();
        }
    }
}
