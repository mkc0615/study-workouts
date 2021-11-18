package com.jpabook.jpashop.service;

import com.jpabook.jpashop.Repository.MemberRepository;
import com.jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 읽기기능에서 사용하면 성능 향상! 안에 있는 Transactional가 우선적으로 기능함
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // @Autowired 요즘 스프링버전에서는 생성자가 하나면 자동으로 오토인젝션을 함
//    public MemberService(MemberRepository memberRepository){
//        this.memberRepository = memberRepository;
//    } -> @RequiredArgsContructor로 자동 생성

    // 회원가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member){
        //exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다!");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

}
