package com.jaramgroupware.attendance.domain.timeTable;

import com.jaramgroupware.attendance.utlis.parse.ParseByNameBuilder;
import com.jaramgroupware.attendance.utlis.spec.SearchCriteria;
import com.jaramgroupware.attendance.utlis.spec.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TimeTableSpecificationBuilder {
    private final ParseByNameBuilder parseByNameBuilder;
    private final LocalDateTime maxDateTime = LocalDateTime.parse("9999-12-31 23:59:59",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final LocalDateTime minDateTime = LocalDateTime.parse("0001-01-01 00:00:00",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @Getter
    @AllArgsConstructor
    private enum EqualKeys {


        EVENT("eventID","event","Long"),
        CREATEBY("createBy","createBy","String"),
        MODIFIEDBY("modifiedBy","modifiedBy","String");

        private final String queryParamName;
        private final String tableName;
        private final String type;
    }

    @Getter
    @AllArgsConstructor
    private enum RangeKeys {

        CREATEDDATETIME("startCreatedDateTime","endCreatedDateTime","createdDateTime"),
        MODIFIEDDATETIME("startModifiedDateTime","endModifiedDateTime","modifiedDateTime");

        private final String startQueryParamName;
        private final String endQueryParamName;
        private final String tableName;
    }

    @Getter
    @AllArgsConstructor
    private enum RangeBetweenKeys {

        DATETIME("startDateTime","endDateTime","startDateTime","endDateTime");

        private final String startQueryParamName;
        private final String endQueryParamName;
        private final String firTableName;
        private final String lastTableName;
    }

    @Getter
    @AllArgsConstructor
    private enum LikeKeys{

        NAME("name","name");

        private final String queryParamName;
        private final String tableName;
    }

    public TimeTableSpecification toSpec(MultiValueMap<String, String> queryParam){


        TimeTableSpecification specification = new TimeTableSpecification();

            for (EqualKeys key: EqualKeys.values()) {
                if(queryParam.containsKey(key.getQueryParamName())){
                    specification.add(new SearchCriteria(key.getTableName()
                            , Collections.singletonList(parseByNameBuilder.parse(queryParam.getFirst(key.getQueryParamName()), key.getType()))
                            , SearchOperation.EQUAL));
                }
            }

            for (RangeKeys key: RangeKeys.values()) {
                if(queryParam.containsKey(key.getStartQueryParamName()) || queryParam.containsKey(key.getEndQueryParamName()) ){
                    LocalDateTime start = minDateTime;
                    LocalDateTime end = maxDateTime;
                    try {
                        start = (queryParam.containsKey(key.getStartQueryParamName()))
                                ? LocalDateTime.parse(Objects.requireNonNull(queryParam.getFirst(key.getStartQueryParamName())), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                : minDateTime;
                        end = (queryParam.containsKey(key.getEndQueryParamName()))
                                ? LocalDateTime.parse(Objects.requireNonNull(queryParam.getFirst(key.getEndQueryParamName())),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                : maxDateTime;
                    }catch (Exception e){
                        throw new IllegalArgumentException(key.getStartQueryParamName()+"혹은"+key.getEndQueryParamName()+"의 형식이 잘못됬습니다.");
                    }

                    if(start.isAfter(end)){
                        throw new IllegalArgumentException(key.getStartQueryParamName()+"과"+ key.getStartQueryParamName()+"인자가 잘못됬습니다. 범위가 적절한지 다시 확인하세요.");
                    }
                    specification.add(new SearchCriteria(key.getTableName(),
                            Arrays.asList(new LocalDateTime[] {start,end}),
                            SearchOperation.BETWEEN
                            ));
                }
            }

        for (RangeBetweenKeys key: RangeBetweenKeys.values()) {
            if(queryParam.containsKey(key.getStartQueryParamName()) || queryParam.containsKey(key.getEndQueryParamName()) ){
                LocalDateTime start = minDateTime;
                LocalDateTime end = maxDateTime;
                try {
                    start = (queryParam.containsKey(key.getStartQueryParamName()))
                            ? LocalDateTime.parse(queryParam.getFirst(key.getStartQueryParamName()), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            : minDateTime;
                    end = (queryParam.containsKey(key.getEndQueryParamName()))
                            ? LocalDateTime.parse(queryParam.getFirst(key.getEndQueryParamName()),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            : maxDateTime;
                }catch (Exception e){
                    throw new IllegalArgumentException(key.getStartQueryParamName()+"혹은"+key.getEndQueryParamName()+"의 형식이 잘못됬습니다.");
                }

                if(start.isAfter(end)){
                    throw new IllegalArgumentException(key.getStartQueryParamName()+"과"+ key.getStartQueryParamName()+"인자가 잘못됬습니다. 범위가 적절한지 다시 확인하세요.");
                }
                specification.add(new SearchCriteria(key.getFirTableName(),
                        Arrays.asList(new LocalDateTime[] {start,end}),
                        SearchOperation.BETWEEN
                ));
                specification.add(new SearchCriteria(key.getLastTableName(),
                        Arrays.asList(new LocalDateTime[] {start,end}),
                        SearchOperation.BETWEEN
                ));
            }
        }
            for (LikeKeys key : LikeKeys.values()) {
                if(queryParam.containsKey(key.getQueryParamName())){
                    specification.add(new SearchCriteria(key.getTableName()
                            , Arrays.asList(new String[]{queryParam.getFirst(key.getQueryParamName())})
                            ,SearchOperation.MATCH));
                }
            }
            return specification;
    }

}

