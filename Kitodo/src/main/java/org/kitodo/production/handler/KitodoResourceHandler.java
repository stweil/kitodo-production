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

package org.kitodo.production.handler;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import org.primefaces.application.resource.PrimeResourceHandler;

public class KitodoResourceHandler extends PrimeResourceHandler {

    private static final String KITODO_RANGE_STREAMED_CONTENT_KEY = "krsc";

    public KitodoResourceHandler(ResourceHandler wrapped) {
        super(wrapped);
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        // Check if parameter is available to stream with RangeStreamContentHandler
        if (params.containsKey(KITODO_RANGE_STREAMED_CONTENT_KEY) && params.get(KITODO_RANGE_STREAMED_CONTENT_KEY)
                .equals("true")) {
            new RangeStreamContentHandler().handle(context);
            return;
        }
        super.handleResourceRequest(context);
    }

}
