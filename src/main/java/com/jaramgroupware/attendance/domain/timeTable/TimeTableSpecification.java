//package com.jaramgroupware.attendance.domain.timeTable;
//
//import com.jaramgroupware.attendance.utils.spec.PredicatesBuilder;
//import com.jaramgroupware.attendance.utils.spec.SearchCriteria;
//import org.springframework.data.jpa.domain.Specification;
//
//import javax.persistence.criteria.*;
//import java.util.ArrayList;
//import java.util.List;
//
////ref : https://attacomsian.com/blog/spring-data-jpa-specifications
//public class TimeTableSpecification implements Specification<TimeTable>{
//
//    private final List<SearchCriteria> list = new ArrayList<>();
//
//    public void add(SearchCriteria criteria) {
//        list.add(criteria);
//    }
//
//    @Override
//    public Predicate toPredicate(Root<TimeTable> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//        PredicatesBuilder predicatesBuilder = new PredicatesBuilder();
//
//        query.distinct(true);
//
//        if (query.getResultType() != Long.class && query.getResultType() != long.class){
//            root.fetch("event", JoinType.LEFT);
//        }
//
//        return predicatesBuilder.build(root,query,builder,list);
//    }
//}
