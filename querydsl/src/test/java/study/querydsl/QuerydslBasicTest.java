package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Member;
import study.querydsl.domain.QMember;
import study.querydsl.domain.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.domain.QMember.member;


@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member2", 30, teamB);
        Member member4 = new Member("member2", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL(){
        // find member1
        Member findSingleMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findSingleMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl(){
        // removed below by import static
        // QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch(){
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        Member fetchOne = queryFactory
                .selectFrom(QMember.member)
                .fetchOne();

        Member fetchf = queryFactory
                .selectFrom(QMember.member)
                // .limit(1).fetchOne();
                .fetchFirst();

        // depreciated
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
        results.getTotal();
        List<Member> content = results.getResults();

        // depreciated
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();

        // for future use below!
        Long totalCount = queryFactory
                // .select(Wildcard.count) // select count(*)
                .select(member.count()) // select count(member.id)
                .from(member)
                .fetchOne();
    }

    // search options
    public void otherSearches(){

        member.username.eq("member1"); // =
        member.username.ne("member1"); // !=
        member.username.eq("member1").not(); // !=

        member.username.isNotNull(); // username is not null

        member.age.in(10, 20); // age in (10, 20)
        member.age.notIn(10, 20); // age not in (10, 20)
        member.age.between(10, 20); // age between 10 , 20

        member.age.goe(30); // >=
        member.age.gt(30); // >
        member.age.loe(30); // <=
        member.age.lt(30); // <

        member.username.like("%member%"); // like search
        member.username.contains("member"); // like %member% search
        member.username.startsWith("member"); // like member% search
    }
}
