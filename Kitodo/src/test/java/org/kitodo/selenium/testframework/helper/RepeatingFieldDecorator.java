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

package org.kitodo.selenium.testframework.helper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

/**
 * Extend the default implementation of a field decorator which handles @FindBy decorated fields.
 * 
 * <p>Use a custom handler to proxy calls to a WebElement, which tries invoking methods multiple 
 * times before giving up.</p>
 */
public class RepeatingFieldDecorator extends DefaultFieldDecorator {

    public RepeatingFieldDecorator(ElementLocatorFactory factory) {
        super(factory);
    }

    @Override
    protected WebElement proxyForLocator(ClassLoader loader, ElementLocator locator) {
        InvocationHandler handler = new RepeatingLocatingElementHandler(locator);

        WebElement proxy;
        proxy =
            (WebElement)
                Proxy.newProxyInstance(
                    loader,
                    new Class[] {WebElement.class, WrapsElement.class, Locatable.class},
                    handler);
        return proxy;
    }

}
