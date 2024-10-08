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

package org.kitodo.data.elasticsearch.search;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Objects;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.metrics.ValueCount;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kitodo.config.ConfigMain;
import org.kitodo.data.elasticsearch.MockEntity;
import org.kitodo.data.elasticsearch.index.IndexRestClient;

/**
 * Test class for Searcher.
 */
public class SearcherIT {

    private static Node node;
    private static IndexRestClient indexRestClient;
    private static String testIndexName;
    private static final String testSearch = "testsearch";
    private static final QueryBuilder query = QueryBuilders.matchAllQuery();
    private static final Searcher searcher = new Searcher(testSearch);
    private static final String TITLE = "title";
    private static final String BATCH_ONE = "Batch1";
    private static final String WRONG_AMOUNT = "Incorrect result - amount doesn't match to given number!";
    private static final String WRONG_ID = "Incorrect result - id doesn't match to given int values!";
    private static final String WRONG_SIZE = "Incorrect result - size doesn't match to given int value!";
    private static final String WRONG_TITLE = "Incorrect result - title doesn't match to given plain text!";

    @BeforeAll
    public static void prepareIndex() throws Exception {
        MockEntity.setUpAwaitility();

        testIndexName = ConfigMain.getParameter("elasticsearch.index", "testindex");
        indexRestClient = initializeIndexRestClient();

        node = MockEntity.prepareNode();
        node.start();

        indexRestClient.createIndex(null, testSearch);
        indexRestClient.addDocument(searcher.getType(), MockEntity.createEntities().get(1), 1, false);
        indexRestClient.addDocument(searcher.getType(), MockEntity.createEntities().get(2), 2, false);
        indexRestClient.addDocument(searcher.getType(), MockEntity.createEntities().get(3), 3, false);
        indexRestClient.addDocument(searcher.getType(), MockEntity.createEntities().get(4), 4, false);
        indexRestClient.enableSortingByTextField(TITLE, testSearch);
    }

    @AfterAll
    public static void cleanIndex() throws Exception {
        indexRestClient.deleteIndex(testSearch);
        node.close();
    }

    @Test
    public void shouldCountDocuments() {
        await().untilAsserted(() ->
            assertEquals(4, searcher.countDocuments(query).longValue(), "Amount of documents doesn't match to given number!"));
    }

    @Test
    public void shouldAggregateDocumentsAccordingToQueryAmount() {
        AggregationBuilder aggregation = AggregationBuilders.sum("amount").field("amount");

        await().untilAsserted(() ->
            assertEquals(8.0, ((Sum) searcher.aggregateDocuments(query, aggregation).get("amount")).getValue(), 0.0, WRONG_AMOUNT));

        /*QueryBuilder matchQuery = QueryBuilders.matchQuery("type", "");
        await().untilAsserted(() -> assertTrue(WRONG_AMOUNT,
            ((Sum) searcher.aggregateDocuments(matchQuery, aggregation).get("amount")).getValue() == 6.0));*/
    }

    @Test
    public void shouldAggregateDocumentsAccordingToQueryCount() {
        AggregationBuilder aggregation = AggregationBuilders.count("count").field("amount");
        await().untilAsserted(() ->
            assertEquals(4, ((ValueCount) searcher.aggregateDocuments(query, aggregation).get("count")).getValue(), "Incorrect result - count doesn't match to given number!"));

        /*QueryBuilder matchQuery = QueryBuilders.matchQuery("type", "");
        await().untilAsserted(() -> assertEquals("Incorrect result - count doesn't match to given number!", 3,
            ((ValueCount) searcher.aggregateDocuments(matchQuery, aggregation).get("count")).getValue()));*/
    }

    @Test
    public void shouldFindDocumentById() {
        await().untilAsserted(() ->
            assertEquals(1, getIdFromJSONObject(searcher.findDocument(1)).longValue(), "Incorrect result - id doesn't match to given plain text!"));

        await().untilAsserted(() ->
            assertEquals(BATCH_ONE, searcher.findDocument(1).get(TITLE), WRONG_TITLE));
    }

    @Test
    public void shouldFindDocumentByQuery() {
        await().untilAsserted(() ->
            assertEquals(1, getIdFromJSONObject(searcher.findDocument(query)).intValue(), "Incorrect result - id doesn't match to given number!"));

        await().untilAsserted(() ->
            assertEquals(BATCH_ONE, searcher.findDocument(query).get(TITLE), WRONG_TITLE));

        QueryBuilder queryMatch = QueryBuilders.matchQuery(TITLE, BATCH_ONE);
        await().untilAsserted(() ->
            assertEquals(1, getIdFromJSONObject(searcher.findDocument(queryMatch)).intValue(), "Incorrect result - id doesn't match to given plain text!"));

        await().untilAsserted(() ->
            assertEquals(BATCH_ONE, searcher.findDocument(queryMatch).get(TITLE), WRONG_TITLE));

        QueryBuilder queryNonexistent = QueryBuilders.matchQuery(TITLE, "Nonexistent");
        await().untilAsserted(() ->
            assertEquals(Integer.valueOf(0), getIdFromJSONObject(searcher.findDocument(queryNonexistent)), "Incorrect result - id has another value than null!"));
    }

    @Test
    public void shouldFindDocumentByQueryAndSort() {
        SortBuilder sort = new FieldSortBuilder(TITLE).order(SortOrder.DESC);
        await().untilAsserted(() ->
            assertEquals(2, getIdFromJSONObject(searcher.findDocument(query, sort)).intValue(), "Incorrect result - id doesn't match to given number!"));

        await().untilAsserted(() ->
            assertEquals("Sort", searcher.findDocument(query, sort).get(TITLE), WRONG_TITLE));
    }

    @Test
    public void shouldFindDocumentsByQuery() {
        await().ignoreExceptions()
                .untilAsserted(() -> assertEquals(1, getIdFromJSONObject(searcher.findDocuments(query).get(0)).intValue(), WRONG_ID));

        await().ignoreExceptions()
                .untilAsserted(() -> assertEquals(4, searcher.findDocuments(query).size(), WRONG_SIZE));

        QueryBuilder queryMatch = QueryBuilders.matchQuery(TITLE, BATCH_ONE);
        await().ignoreExceptions()
                .untilAsserted(() -> assertEquals(1, getIdFromJSONObject(searcher.findDocuments(queryMatch).get(0)).intValue(), WRONG_ID));

        await().ignoreExceptions()
                .untilAsserted(() -> assertEquals(1, searcher.findDocuments(queryMatch).size(), WRONG_SIZE));

        QueryBuilder queryNonexistent = QueryBuilders.matchQuery(TITLE, "Nonexistent");
        await().ignoreExceptions()
                .untilAsserted(() -> assertEquals(0, searcher.findDocuments(queryNonexistent).size(), "Incorrect result - size is bigger than 0!"));
    }

    @Test
    public void shouldFindDocumentsByQueryAndSort() {
        SortBuilder sort = new FieldSortBuilder(TITLE).order(SortOrder.DESC);
        await().ignoreExceptions()
                .untilAsserted(() -> assertEquals(2, getIdFromJSONObject(searcher.findDocuments(query, sort).get(0)).intValue(), WRONG_ID));
    }

    @Test
    public void shouldFindDocumentsByQueryAndPagination() {
        await().ignoreExceptions()
                .untilAsserted(() -> assertEquals(2, getIdFromJSONObject(searcher.findDocuments(query, 1, 2).get(0)).intValue(), WRONG_ID));
    }

    @Test
    public void shouldFindDocumentsByQuerySortAndPagination() {
        SortBuilder sort = new FieldSortBuilder(TITLE).order(SortOrder.DESC);

        await().untilAsserted(() -> assertEquals(4, getIdFromJSONObject(searcher.findDocuments(query, sort, 1, 2).get(0)).intValue(), WRONG_ID));

        await().untilAsserted(() -> assertEquals(3, getIdFromJSONObject(searcher.findDocuments(query, sort, 1, 2).get(1)).intValue(), WRONG_ID));
    }

    private static IndexRestClient initializeIndexRestClient() {
        IndexRestClient restClient = IndexRestClient.getInstance();
        restClient.setIndexBase(testIndexName);
        return restClient;
    }

    /**
     * Get id from JSON object returned form ElasticSearch.
     *
     * @param jsonObject
     *            returned form ElasticSearch
     * @return id as Integer
     */
    private static Integer getIdFromJSONObject(Map<String, Object> jsonObject) {
        if (jsonObject.containsKey("id") && Objects.nonNull(jsonObject.get("id"))) {
            return Integer.valueOf((String) jsonObject.get("id"));
        }
        return 0;
    }
}
