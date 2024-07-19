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

package org.kitodo;

import static org.mockito.Mockito.when;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class BasePrimefaceTest {

    @Mock
    protected FacesContext facesContext;

    @Mock
    protected ExternalContext externalContext;


    @Before
    public void initPrimeface() {
        when(facesContext.getExternalContext()).thenReturn(externalContext);
    }
}
