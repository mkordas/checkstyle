/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.puppycrawl.tools.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

class DebugAuditAdapter implements AuditListener {
    /** Keeps track of the number of errors. */
    private boolean called;

    public boolean wasCalled() {
        return called;
    }

    public void resetListener() {
        called = false;
    }

    @Override
    public void addError(AuditEvent event) {
        called = true;
    }

    @Override
    public void addException(AuditEvent event, Throwable throwable) {
        called = true;
    }

    @Override
    public void auditStarted(AuditEvent event) {
        called = true;
    }

    @Override
    public void fileStarted(AuditEvent event) {
        called = true;
    }

    @Override
    public void auditFinished(AuditEvent event) {
        called = true;
    }

    @Override
    public void fileFinished(AuditEvent event) {
        called = true;
    }
}
