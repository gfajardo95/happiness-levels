package com.fajardo.happinesslevels.models;

import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DefaultCoder(AvroCoder.class)
public class Tweet {
    private String text;
    private String country;
    private int sentiment;
}