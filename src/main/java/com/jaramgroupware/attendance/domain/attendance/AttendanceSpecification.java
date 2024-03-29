package com.jaramgroupware.attendance.domain.attendance;


import com.jaramgroupware.attendance.utlis.spec.PredicatesBuilder;
import com.jaramgroupware.attendance.utlis.spec.SearchCriteria;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;


import java.util.ArrayList;
import java.util.List;

//ref : https://attacomsian.com/blog/spring-data-jpa-specifications
public class AttendanceSpecification implements Specification<Attendance>{

    private final List<SearchCriteria> list = new ArrayList<>();

    @Override
    public Predicate toPredicate(Root<Attendance> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        PredicatesBuilder predicatesBuilder = new PredicatesBuilder();
        query.distinct(true);

        //count query error
        //ref : https://starrybleu.github.io/development/2018/08/10/jpa-n+1-fetch-strategy-specification.html
        if (query.getResultType() != Long.class && query.getResultType() != long.class){
            root.fetch("member", JoinType.LEFT);
            root.fetch("timeTable", JoinType.LEFT);
            root.fetch("attendanceType", JoinType.LEFT);
        }

        return predicatesBuilder.build(root,query,builder,list);
    }


    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

}
