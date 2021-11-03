package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    private final MemberService memberService;
//  @Autowired private MemberService memberService; <- 이게 필드 주입의 예시

    // setter 주입의 예시 (보안상 안 좋음)
//  public void setMemberService(MemberSErvice memberService){
//      this.memberSerivce = memberService;
//  }

    // 스프링 컨테이너에서 있는 서비스에 의존성 주입(아래가 생성자 주입의 예시)
    // Annotation 에는 @component 가 포함되어 있음
    // 스프링 로딩시 component scan을 통해 불러오고 자동 의존관계 설정이 된다
    @Autowired
    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping("/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(MemberForm form){
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member);

        return "redirect:/";

    }

}
