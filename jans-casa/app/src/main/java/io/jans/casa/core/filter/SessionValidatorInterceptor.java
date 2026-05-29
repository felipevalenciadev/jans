package io.jans.casa.core.filter;

import io.jans.casa.core.PersistenceService;
import io.jans.casa.core.SessionContext;
import io.jans.casa.core.pojo.User;
import io.jans.casa.misc.Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.RequestInterceptor;

public class SessionValidatorInterceptor implements RequestInterceptor {

    private static final String OAUTH_SESSION_COOKIE = "session_id";

    private Logger logger = LoggerFactory.getLogger(getClass());
    private PersistenceService persistenceService;

    public SessionValidatorInterceptor() {
        logger.info("SessionValidatorInterceptor initialized");
        persistenceService = Utils.managedBean(PersistenceService.class);
    }

    @Override
    public void request(Session session, Object request, Object response) {
        try {
            SessionContext sessionContext = Utils.managedBean(SessionContext.class);
            if (sessionContext == null) return;

            User user = sessionContext.getUser();
            if (user == null) return;

            HttpServletRequest httpRequest   = (HttpServletRequest)  request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String oauthSessionId = getCookieValue(httpRequest, OAUTH_SESSION_COOKIE);

            if (oauthSessionId == null || oauthSessionId.isEmpty()) {
                logger.warn("[Security] No OAuth session cookie for user '{}'. Invalidating.",
                        user.getUserName());
                invalidateAndRedirect(session, httpRequest, httpResponse);
                return;
            }

            if (!persistenceService.oauthSessionExists(oauthSessionId)) {
                logger.warn("[Security] OAuth session '{}' not found in DB for user '{}'. Invalidating.",
                        oauthSessionId, user.getUserName());
                invalidateAndRedirect(session, httpRequest, httpResponse);
            }

        } catch (Exception e) {
            logger.error("SessionValidatorInterceptor error: {}", e.getMessage());
        }
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    private void invalidateAndRedirect(Session session, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        session.invalidate();
        response.sendRedirect(request.getContextPath() + "/");
    }

}