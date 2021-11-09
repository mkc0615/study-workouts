package hello.hellospring;

import hello.hellospring.aop.TimeTraceAop;
import hello.hellospring.repository.*;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    // springData 사용시
    private final MemberRepository memberRepository;

    @Autowired
    public SpringConfig(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    // jdbc / jpa 사용시
//    private DataSource dataSource;
//    private EntityManager em;
//
//    @Autowired
//    public SpringConfig(DataSource dataSource, EntityManager em){
//        this.dataSource = dataSource;
//        this.em = em;
//    }

//  @Autowired
//  public SpringConfig(DataSource dataSource){
//      this.dataSource = dataSource;
//  }

    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository);
    }

//    @Bean
//    public TimeTraceAop timeTraceAop(){
//        return new TimeTraceAop();
//    }

    // 직접 컴포넌트를 추가할 경우는 하기와 같이
//    @Bean
//    public MemberRepository memberRepository(){
          // 순수 jdbc 사용시
//        return new JdbcMemberRepository();
//        return new MemoryMemberRepository();
//        return new JdbcTemplateMemberRepository(dataSource);
//        return new JpaMemberRepository(em);
//    }
}
