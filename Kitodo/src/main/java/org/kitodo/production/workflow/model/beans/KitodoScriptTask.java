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

package org.kitodo.production.workflow.model.beans;

import org.camunda.bpm.model.bpmn.instance.ScriptTask;

public class KitodoScriptTask extends KitodoTask {

    private String scriptName;
    private String scriptPath;

    /**
     * Constructor.
     * 
     * @param scriptTask
     *            BPMN model task
     */
    public KitodoScriptTask(ScriptTask scriptTask) {
        super(scriptTask);
        this.scriptName = scriptTask.getAttributeValueNs(NAMESPACE, "scriptName");
        this.scriptPath = scriptTask.getAttributeValueNs(NAMESPACE, "scriptPath");
    }

    /**
     * Get scriptName.
     *
     * @return value of scriptName
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * Set scriptName.
     *
     * @param scriptName
     *            as String
     */
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    /**
     * Get scriptPath.
     *
     * @return value of scriptPath
     */
    public String getScriptPath() {
        return scriptPath;
    }

    /**
     * Set scriptPath.
     *
     * @param scriptPath
     *            as java.lang.String
     */
    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }
}
