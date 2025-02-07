package com.acesif.scholarscraper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Response {

    private Long id;
    private String title;
    private String link;
    private String doi;
    private String authors;
    private int year;
    private String snippet;
    private int citations;
}
