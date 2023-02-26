package com.jaramgroupware.attendance.utlis.code;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@RequiredArgsConstructor
@Component
public class CodeGenerator {

    private final Random random = new Random();

    public String getKey(Integer length){

        StringBuilder stringBuilder = new StringBuilder();
        random.setSeed(System.currentTimeMillis());
        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(9));
        }


        return stringBuilder.toString();
    }
}
