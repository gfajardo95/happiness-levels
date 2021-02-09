package com.fajardo.happinesslevels;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "pipeline")
public class PipelineProperties {

    private boolean testing;
}
