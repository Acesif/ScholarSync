package com.acesif.scholarscraper.util;

import com.acesif.scholarscraper.dto.Response;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import static com.acesif.scholarscraper.dto.Endpoints.CROSSREF_UNIFIED_RESOURCE_API;

@Slf4j
public class PDFDownloaderUtils {

    static String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)",
            "Mozilla/5.0 (Linux; Android 10)",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)"
    };

    public static String getRandomUserAgent() {
        Random random = new Random();
        return userAgents[random.nextInt(userAgents.length)];
    }

    public static Response getTitleAndLinkFromDOI(String doi) {
        int maxRetries = 5;
        int attempt = 0;
        int delay = 2000;

        while (true) {
            try {
                String url = CROSSREF_UNIFIED_RESOURCE_API + "/" + doi;
                log.debug("Attempt {} - Fetching title and link for DOI: '{}'", (attempt + 1), doi);
                log.debug("CrossRef API URL: {}", url);

                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .timeout(5000)  // Timeout per request
                        .get();

                JSONObject jsonResponse = new JSONObject(doc.text());
                log.debug("Received response from CrossRef API: {}", jsonResponse);

                JSONObject message = jsonResponse.getJSONObject("message");

                JSONArray titleArray = message.optJSONArray("title");
                String title = (titleArray != null && !titleArray.isEmpty()) ? titleArray.getString(0) : "No title found";

                String link = null;
                JSONArray linkArray = message.optJSONArray("link");
                if (linkArray != null && !linkArray.isEmpty()) {
                    link = linkArray.getJSONObject(0).optString("URL", null);
                }

                if (link == null) {
                    JSONObject resource = message.optJSONObject("resource");
                    if (resource != null) {
                        JSONObject primary = resource.optJSONObject("primary");
                        if (primary != null) {
                            link = primary.optString("URL", null);
                        }
                    }
                }

                Response response = Response.builder()
                        .title(title)
                        .link(link)
                        .build();

                log.info("Found title for DOI '{}': {}, Link: {}", doi, title, link);
                return response;

            } catch (IOException e) {
                attempt++;
                log.error("Attempt {} failed for DOI '{}': {}", attempt, doi, e.getMessage());

                if (attempt >= maxRetries) {
                    log.error("Max retries reached. Giving up on DOI '{}'", doi);
                    return null;
                }

                try {
                    log.info("Retrying in {} ms...", delay);
                    Thread.sleep(delay);
                    delay *= 2;
                } catch (InterruptedException ie) {
                    log.warn("Retry sleep interrupted: {}", ie.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.warn("Failed to retrieve title and link for DOI '{}' after {} attempts", doi, maxRetries);
        return null;
    }

    public static ResponseEntity<Resource> downloadPdfFromUrl(String title, String pdfUrl) {
        try {
            log.info("Downloading PDF from: {}", pdfUrl);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setRedirectStrategy(new DefaultRedirectStrategy())
                    .build();
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            headers.set(HttpHeaders.REFERER, "https://dl.acm.org/");
            headers.set(HttpHeaders.ACCEPT, "application/pdf");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(pdfUrl, HttpMethod.GET, entity, byte[].class);

            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Headers: {}", response.getHeaders());

            if (response.getStatusCode() == HttpStatus.OK &&
                    response.getHeaders().getContentType() != null &&
                    response.getHeaders().getContentType().toString().contains("pdf")) {

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

    public static ResponseEntity<Resource> getFromPlaywright(String title, String pdfUrl) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            System.out.println("Opening URL: " + pdfUrl);
            page.navigate(pdfUrl);
            Thread.sleep(7000);

            String finalPdfUrl = page.url();
            System.out.println("Final resolved PDF URL: " + finalPdfUrl);

            byte[] pdfBytes = downloadPdf(finalPdfUrl);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + title + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.fillInStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private static byte[] downloadPdf(String pdfUrl) throws Exception {
        URI url = new URI(pdfUrl);
        HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.71 Safari/537.36");

        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[4096];
            int n;
            while ((n = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, n);
            }
            return buffer.toByteArray();
        }
    }
}
