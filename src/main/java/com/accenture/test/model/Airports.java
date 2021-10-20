package com.accenture.test.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter

public class Airports implements Serializable {

    private static final long serialVersionUID = 1L;
    private String iso_country;
}
