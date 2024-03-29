package com.jaramgroupware.attendance.domain.event;


import com.jaramgroupware.attendance.utlis.spec.PredicatesBuilder;
import com.jaramgroupware.attendance.utlis.spec.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


import java.util.ArrayList;
import java.util.List;

//ref : https://attacomsian.com/blog/spring-data-jpa-specifications
public class EventSpecification implements Specification<Event>{

    private final List<SearchCriteria> list = new ArrayList<>();

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        PredicatesBuilder predicatesBuilder = new PredicatesBuilder();
        return predicatesBuilder.build(root,query,builder,list);
    }


    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

}
