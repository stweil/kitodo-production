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
import org.kitodo.data.database.beans.Batch;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.elasticsearch.index.type.enums.BatchTypeField;
import org.kitodo.data.elasticsearch.index.type.enums.ProcessTypeField;

/**
 * Test class for BatchType.
 */
public class BatchTypeTest {

    private static List<Batch> prepareData() {

        List<Batch> batches = new ArrayList<>();
        List<Process> processes = new ArrayList<>();

        Process firstProcess = new Process();
        firstProcess.setId(1);
        firstProcess.setTitle("First");
        processes.add(firstProcess);

        Process secondProcess = new Process();
        secondProcess.setId(2);
        secondProcess.setTitle("Second");
        processes.add(secondProcess);

        Batch firstBatch = new Batch();
        firstBatch.setId(1);
        firstBatch.setTitle("Batch1");
        firstBatch.setProcesses(processes);
        batches.add(firstBatch);

        Batch secondBatch = new Batch();
        secondBatch.setId(2);
        secondBatch.setTitle("Batch2");
        batches.add(secondBatch);

        Batch thirdBatch = new Batch();
        thirdBatch.setId(3);
        thirdBatch.setTitle("Batch3");
        batches.add(thirdBatch);

        return batches;
    }

    @Test
    public void shouldCreateFirstDocument() throws Exception {
        BatchType batchType = new BatchType();

        Batch batch = prepareData().get(0);
        Map<String, Object> actual = batchType.createDocument(batch);

        assertEquals("Batch1", BatchTypeField.TITLE.getStringValue(actual), "Key title doesn't match to given value!");

        List<Map<String, Object>> processes = BatchTypeField.PROCESSES.getJsonArray(actual);
        assertEquals(2, processes.size(), "Size processes doesn't match to given value!");

        Map<String, Object> process = processes.get(0);
        assertEquals(1, ProcessTypeField.ID.getIntValue(process), "Key processes.id doesn't match to given value!");
        assertEquals("First", ProcessTypeField.TITLE.getStringValue(process), "Key processes.title doesn't match to given value!");

        process = processes.get(1);
        assertEquals(2, ProcessTypeField.ID.getIntValue(process), "Key processes.id doesn't match to given value!");
        assertEquals("Second", ProcessTypeField.TITLE.getStringValue(process), "Key processes.title doesn't match to given value!");
    }

    @Test
    public void shouldCreateSecondDocument() throws Exception {
        BatchType batchType = new BatchType();

        Batch batch = prepareData().get(1);
        Map<String, Object> actual = batchType.createDocument(batch);

        assertEquals("Batch2", BatchTypeField.TITLE.getStringValue(actual), "Key title doesn't match to given value!");

        List<Map<String, Object>> processes = BatchTypeField.PROCESSES.getJsonArray(actual);
        assertEquals(0, processes.size(), "Size processes doesn't match to given value!");
    }

    @Test
    public void shouldCreateDocumentWithCorrectAmountOfKeys() throws Exception {
        BatchType batchType = new BatchType();

        Batch batch = prepareData().get(0);
        Map<String, Object> actual = batchType.createDocument(batch);

        assertEquals(2, actual.keySet().size(), "Amount of keys is incorrect!");

        List<Map<String, Object>> processes = BatchTypeField.PROCESSES.getJsonArray(actual);
        Map<String, Object> process = processes.get(0);
        assertEquals(2, process.keySet().size(), "Amount of keys in processes is incorrect!");
    }

    @Test
    public void shouldCreateDocuments() {
        BatchType batchType = new BatchType();

        List<Batch> batches = prepareData();
        Map<Integer, Map<String, Object>> documents = batchType.createDocuments(batches);
        assertEquals(3, documents.size(), "HashMap of documents doesn't contain given amount of elements!");
    }
}
