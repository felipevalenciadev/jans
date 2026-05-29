package io.jans.casa.core.model;

import io.jans.orm.annotation.AttributeName;
import io.jans.orm.annotation.DataEntry;
import io.jans.orm.annotation.ObjectClass;
import io.jans.orm.model.base.BaseEntry;

@DataEntry
@ObjectClass("jansSessId")
public class JansSessId extends BaseEntry {

    @AttributeName(name = "jansId")
    private String sessionId;

    @AttributeName(name = "jansState")
    private String state;

    @AttributeName(name = "jansUsrDN")
    private String userDn;

    public String getSessionId() { return sessionId; }
    public String getState()     { return state; }
    public String getUserDn()    { return userDn; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setState(String state)         { this.state = state; }
    public void setUserDn(String userDn)       { this.userDn = userDn; }

}