package com.jaramgroupware.attendance.utlis.validation;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;


import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class ValidList<E> implements List<E> {

    @Valid
    @Delegate
    private List<E> list = new ArrayList<>();
}
