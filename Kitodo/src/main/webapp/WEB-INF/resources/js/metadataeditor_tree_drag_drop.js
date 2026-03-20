/**
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/* global PrimeFaces */

/**
 * Improves the PrimeFaces tree component by sorting the tree node 
 * selection with respect to the tree order such that dragging multiple 
 * nodes are not inserted in their selection order but in tree order.
 */
function registerPrimeFacesTreeSelectionOrderSorting() {

    // overwrite Primefaces method to sort selection array by rowKey
    let backupFunc = PrimeFaces.widget.BaseTree.prototype.addToSelection;
    PrimeFaces.widget.BaseTree.prototype.addToSelection = function(rowKey) {
        backupFunc.apply(this, [rowKey]);
        
        this.selections.sort(function (rk1, rk2) {
            let rk1a = rk1.split("_").map(function(s) { return parseInt(s); });
            let rk2a = rk2.split("_").map(function(s) { return parseInt(s); });
            let idx = 0;
            let length = Math.max(rk1a.length, rk2a.length);

            while (idx < length) {
                let delta = (rk1a[idx] || 0) - (rk2a[idx] || 0);
                if (delta > 0 || delta < 0) { 
                    return delta;
                }
                idx += 1;
            }
        });
    };
}

$(function() {
    registerPrimeFacesTreeSelectionOrderSorting();
});
