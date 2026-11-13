package org.nors.dev.codes.lpu.service;

import java.time.Instant;
import java.util.List;
import org.nors.dev.codes.lpu.dto.AdminLinkResponse;
import org.nors.dev.codes.lpu.dto.AdminPortalResponse;
import org.nors.dev.codes.lpu.dto.LinkResponse;
import org.nors.dev.codes.lpu.dto.LinkWriteRequest;
import org.nors.dev.codes.lpu.dto.PortalResponse;
import org.nors.dev.codes.lpu.dto.PortalWriteRequest;
import org.nors.dev.codes.lpu.model.HubLink;
import org.nors.dev.codes.lpu.model.Portal;
import org.nors.dev.codes.lpu.repository.HubLinkRepository;
import org.nors.dev.codes.lpu.repository.PortalRepository;
import org.nors.dev.codes.lpu.security.LinkUrls;
import org.nors.dev.codes.lpu.security.PortalHosts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PortalService {

    private final PortalRepository portalRepository;
    private final HubLinkRepository hubLinkRepository;

    public PortalService(PortalRepository portalRepository, HubLinkRepository hubLinkRepository) {
        this.portalRepository = portalRepository;
        this.hubLinkRepository = hubLinkRepository;
    }

    @Transactional(readOnly = true)
    public PortalResponse publicPortal(String rawCode) {
        String code = requiredCode(rawCode);
        Portal portal = requiredPortal(code);
        List<LinkResponse> links = hubLinkRepository.findByPortalCode(code, true).stream()
                .map(this::toPublicLink)
                .toList();
        return new PortalResponse(
                portal.getCode(),
                portal.getTitle(),
                portal.getSubtitle(),
                portal.getWelcomeLabel(),
                portal.getLayout(),
                links
        );
    }

    @Transactional(readOnly = true)
    public List<AdminPortalResponse> adminPortals() {
        return portalRepository.findAll().stream().map(this::toAdminPortal).toList();
    }

    @Transactional
    public AdminPortalResponse updatePortal(String rawCode, PortalWriteRequest request) {
        Portal portal = requiredPortal(requiredCode(rawCode));
        portal.setTitle(request.title().trim());
        portal.setSubtitle(request.subtitle().trim());
        portal.setWelcomeLabel(request.welcomeLabel().trim());
        portalRepository.save(portal);
        return toAdminPortal(portal);
    }

    @Transactional
    public AdminLinkResponse createLink(String rawCode, LinkWriteRequest request) {
        String code = requiredCode(rawCode);
        Portal portal = requiredPortal(code);
        String name = request.name().trim();
        if (hubLinkRepository.existsByName(code, name, null)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A link with that name already exists");
        }
        HubLink link = new HubLink();
        link.setPortal(portal);
        apply(link, request, name);
        Instant now = Instant.now();
        link.setCreatedAt(now);
        link.setUpdatedAt(now);
        hubLinkRepository.persist(link);
        return toAdminLink(link);
    }

    @Transactional
    public AdminLinkResponse updateLink(Long id, LinkWriteRequest request) {
        HubLink link = requiredLink(id);
        String name = request.name().trim();
        if (hubLinkRepository.existsByName(link.getPortal().getCode(), name, link.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A link with that name already exists");
        }
        apply(link, request, name);
        link.setUpdatedAt(Instant.now());
        return toAdminLink(hubLinkRepository.save(link));
    }

    @Transactional
    public void deleteLink(Long id) {
        hubLinkRepository.delete(requiredLink(id));
    }

    @Transactional
    public void recordClick(String rawCode, Long id) {
        String code = requiredCode(rawCode);
        HubLink link = requiredLink(id);
        if (!link.getPortal().getCode().equals(code) || !link.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found");
        }
        link.setClickCount(link.getClickCount() + 1);
        link.setUpdatedAt(Instant.now());
        hubLinkRepository.save(link);
    }

    private void apply(HubLink link, LinkWriteRequest request, String name) {
        String url = request.url().trim();
        try {
            LinkUrls.check(url);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        link.setName(name);
        link.setUrl(url);
        link.setDescription(request.description().trim());
        link.setIcon(request.icon().trim());
        link.setSortOrder(request.sortOrder());
        String downloadName = request.downloadName() == null ? "" : request.downloadName().trim();
        link.setDownloadName(downloadName.isEmpty() ? null : downloadName);
        link.setNetworkOnly(Boolean.TRUE.equals(request.networkOnly()));
        link.setEnabled(Boolean.TRUE.equals(request.enabled()));
    }

    private AdminPortalResponse toAdminPortal(Portal portal) {
        List<AdminLinkResponse> links = hubLinkRepository.findByPortalCode(portal.getCode(), false).stream()
                .map(this::toAdminLink)
                .toList();
        return new AdminPortalResponse(
                portal.getCode(),
                portal.getTitle(),
                portal.getSubtitle(),
                portal.getWelcomeLabel(),
                portal.getLayout(),
                links
        );
    }

    private LinkResponse toPublicLink(HubLink link) {
        return new LinkResponse(
                link.getId(),
                link.getName(),
                link.getUrl(),
                link.getDescription(),
                link.getIcon(),
                link.getDownloadName(),
                link.isNetworkOnly(),
                link.getSortOrder()
        );
    }

    private AdminLinkResponse toAdminLink(HubLink link) {
        return new AdminLinkResponse(
                link.getId(),
                link.getName(),
                link.getUrl(),
                link.getDescription(),
                link.getIcon(),
                link.getDownloadName(),
                link.isNetworkOnly(),
                link.getSortOrder(),
                link.isEnabled(),
                link.getClickCount()
        );
    }

    private Portal requiredPortal(String code) {
        return portalRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portal not found"));
    }

    private HubLink requiredLink(Long id) {
        return hubLinkRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));
    }

    private static String requiredCode(String rawCode) {
        String code = PortalHosts.normalize(rawCode);
        if (code == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Portal not found");
        }
        return code;
    }
}
