package hello.hellospring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeTraceAop {
    // springconfig에 bean으로 등록해서 쓸경우 순환이 발생하므로 그때는 아래와 같이 사용
    //@Around("execution(* hello.hellospring..*(..)) && !target(hello.hellospring.SpringConfig)")
    @Around("execution(* hello.hellospring..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{
        Long start = System.currentTimeMillis();
        System.out.println("START ::: "+joinPoint.toString());

        try {

            return joinPoint.proceed();

        }finally{

            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END ::: "+joinPoint.toString() + " "+timeMs + "ms");

        }
    }
}
