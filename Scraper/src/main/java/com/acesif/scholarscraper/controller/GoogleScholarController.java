package com.acesif.scholarscraper.controller;

import com.acesif.scholarscraper.dto.Request;
import com.acesif.scholarscraper.dto.Response;
import com.acesif.scholarscraper.service.GoogleScholarScraperService;
import com.acesif.scholarscraper.service.ScihubScraperService;
import com.acesif.scholarscraper.util.PDFDownloaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class GoogleScholarController {

    private final GoogleScholarScraperService scraperService;
    private final ScihubScraperService scihubScraperService;

    public GoogleScholarController(GoogleScholarScraperService scraperService, ScihubScraperService scihubScraperService) {
        this.scraperService = scraperService;
        this.scihubScraperService = scihubScraperService;
    }

    @PostMapping("/search")
    public List<Response> searchGoogleScholar(@RequestBody Request request) {
        return scraperService.searchScholar(request.getQuery(), request.getLimit());
    }

    @PostMapping("/pdf")
    public ResponseEntity<? extends Resource> downloadPaper(
            @RequestParam String doi
    ) {
        Response titleAndLink = PDFDownloaderUtils.getTitleAndLinkFromDOI(doi);
        if (titleAndLink != null) {
            ResponseEntity<Resource> response = scihubScraperService.getPdfByDOIFromSciHub(titleAndLink.getTitle(), doi);
            if (!(response.getStatusCode().equals(HttpStatus.OK))){
                if (titleAndLink.getLink().contains("aclanthology.org")){
                    return PDFDownloaderUtils.downloadPdfFromUrl(titleAndLink.getTitle(), titleAndLink.getLink()+".pdf");
                }
                try {
                    return PDFDownloaderUtils.downloadPdfFromUrl(titleAndLink.getTitle(), titleAndLink.getLink());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return PDFDownloaderUtils.getFromPlaywright(titleAndLink.getTitle(), titleAndLink.getLink());
                }
            }
            return response;
        }
        return ResponseEntity.noContent().build();
    }
}

