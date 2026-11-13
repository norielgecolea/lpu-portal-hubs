package org.nors.dev.codes.lpu.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.nors.dev.codes.lpu.dto.AdminLinkResponse;
import org.nors.dev.codes.lpu.dto.AdminPortalResponse;
import org.nors.dev.codes.lpu.dto.LinkWriteRequest;
import org.nors.dev.codes.lpu.dto.PortalWriteRequest;
import org.nors.dev.codes.lpu.service.PortalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PortalAdminController {

    private final PortalService portalService;

    public PortalAdminController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/portals")
    public List<AdminPortalResponse> portals() {
        return portalService.adminPortals();
    }

    @PutMapping("/portals/{code}")
    public AdminPortalResponse updatePortal(
            @PathVariable String code,
            @Valid @RequestBody PortalWriteRequest request
    ) {
        return portalService.updatePortal(code, request);
    }

    @PostMapping("/portals/{code}/links")
    public ResponseEntity<AdminLinkResponse> createLink(
            @PathVariable String code,
            @Valid @RequestBody LinkWriteRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portalService.createLink(code, request));
    }

    @PutMapping("/links/{id}")
    public AdminLinkResponse updateLink(@PathVariable Long id, @Valid @RequestBody LinkWriteRequest request) {
        return portalService.updateLink(id, request);
    }

    @DeleteMapping("/links/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        portalService.deleteLink(id);
        return ResponseEntity.noContent().build();
    }
}
