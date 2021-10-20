package com.accenture.test.Writer;


import com.accenture.test.model.AirportData;
import com.accenture.test.model.Airports;
import com.accenture.test.model.Countries;
import com.accenture.test.Repository.CountryCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class FlatFileWriter implements ItemWriter<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(FlatFileWriter.class);

    private CountryCodeRepository countryCodeRepository;

    public FlatFileWriter(CountryCodeRepository codeRepository){
        this.countryCodeRepository=codeRepository;
    }


    @Override
    public void write(List<? extends Object> messages) throws Exception {
        AirportData airports = null;
        List<String > data = new ArrayList<>();
        Map<String,Integer> unSorted = new HashMap<>();
        Map<String,String> countryCode = new HashMap<>();
        LinkedHashMap<String,Integer> sortedMap = new LinkedHashMap<>();
        java.io.FileWriter writer = null;
        try {
            if(messages != null){
                for(Object obj : messages){
                    if(obj instanceof Airports){
                        data.add(((Airports) obj).getIso_country());
                        if(data.size() == messages.size()){
                            List<String> keys = getSortedListOfCountryCode(data, unSorted, sortedMap);

                            for(int i=0 ;i < keys.size();i++){
                                airports = new AirportData();
                                airports.setCountryCode(keys.get(i));
                                countryCodeRepository.save(airports);
                            }
                        }
                    } else {
                        countryCode.put(((Countries) obj).getCode(),  ((Countries) obj).getName());
                        if (countryCode.size() == messages.size()){
                            List<AirportData> retrieveCodeList = countryCodeRepository.findAll();
                            for(int i=0;i < retrieveCodeList.size() ;i++){
                                for(Map.Entry<String, String> countriesMap : countryCode.entrySet()){
                                    if(countriesMap.getKey().equals(retrieveCodeList.get(i).getCountryCode())){
                                        data.add(countriesMap.getValue());
                                        if(data.size()==retrieveCodeList.size()){
                                            writer = new java.io.FileWriter("src/main/resources/output/outputTop10Countries.txt");
                                        for( String countryName : data){
                                            writer.write(countryName + System.lineSeparator());
                                        }
                                        writer.close();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            LOG.error("No data present in the message Object",e);
        }


    }

    private List<String> getSortedListOfCountryCode(List<String> countryCodeList, Map<String, Integer> unSorted, LinkedHashMap<String, Integer> sortedMap) {
        Set<String> dinstict;
        dinstict = new HashSet<>(countryCodeList);
        for (String codeList : dinstict) {
            unSorted.put(codeList, Collections.frequency(countryCodeList,codeList));
        }
        unSorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        List<String> countryCode = sortedMap.entrySet().stream().map(Map.Entry::getKey).limit(10).collect(Collectors.toList());
    return countryCode;
    }
}
