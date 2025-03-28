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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.acesif.scholarscraper.dto.Endpoints.CROSSREF_UNIFIED_RESOURCE_API;
import static com.acesif.scholarscraper.dto.Endpoints.GOOGLE_SCHOLAR_SEARCH_URL;

@Slf4j
@Service
public class GoogleScholarScraperService {

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
                        String doi = extractDOIFromURL(link);

                        log.debug("Processing article: {}", title);

                        results.add(
                                Response.builder()
                                        .id(id++)
                                        .title(title)
                                        .link(link)
                                        .doi(doi == null ? getDOIFromTitleOrLink(title, link) : doi)
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

    public String getDOIFromTitleOrLink(String title, String link) {
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
                String doi = firstItem.optString("DOI", null);
                if (doi != null) {
                    log.info("Found DOI via CrossRef API: {}", doi);
                    return doi;
                }
            }
        } catch (IOException e) {
            log.error("Error fetching DOI for title '{}' from CrossRef: {}", title, e.getMessage(), e);
            String scrapedDOI = scrapeDOIFromPage(link);

            if (scrapedDOI != null) {
                log.info("Found DOI via page scraping: {}", scrapedDOI);
                return scrapedDOI;
            }
        }
        return "DOI not found";
    }

    private String scrapeDOIFromPage(String link) {
        try {
            Document doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(5000)
                    .get();

            Elements links = doc.select("a[href*='doi.org']");
            for (Element element : links) {
                String href = element.attr("href");
                log.debug("Potential DOI link found in href: {}", href);

                String doi = extractDOIFromURL(href);
                if (doi != null) {
                    return doi;
                }
            }

            Elements allElements = doc.getAllElements();
            for (Element element : allElements) {
                String text = element.text();
                if (text.contains("doi.org")) {
                    log.debug("Potential DOI found in text: {}", text);

                    String doi = extractDOIFromURL(text);
                    if (doi != null) {
                        return doi;
                    }
                }
            }

            Elements metaTags = doc.select("meta[content*='doi.org']");
            for (Element meta : metaTags) {
                String content = meta.attr("content");
                log.debug("Potential DOI found in meta tag: {}", content);

                String doi = extractDOIFromURL(content);
                if (doi != null) {
                    return doi;
                }
            }
            log.warn("No DOI found in the provided link: {}", link);
        } catch (IOException e) {
            log.error("Error scraping DOI from link '{}': {}", link, e.getMessage(), e);
        }
        return null;
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

    private String extractDOIFromURL(String link) {
        Pattern doiPattern = Pattern.compile("10\\.\\d{4,9}/[-._;()/:a-zA-Z0-9]+");
        Matcher matcher = doiPattern.matcher(link);
        return matcher.find() ? matcher.group() : null;
    }

}


