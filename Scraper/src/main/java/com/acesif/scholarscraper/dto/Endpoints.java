package com.acesif.scholarscraper.dto;

import lombok.Data;

@Data
public class Endpoints {
    public static final String GOOGLE_SCHOLAR_SEARCH_URL = "https://scholar.google.com/scholar";
    public static final String CROSSREF_UNIFIED_RESOURCE_API = "https://api.crossref.org/works";
    public static final String[] SCI_HUB_MIRRORS = {
            "https://sci-hub.se/",
            "http://sci-hub.kr/",
            "https://sci-hub.st/",
            "https://sci-hub.tw/",
            "http://sci-hub.st/",
            "http://sci-hub.tw/"
    };
}
