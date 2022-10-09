package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.config.Config;
import com.jaramgroupware.attendance.domain.config.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ConfigService {
    private final ConfigRepository configRepository;

    @Transactional
    public void add(String key,String val){
        configRepository.save(Config.builder()
                .name(key)
                .val(val)
                .build());
    }

    @Transactional(readOnly = true)
    public String find(String key){
        return configRepository.findConfigByName(key).getName();
    }
}
