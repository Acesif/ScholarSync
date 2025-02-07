package com.acesif.scholarscraper.service;

import com.acesif.scholarscraper.dto.Response;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoogleScholarScraperService {

    private static final String GOOGLE_SCHOLAR_SEARCH_URL = "https://scholar.google.com/scholar?";
    private static final String CROSSREF_UNIFIED_RESOURCE_API = "https://api.crossref.org/works";
    private static final String SCI_HUB_URL = "https://sci-hub.se/";

    public List<Response> searchScholar(String query, long limit) {
        long id = 1L;
        long current = 0L;
        List<Response> results = new ArrayList<>();
        while (current < limit) {
            try {
                String url = GOOGLE_SCHOLAR_SEARCH_URL + "start="+current+"&q=" + query.replace(" ", "+");

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(5000)
                        .get();

                Elements elements = doc.select(".gs_ri");

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
                    }
                }

            } catch (IOException e) {
                e.fillInStackTrace();
            }
            current += 10L;
        }
        return results;
    }

    public String getDOIFromTitle(String title) {
        try {
            String url = CROSSREF_UNIFIED_RESOURCE_API + "?query.title=" + title.replace(" ", "+");

            Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .timeout(5000)
                    .get();

            JSONObject jsonResponse = new JSONObject(doc.text());

            JSONArray items = jsonResponse.getJSONObject("message").getJSONArray("items");

            if (!items.isEmpty()) {
                JSONObject firstItem = items.getJSONObject(0);

                return firstItem.optString("DOI", "No DOI found");
            } else {
                return "No DOI found";
            }

        } catch (IOException e) {
            e.fillInStackTrace();
            return "Error fetching DOI";
        }
    }

    public String getTitleFromDOI(String doi) {
        try {
            String url = CROSSREF_UNIFIED_RESOURCE_API + "/" + doi;

            Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .timeout(5000)
                    .get();

            JSONObject jsonResponse = new JSONObject(doc.text());

            JSONObject message = jsonResponse.getJSONObject("message");

            JSONArray titleArray = message.optJSONArray("title");

            if (titleArray != null && !titleArray.isEmpty()) {
                return titleArray.getString(0);
            } else {
                return "No title found";
            }

        } catch (IOException e) {
            e.fillInStackTrace();
            return "Error fetching title";
        }
    }

    public ResponseEntity<Resource> getPdfByDOI(String title, String doi) {
        try {
            String pdfUrl = SCI_HUB_URL + doi;
            title = title.replace(" ", "_");

            Document doc = Jsoup.connect(pdfUrl)
                    .userAgent("Mozilla/5.0")
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

            if (actualPdfUrl == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ByteArrayResource("PDF not found on Sci-Hub".getBytes()));
            }

            if (actualPdfUrl.startsWith("//")) {
                actualPdfUrl = "https:" + actualPdfUrl;
            }

            return downloadPdfFromUrl(title,actualPdfUrl);

        } catch (IOException e) {
            e.fillInStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource("Error fetching PDF".getBytes()));
        }
    }

    private String extractUrlFromOnclick(String onclickText) {
        Pattern pattern = Pattern.compile("'(https?://[^']+)'");
        Matcher matcher = pattern.matcher(onclickText);
        return matcher.find() ? matcher.group(1) : null;
    }

    private ResponseEntity<Resource> downloadPdfFromUrl(String title, String pdfUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(pdfUrl, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ByteArrayResource resource = new ByteArrayResource(Objects.requireNonNull(response.getBody()));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+title+".pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .contentLength(response.getBody().length)
                        .body(resource);
            }

        } catch (Exception e) {
            e.fillInStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }

    private int extractCitations(String text) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }

    private int extractYear(String text) {
        Pattern pattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return -1;
    }

}


