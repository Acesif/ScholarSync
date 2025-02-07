package com.acesif.scholarscraper.controller;

import com.acesif.scholarscraper.dto.Request;
import com.acesif.scholarscraper.dto.Response;
import com.acesif.scholarscraper.service.GoogleScholarScraperService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GoogleScholarController {

    private final GoogleScholarScraperService scraperService;

    public GoogleScholarController(GoogleScholarScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @PostMapping("/search")
    public List<Response> searchGoogleScholar(@RequestBody Request request) {
        return scraperService.searchScholar(request.getQuery(), request.getLimit());
    }

    @GetMapping("/pdf")
    public ResponseEntity<? extends Resource> downloadPaper(
            @RequestParam String doi
    ) {
        String title = scraperService.getTitleFromDOI(doi);
        return scraperService.getPdfByDOI(title, doi);
    }
}

