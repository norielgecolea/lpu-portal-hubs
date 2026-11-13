package org.nors.dev.codes.lpu.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.nors.dev.codes.lpu.dto.PortalResponse;
import org.nors.dev.codes.lpu.security.PortalHosts;
import org.nors.dev.codes.lpu.service.PortalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/public")
public class PublicPortalController {

    private final PortalService portalService;

    public PublicPortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/portal")
    public PortalResponse current(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-Host");
        String host = forwarded == null || forwarded.isBlank() ? request.getHeader("Host") : forwarded;
        String code = PortalHosts.codeFor(host);
        if (code == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Open learnershub.lpulaguna.com or adminportal.lpulaguna.com"
            );
        }
        return portalService.publicPortal(code);
    }

    @GetMapping("/portals/{code}")
    public PortalResponse portal(@PathVariable String code) {
        return portalService.publicPortal(code);
    }

    @PostMapping("/portals/{code}/links/{id}/click")
    public ResponseEntity<Void> click(@PathVariable String code, @PathVariable Long id) {
        portalService.recordClick(code, id);
        return ResponseEntity.noContent().build();
    }
}
