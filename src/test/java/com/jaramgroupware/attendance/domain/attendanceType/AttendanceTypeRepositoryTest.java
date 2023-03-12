package com.jaramgroupware.attendance.domain.attendanceType;


import com.jaramgroupware.attendance.config.TestDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Transactional
@DataJpaTest
class AttendanceTypeRepositoryTest {

    private final TestDataUtils testUtils = new TestDataUtils();

    @Autowired
    private AttendanceTypeRepository attendanceTypeRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAttendanceTypeById() {
        //given
        AttendanceType testGoal = testUtils.getTestAttendanceType();
        //when
        AttendanceType result = attendanceTypeRepository.findAttendanceTypeById(1)
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertEquals(result.toString(),testGoal.toString());
    }

    @Test
    void findAllBy() {
        //given
        List<AttendanceType> testGoal = new ArrayList<AttendanceType>();
        testGoal.add(testUtils.getTestAttendanceType2());
        testGoal.add(testUtils.getTestAttendanceType());
        //when
        List<AttendanceType> results = attendanceTypeRepository.findAllBy()
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertTrue(testUtils.isListSame(testGoal,results));
    }
}