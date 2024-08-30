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

package org.kitodo.production.security;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.production.helper.Helper;
import org.kitodo.production.services.ServiceManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

/**
 * This class was made to perform an expiration of users session on logout. The session of the user
 * is than not listed anymore as active immediately after logout.
 */
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger logger = LogManager.getLogger(CustomLogoutSuccessHandler.class);
    private final String onSuccessUrl;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public CustomLogoutSuccessHandler(String onSuccessUrl) {
        this.onSuccessUrl = onSuccessUrl;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        if (Objects.nonNull(authentication)) {
            if (Objects.nonNull(authentication.getDetails())) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails) {
                    UserDetails user = (UserDetails) principal;
                    ServiceManager.getSessionService().expireSessionsOfUser(user);
                } else {
                    logger.warn(MessageFormat.format("Cannot expire session: {0} is not an instance of UserDetails",
                        Helper.getObjectDescription(principal)));
                }
            } else {
                logger.warn("Cannot expire session: authentication.getDetails() is null");
            }
        } else {
            logger.warn("Cannot expire session: authentication is null");
        }
        redirectStrategy.sendRedirect(request, response, onSuccessUrl);
    }
}
