package com.acesif.scholarscraper.service;

import com.acesif.scholarscraper.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GoogleScholarScraperService {

    private static final String GOOGLE_SCHOLAR_SEARCH_URL = "https://scholar.google.com/scholar";
    private static final String CROSSREF_UNIFIED_RESOURCE_API = "https://api.crossref.org/works";
    private static final String[] SCI_HUB_MIRRORS = {
            "https://sci-hub.se/",
            "http://sci-hub.kr/",
            "https://sci-hub.st/",
            "https://sci-hub.tw/",
            "http://sci-hub.st/",
            "http://sci-hub.tw/"
    };

    public List<Response> searchScholar(String query, long limit) {
        long id = 1L;
        long current = 0L;
        List<Response> results = new ArrayList<>();

        log.info("Starting Google Scholar search for query: '{}', limit: {}", query, limit);

        while (current < limit) {
            try {
                String url = GOOGLE_SCHOLAR_SEARCH_URL + "?start=" + current + "&q=" + query.replace(" ", "+");
                log.debug("Fetching URL: {}", url);

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(5000)
                        .get();

                Elements elements = doc.select(".gs_ri");
                log.info("Fetched {} results from Google Scholar for page starting at {}", elements.size(), current);

                for (Element element : elements) {
                    Element titleElement = element.selectFirst(".gs_rt a");
                    Element authorElement = element.selectFirst(".gs_a");
                    Element snippetElement = element.selectFirst(".gs_rs");
                    Element citationsElement = element.selectFirst("a[href*='cites']");

                    if (titleElement != null) {
                        String title = titleElement.text();
                        String link = titleElement.attr("href");
                        String authorInfo = authorElement != null ? authorElement.text() : "No author info";
                        String snippet = snippetElement != null ? snippetElement.text() : "No description info available";
                        String citations = citationsElement != null ? citationsElement.text() : "No citations info available";
                        int citationCount = extractCitations(citations);
                        int year = extractYear(authorInfo);
                        String doi = getDOIFromTitle(title);

                        log.debug("Processing article: {}", title);

                        results.add(
                                Response.builder()
                                        .id(id++)
                                        .title(title)
                                        .link(link)
                                        .doi(doi)
                                        .authors(authorInfo)
                                        .year(year)
                                        .snippet(snippet)
                                        .citations(citationCount)
                                        .build()
                        );
                    } else {
                        log.warn("Skipped an entry due to missing title element.");
                    }
                }

            } catch (IOException e) {
                log.error("Failed to fetch Google Scholar results at start index {}: {}", current, e.getMessage(), e);
            }
            current += 10L;
        }

        log.info("Search completed. Retrieved {} articles.", results.size());
        return results;
    }

    public String getDOIFromTitle(String title) {
        try {
            String url = CROSSREF_UNIFIED_RESOURCE_API + "?query.title=" + title.replace(" ", "+");
            log.debug("Fetching DOI for title: '{}'", title);
            log.debug("CrossRef API URL: {}", url);

            Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .timeout(5000)
                    .get();

            JSONObject jsonResponse = new JSONObject(doc.text());
            log.debug("Received response from CrossRef API: {}", jsonResponse);

            JSONArray items = jsonResponse.getJSONObject("message").getJSONArray("items");

            if (!items.isEmpty()) {
                JSONObject firstItem = items.getJSONObject(0);
                String doi = firstItem.optString("DOI", "No DOI found");
                log.info("Found DOI: {}", doi);
                return doi;
            } else {
                log.warn("No DOI found for title: '{}'", title);
                return "No DOI found";
            }

        } catch (IOException e) {
            log.error("Error fetching DOI for title '{}': {}", title, e.getMessage(), e);
            return "Error fetching DOI";
        }
    }

    public String getTitleFromDOI(String doi) {
        try {
            String url = CROSSREF_UNIFIED_RESOURCE_API + "/" + doi;
            log.debug("Fetching title for DOI: '{}'", doi);
            log.debug("CrossRef API URL: {}", url);

            Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .timeout(5000)
                    .get();

            JSONObject jsonResponse = new JSONObject(doc.text());
            log.debug("Received response from CrossRef API: {}", jsonResponse);

            JSONObject message = jsonResponse.getJSONObject("message");
            JSONArray titleArray = message.optJSONArray("title");

            if (titleArray != null && !titleArray.isEmpty()) {
                String title = titleArray.getString(0);
                log.info("Found title for DOI '{}': {}", doi, title);
                return title;
            } else {
                log.warn("No title found for DOI: '{}'", doi);
                return "No title found";
            }

        } catch (IOException e) {
            log.error("Error fetching title for DOI '{}': {}", doi, e.getMessage(), e);
            return "Error fetching title";
        }
    }

    public ResponseEntity<Resource> getPdfByDOI(String title, String doi) {
        title = title.replace(" ", "_");

        String[] userAgents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)",
                "Mozilla/5.0 (Linux; Android 10)",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)"
        };

        Random random = new Random();
        String userAgent = userAgents[random.nextInt(userAgents.length)];

        log.info("Attempting to fetch PDF for DOI: '{}'", doi);
        log.debug("Generated user agent: {}", userAgent);

        for (String baseUrl : SCI_HUB_MIRRORS) {
            try {
                String pdfUrl = baseUrl + doi;
                log.debug("Trying Sci-Hub mirror: {}", pdfUrl);

                String actualPdfUrl = fetchPdfUrl(pdfUrl, userAgent);

                if (actualPdfUrl != null) {
                    if (!actualPdfUrl.startsWith("http")){
                        actualPdfUrl = baseUrl + actualPdfUrl;
                    }
                    log.info("Found PDF URL: {}", actualPdfUrl);
                    return downloadPdfFromUrl(title, actualPdfUrl);
                } else {
                    log.warn("No PDF found at: {}", baseUrl);
                }

            } catch (IOException e) {
                log.error("Failed to fetch from: {} - Trying next mirror...", baseUrl, e);
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

    private ResponseEntity<Resource> downloadPdfFromUrl(String title, String pdfUrl) {
        try {
            log.info("Downloading PDF from: {}", pdfUrl);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(pdfUrl, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ByteArrayResource resource = new ByteArrayResource(Objects.requireNonNull(response.getBody()));
                log.info("Successfully downloaded PDF for: '{}'", title);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + title + ".pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .contentLength(response.getBody().length)
                        .body(resource);
            } else {
                log.warn("Failed to download PDF from: {}", pdfUrl);
            }

        } catch (Exception e) {
            log.error("Error downloading PDF from '{}': {}", pdfUrl, e.getMessage(), e);
        }

        return ResponseEntity.badRequest().build();
    }

    private int extractCitations(String text) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            int citationCount = Integer.parseInt(matcher.group());
            log.debug("Extracted citation count: {}", citationCount);
            return citationCount;
        }

        log.warn("No citations found in text: {}", text);
        return 0;
    }

    private int extractYear(String text) {
        Pattern pattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group());
            log.debug("Extracted year: {}", year);
            return year;
        }

        log.warn("No valid year found in text: {}", text);
        return -1;
    }

}


