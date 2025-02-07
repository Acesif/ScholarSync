package com.acesif.scholarscraper.dto;

import lombok.Data;

@Data
public class Request {

    private String query;
    private long limit;
}
