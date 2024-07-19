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

package org.kitodo.production.beans;

public class Range {
    private final long start;
    private final long end;
    private final long length;
    private final long total;

    /**
     * Construct a byte range.
     *
     * @param start
     *         Start of the byte range.
     * @param end
     *         End of the byte range.
     * @param total
     *         Total length of the byte source.
     */
    public Range(long start, long end, long total) {
        this.start = start;
        this.end = end;
        this.length = end - start + 1;
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public long getLength() {
        return length;
    }

    public long getEnd() {
        return end;
    }

    public long getStart() {
        return start;
    }
}
