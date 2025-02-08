package com.acesif.scholarscraper.service;

import com.acesif.scholarscraper.util.PDFDownloaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.acesif.scholarscraper.dto.Endpoints.SCI_HUB_MIRRORS;

@Slf4j
@Service
public class ScihubScraperService {

    public ResponseEntity<Resource> getPdfByDOIFromSciHub(String title, String doi) {
        title = title.replace(" ", "_");

        log.info("Attempting to fetch PDF for DOI: '{}'", doi);
        String userAgent = PDFDownloaderUtils.getRandomUserAgent();
        log.debug("Generated user agent: {}", userAgent);

        for (String baseUrl : SCI_HUB_MIRRORS) {
            try {
                String pdfUrl = baseUrl + doi;
                log.debug("Trying Sci-Hub mirror: {}", pdfUrl);

                String actualPdfUrl = fetchPdfUrl(pdfUrl, userAgent);

                if (actualPdfUrl != null) {
                    if (!actualPdfUrl.startsWith("http")) {
                        actualPdfUrl = baseUrl + actualPdfUrl;
                    }
                    log.info("Found PDF URL: {}", actualPdfUrl);
                    return PDFDownloaderUtils.downloadPdfFromUrl(title, actualPdfUrl);
                } else {
                    log.warn("No PDF found at: {}", baseUrl);
                }

            } catch (IOException e) {
                log.error("Failed to fetch from: {} - Trying next mirror...", baseUrl, e);
            }

            try {
                log.info("Waiting 5 seconds before trying next Sci-Hub mirror...");
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
                log.warn("Sleep interrupted: {}", ie.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        log.warn("PDF not found on any Sci-Hub mirrors for DOI: '{}'", doi);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ByteArrayResource("PDF not found on any Sci-Hub mirrors".getBytes()));
    }

    private String fetchPdfUrl(String pdfUrl, String userAgent) throws IOException {
        log.debug("Fetching PDF URL from: {}", pdfUrl);

        Document doc = Jsoup.connect(pdfUrl)
                .userAgent(userAgent)
                .timeout(5000)
                .get();

        Element embedElement = doc.selectFirst("embed[type='application/pdf']");
        String actualPdfUrl = (embedElement != null) ? embedElement.attr("src") : null;

        if (actualPdfUrl == null) {
            Element buttonElement = doc.selectFirst("button[onclick]");
            if (buttonElement != null) {
                String onclickText = buttonElement.attr("onclick");
                actualPdfUrl = extractUrlFromOnclick(onclickText);
            }
        }

        if (actualPdfUrl != null && actualPdfUrl.startsWith("//")) {
            actualPdfUrl = "https:" + actualPdfUrl;
        }

        log.debug("Extracted actual PDF URL: {}", actualPdfUrl);
        return actualPdfUrl;
    }

    private String extractUrlFromOnclick(String onclickText) {
        log.debug("Extracting URL from onclick attribute: {}", onclickText);
        Pattern pattern = Pattern.compile("'(https?://[^']+)'");
        Matcher matcher = pattern.matcher(onclickText);
        String extractedUrl = matcher.find() ? matcher.group(1) : null;
        log.debug("Extracted URL: {}", extractedUrl);
        return extractedUrl;
    }
}
