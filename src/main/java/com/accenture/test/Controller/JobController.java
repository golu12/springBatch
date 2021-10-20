package com.accenture.test.Controller;

import com.accenture.test.Constant.FileLoaderConstant;
import com.accenture.test.model.SrcFileConfigurationBean;
import com.accenture.test.Service.SrcServiceConf;
import com.accenture.test.batch.CustomJobParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path ="/fileloader")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job processJob;

    @Autowired
    private SrcServiceConf srcServiceConf;

    private static final Logger LOG = LoggerFactory.getLogger(JobController.class);


    @RequestMapping(value = "/invokeJob")
    public String handle(@RequestParam String sourceName) throws Exception{

        JobExecution jobExecution = null;
        JobParameters jobParameter = null;
        List<SrcFileConfigurationBean> lstSrcFileConfigurationBeans = null;
        String jobStatus = null;

        try {
            lstSrcFileConfigurationBeans = srcServiceConf.readSourceFiles(sourceName);
            jobStatus = FileLoaderConstant.JOB_STATUS_COMPLETED;
            for(SrcFileConfigurationBean file : lstSrcFileConfigurationBeans){
                jobParameter = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                        .addParameter(FileLoaderConstant.FILENAME, new CustomJobParameter<SrcFileConfigurationBean>(file)).toJobParameters();

                jobExecution= jobLauncher.run(processJob, jobParameter);
                jobStatus = jobExecution.getStatus().toString();
            }
        }catch (Exception e){
            LOG.error("Error Occured in handle execution method");
        }
        return jobStatus;
    }
}


