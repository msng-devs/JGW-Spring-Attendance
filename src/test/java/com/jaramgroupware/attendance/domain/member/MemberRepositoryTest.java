package com.jaramgroupware.attendance.domain.member;


import com.jaramgroupware.attendance.config.TestDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Transactional
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private final TestDataUtils testDataUtils = new TestDataUtils();

    @DisplayName("findById - DB에 존재하는 Member의 id가 주어지면, 해당 Member를 리턴한다.")
    @Test
    void findById() {
        //given
        Member testGoal = testDataUtils.getTestMember();
        //when
        Member result = memberRepository.findById(testGoal.getId())
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertEquals(result.toString(),testGoal.toString());
    }

    @DisplayName("findById - DB에 존재하지 않는 Member의 Id가 주어지면, IllegalArgumentException이 발생한다.")
    @Test
    void findById2() {
        //given
        String testMemberUid = "thisIsNotExist";
        //when
        assertThrows(IllegalArgumentException.class,()-> memberRepository.findById(testMemberUid)
                .orElseThrow(IllegalArgumentException::new));
    }

    @DisplayName("findTargetMember - 테스트 데이터 셋의 1번 role과 1번 rank가 주어지면, 첫번째 member를 리턴한다.")
    @Test
    void findTargetMember() {
        //given
        Member testGoal = testDataUtils.getTestMember();
        //when
        List<Member> result = memberRepository.findTargetMember(Collections.singletonList(1),Collections.singletonList(1));
        //then
        assertEquals(1,result.size());
        assertEquals(testGoal.toString(),result.get(0).toString());
    }

    @DisplayName("findTargetMember - 테스트 데이터 셋의 2번 role과 2번 rank가 주어지면, 빈 member를 리턴한다.")
    @Test
    void findTargetMember2() {
        //given
        Member testGoal = testDataUtils.getTestMember();
        //when
        List<Member> result = memberRepository.findTargetMember(Collections.singletonList(2),Collections.singletonList(2));
        //then
        assertEquals(0,result.size());
    }
}