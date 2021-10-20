package com.accenture.test.Processor;

import org.springframework.batch.item.ItemProcessor;

public class FileProcessor implements ItemProcessor<Object, Object> {


    @Override
    public Object process(Object data) throws Exception {
        return data;
    }
}
