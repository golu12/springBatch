package com.accenture.test.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class AirportData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) long id;

    @Column
    private String countryCode;
}
