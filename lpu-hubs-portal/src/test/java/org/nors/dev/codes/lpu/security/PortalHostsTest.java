package org.nors.dev.codes.lpu.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PortalHostsTest {

    @Test
    void mapsProductionHosts() {
        assertEquals("ADMIN", PortalHosts.codeFor("adminportal.lpulaguna.com"));
        assertEquals("LEARNER", PortalHosts.codeFor("learnershub.lpulaguna.com"));
        assertEquals("ADMIN", PortalHosts.codeFor("AdminPortal.lpulaguna.com:443"));
        assertEquals("LEARNER", PortalHosts.codeFor("learnershub.lpulaguna.com, proxy.example"));
    }

    @Test
    void ignoresUnknownHosts() {
        assertNull(PortalHosts.codeFor("localhost"));
        assertNull(PortalHosts.codeFor("localhost:4200"));
        assertNull(PortalHosts.codeFor(null));
    }

    @Test
    void normalizesPortalCodes() {
        assertEquals("ADMIN", PortalHosts.normalize("admin"));
        assertEquals("LEARNER", PortalHosts.normalize(" LEARNER "));
        assertNull(PortalHosts.normalize("staff"));
    }

    @Test
    void acceptsHttpAndSitePathsOnly() {
        LinkUrls.check("https://helpdesk.lpulaguna.com");
        LinkUrls.check("http://web.lpu-laguna.edu.ph:8090");
        LinkUrls.check("/LIT_LPU_V5.apk");
        assertThrows(IllegalArgumentException.class, () -> LinkUrls.check("javascript:alert(1)"));
        assertThrows(IllegalArgumentException.class, () -> LinkUrls.check("//evil.example"));
    }
}
