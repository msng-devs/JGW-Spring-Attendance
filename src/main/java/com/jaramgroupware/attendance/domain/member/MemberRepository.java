package com.jaramgroupware.attendance.domain.member;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<Member,String>, JpaSpecificationExecutor<Member>{

    @Query(value = "SELECT\n" +
            "  m.MEMBER_PK,\n" +
            "  m.MEMBER_EMAIL,\n" +
            "  m.MEMBER_NM,\n" +
            "  m.ROLE_ROLE_PK,\n" +
            "  m.MEMBER_STATUS\n" +
            "FROM\n" +
            "  MEMBER m\n" +
            "  INNER JOIN MEMBER_INFO m_info\n" +
            "  ON m_info.MEMBER_MEMBER_PK = m.MEMBER_PK\n" +
            "  INNER JOIN MEMBER_LEAVE_ABSENCE m_la\n" +
            "  ON m_la.MEMBER_MEMBER_PK = m.MEMBER_PK\n" +
            "WHERE\n" +
            "  m.ROLE_ROLE_PK IN :roles\n" +
            "  AND\n" +
            "  m_info.RANK_RANK_PK IN :ranks\n" +
            "  AND\n" +
            "  m_la.MEMBER_LEAVE_ABSENCE_STATUS = 1\n",nativeQuery = true)
    List<Member> findTargetMember(@Param("roles") Set<Integer> roles,@Param("ranks") Set<Integer> ranks);
}
