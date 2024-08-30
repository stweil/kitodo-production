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

package org.kitodo.data.elasticsearch.index.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kitodo.data.database.beans.Filter;
import org.kitodo.data.database.beans.User;
import org.kitodo.data.elasticsearch.index.type.enums.FilterTypeField;

/**
 * Test class for FilterType.
 */
public class FilterTypeTest {

    private static List<Filter> prepareData() {
        List<Filter> filters = new ArrayList<>();

        User firstUser = new User();
        firstUser.setId(1);

        Filter firstFilter = new Filter();
        firstFilter.setId(1);
        firstFilter.setValue("\"id:1\"");
        firstFilter.setUser(firstUser);

        Filter secondFilter = new Filter();
        firstFilter.setId(2);
        secondFilter.setValue("\"id:2\"");
        secondFilter.setUser(firstUser);

        filters.add(firstFilter);
        filters.add(secondFilter);

        return filters;
    }

    @Test
    public void shouldCreateFirstDocument() throws Exception {
        FilterType filterType = new FilterType();

        Filter filter = prepareData().get(0);
        Map<String, Object> actual = filterType.createDocument(filter);

        assertEquals("\"id:1\"", FilterTypeField.VALUE.getStringValue(actual), "Key value doesn't match to given value!");
        assertEquals(1, FilterTypeField.USER.getIntValue(actual), "Key user doesn't match to given value!");
    }

    @Test
    public void shouldCreateSecondDocument() throws Exception {
        FilterType filterType = new FilterType();

        Filter filter = prepareData().get(1);
        Map<String, Object> actual = filterType.createDocument(filter);

        assertEquals("\"id:2\"", FilterTypeField.VALUE.getStringValue(actual), "Key value doesn't match to given value!");
        assertEquals(1, FilterTypeField.USER.getIntValue(actual), "Key user doesn't match to given value!");
    }

    @Test
    public void shouldCreateDocumentWithCorrectAmountOfKeys() throws Exception {
        FilterType filterType = new FilterType();

        Filter filter = prepareData().get(0);
        Map<String, Object> actual = filterType.createDocument(filter);

        assertEquals(2, actual.keySet().size(), "Amount of keys is incorrect!");
    }

    @Test
    public void shouldCreateDocuments() {
        FilterType filterType = new FilterType();

        List<Filter> filters = prepareData();
        Map<Integer, Map<String, Object>> documents = filterType.createDocuments(filters);
        assertEquals(2, documents.size(), "HashMap of documents doesn't contain given amount of elements!");
    }
}
