package com.example.demo.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    @Before("execution( * com.example.demo.controllers.*.getData(..))")
    public void beforeGetData(JoinPoint joinPoint) {
        System.out.println("before fetching data : "+joinPoint.getSignature());
    }

    @After("execution( * com.example.demo.controllers.*.getData(..))")
    public void AfterGetData(JoinPoint joinPoint) {
        System.out.println("after fetching data : "+joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "execution( * com.example.demo.controllers.*.getData(..))",returning = "result") 
        public void afterGetDataReturning(JoinPoint joinPoint, Object result) {
            System.out.println("after fetching data : "+joinPoint.getSignature()+" , result="+result);

        }

        @After("within(com.example.demo.controllers.*)")
        public void afterControllersInGeneral(JoinPoint joinPoint) {
            System.out.println("After controller method : "+joinPoint.getSignature());
    }

    @Around("within(com.example.demo.controllers.*)")
    public Object timeTracker(ProceedingJoinPoint joinPoint) throws Throwable{
       long startTime = System.currentTimeMillis();
       Object result=joinPoint.proceed();
       long endTime = System.currentTimeMillis();
       long timeTakeninMs= endTime-startTime;
       System.out.println("time taken by " +joinPoint.getSignature()+ " is  "+timeTakeninMs+ " ms");
    return result;

    }
    
}