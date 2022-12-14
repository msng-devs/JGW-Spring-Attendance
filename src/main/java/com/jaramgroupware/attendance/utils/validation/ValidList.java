package com.jaramgroupware.attendance.utils.validation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.validation.Valid;
import java.util.*;

@Data
@Getter
@Setter
public class ValidList<E> implements List<E> {

    @Valid
    @Delegate
    private List<E> list = new ArrayList<>();
}
