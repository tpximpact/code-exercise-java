package com.tpximpact.url_shortener.model;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class Alias {
    private String name;
    private String destination;
}
