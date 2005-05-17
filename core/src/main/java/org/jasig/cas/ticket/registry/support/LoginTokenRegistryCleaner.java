/*
 * Copyright 2005 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.ticket.registry.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.ticket.registry.RegistryCleaner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Class to determine if a loginToken is ready to be removed.
 * <p>
 * The loginToken is a random string written to a Hidden field on the login
 * Form. The loginToken string and a related timestamp are cached in the
 * loginTokens Map while valid. The token is used once and then removed from the
 * Map. This prevents the same Form from being resubmitted by pressing BACK
 * enough times on the Web Browser.
 * </p>
 * <p>
 * This routine removes stale loginTokens from the Map.
 * </p>
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.0
 */
public final class LoginTokenRegistryCleaner implements RegistryCleaner,
    InitializingBean {

    /** Instance of Commons logging. */
    private final Log log = LogFactory.getLog(getClass());

    /** the map containing the login tokens and timestamp. */
    private Map loginTokens;

    /** The timeout value. */
    private long timeOut;

    public void clean() {
        final long currentTime = System.currentTimeMillis();
        final List tokensToDelete = new ArrayList();
        log.info("Started cleaning up login tokens at [" + new Date() + "]");

        synchronized (this.loginTokens) {
            final Set keys = this.loginTokens.keySet();

            for (final Iterator iter = keys.iterator(); iter.hasNext();) {
                final String key = (String) iter.next();
                final Date lastUsed = (Date) this.loginTokens.get(key);

                if ((currentTime - lastUsed.getTime()) > this.timeOut) {
                    tokensToDelete.add(key);
                }
            }

            for (final Iterator iter = tokensToDelete.iterator(); iter
                .hasNext();) {
                final String key = (String) iter.next();
                this.loginTokens.remove(key);
            }
        }
        log.info("Finished cleaning up login tokens at [" + new Date() + "]");
    }

    /**
     * @param loginTokens The loginTokens to set.
     */
    public void setLoginTokens(final Map loginTokens) {
        this.loginTokens = loginTokens;
    }

    /**
     * @param timeOut The timeOut to set.
     */
    public void setTimeOut(final long timeOut) {
        this.timeOut = timeOut;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.loginTokens, "loginTokens cannot be empty.");
        Assert.isTrue(this.timeOut > 0,
            "timeOut must be a positive, greater than 0 value.");
    }
}
