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

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitodo.MockDatabase;
import org.kitodo.data.database.beans.Comment;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.enums.CommentType;
import org.kitodo.production.services.ServiceManager;

public class CommentServiceIT {

    private static final CommentService commentService = ServiceManager.getCommentService();
    private static final ProcessService processService = ServiceManager.getProcessService();

    /**
     * Prepare tests by inserting dummy processes into database.
     * 
     * @throws Exception
     *             when saving of dummy or test processes fails.
     */
    @BeforeClass
    public static void prepareDatabase() throws Exception {
        MockDatabase.startNode();
        MockDatabase.insertProcessesFull();
        MockDatabase.setUpAwaitility();
    }

    /**
     * Clean up database after tests.
     * 
     * @throws Exception
     *             when cleaning up database fails.
     */
    @AfterClass
    public static void cleanDatabase() throws Exception {
        MockDatabase.stopNode();
        MockDatabase.cleanDatabase();
    }

    /**
     * Tests wether comment is correctly saved and removed from database.
     * 
     * @throws Exception
     *             when saving or deleting comment fails.
     */
    @Test
    public void shouldSaveAndRemoveInfoComment() throws Exception {
        Process process = processService.getById(1);
        Comment comment = new Comment();

        comment.setProcess(process);
        comment.setMessage("TEST_MESSAGE");
        comment.setAuthor(ServiceManager.getUserService().getById(1));
        comment.setCreationDate(new Date());
        comment.setType(CommentType.INFO);
        commentService.saveToDatabase(comment);
        Comment newComment = commentService.getAll().get(0);
        assertEquals("Comment was not found in database!", newComment.getMessage(), "TEST_MESSAGE");

        commentService.removeComment(newComment);
        List<Comment> comments = commentService.getAll();
        assertEquals("Comments were found in database!", comments.size(), 0);
    }
}
