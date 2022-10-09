package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.rank.Rank;
import com.jaramgroupware.attendance.domain.rank.RankRepository;
import com.jaramgroupware.attendance.dto.rank.serviceDto.RankResponseServiceDto;
import com.jaramgroupware.attendance.utils.exception.CustomException;
import com.jaramgroupware.attendance.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RankService {

    private final RankRepository rankRepository;

    @Transactional(readOnly = true)
    public RankResponseServiceDto findById(Integer id){
        Rank targetRank = rankRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RANK_ID));
        return new RankResponseServiceDto(targetRank);
    }

    @Transactional(readOnly = true)
    public List<RankResponseServiceDto> findAll(){
        return rankRepository.findAllBy()
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_RANK))
                .stream()
                .map(RankResponseServiceDto::new)
                .collect(Collectors.toList());
    }

}
