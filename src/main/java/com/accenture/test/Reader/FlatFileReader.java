package com.accenture.test.Reader;

import com.accenture.test.Constant.FileLoaderConstant;
import com.accenture.test.model.SrcFileConfigurationBean;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;


@Component
public class FlatFileReader implements ItemReader<Object> , StepExecutionListener {



    private static final Logger LOG = LoggerFactory.getLogger(FlatFileReader.class);


    private static int BUFFER_SIZE = 1024 *1024 *10;
    InputStreamReader inputStreamReader = null;
    private LineIterator lineItr= null;
    InputStream inputStream = null;
    String[] globalHeaderArray = null;
    @Autowired
    private SrcFileConfigurationBean srcFileConfigurationBean ;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameter = null;
        try{
            jobParameter = stepExecution.getJobExecution().getJobParameters();
            srcFileConfigurationBean = (SrcFileConfigurationBean) jobParameter.getParameters().get(FileLoaderConstant.FILENAME).getValue();
            globalHeaderArray = readFileHeader1(srcFileConfigurationBean.getFile());
        }catch(Exception e){
            LOG.error("Error Occured in before step method ", e);

        }
    }

    /**
     * Methed to read header from the file.
     * @param file
     * @return headers
     */
    private String[] readFileHeader1(File file)  {
        String[] headerArray = null;
        String header = null;
        try{
            inputStream = new FileInputStream(file.toString());
            inputStreamReader = new InputStreamReader(new BOMInputStream(inputStream));
            lineItr = new LineIterator(new BufferedReader(inputStreamReader, BUFFER_SIZE));
            header = lineItr.nextLine();
            headerArray = header.split(",");
            getHeader(headerArray);
        }catch(Exception e){
            LOG.error("data error");
        }
        return headerArray;
        }

    private void getHeader(String[] data) {
    for(int i=0;i < data.length ; i++){
        if(data[i].startsWith("\"") && data[i].endsWith("\"")){
            data[i]= data[i].substring(1,data[i].length());
            data[i]= data[i].substring(0,data[i].length()-1);
        }
        }
    }

    @Override
    public Object read() {
        Object pojoObj = null;
        String line;
        String[] data = null;
        BeanInfo beanInfo = null;
        String setter = null;
        Class <?> parameterType = null;
        Method setterMethod = null;
        Class<?> modelClassName = getClassName();
        try {
            if(lineItr.hasNext()){
                pojoObj= modelClassName.newInstance();
                line = lineItr.nextLine();
                data = line.split(",");
                for(int i =0 ;i < globalHeaderArray.length ; i++){
                    beanInfo = Introspector.getBeanInfo(modelClassName);
                    for(PropertyDescriptor propertyDesc: beanInfo.getPropertyDescriptors()){
                        if(propertyDesc.getName().toString().equalsIgnoreCase(globalHeaderArray[i])){
                            setter = propertyDesc.getWriteMethod().getName();
                            parameterType = propertyDesc.getPropertyType();
                            setterMethod =  modelClassName.getDeclaredMethod(setter,parameterType);
                            setterMethod.invoke(pojoObj, data[i]);
                            break;
                        }
                    }
                }

            }

        }catch(Exception e){
            LOG.error("Exception Occurred in read method of flatFileReader",e);
        }
        return pojoObj;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(lineItr != null){
            lineItr.close();
        }
        IOUtils.closeQuietly(inputStreamReader);
        IOUtils.closeQuietly(inputStream);
        return null;
    }

    /**
     * Method to retrieve model class.
     * @return model class
     */
    public Class<?> getClassName(){
        Class<?> className = null;
        try{
            className = Class.forName(srcFileConfigurationBean.getEntity());
        }catch(ClassNotFoundException e ){
            LOG.error("Exception Occurred in getClass method" ,e);
        }
        return className;
    }
}
