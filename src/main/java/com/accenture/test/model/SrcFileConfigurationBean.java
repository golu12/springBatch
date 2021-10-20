package com.accenture.test.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.Serializable;

@Getter
@Setter
@Component
public class SrcFileConfigurationBean implements Serializable {

private static final long serialVersionUID = 1L;
private String fileName;
private File file;
private String entity;
}
