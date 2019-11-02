package com.itheima.ssm.controller;

import com.itheima.ssm.domain.SysLog;
import com.itheima.ssm.service.ISysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class LogAop {

    @Autowired
    private HttpServletRequest request;

    private Date visitDate; //开始时间
    private Class clazz;    //访问的类
    private Method method;  //访问的方法
    private String url;

    @Autowired
    private ISysLogService sysLogService;

    @Before("execution(* com.itheima.ssm.controller.*.*(..))")
    public void doBefore(JoinPoint jp) throws NoSuchMethodException {
        visitDate = new Date(); //获取时间
        clazz = jp.getTarget().getClass(); //获取类
        String methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();

        //获取method
        if (args == null || args.length == 0) {
            method = clazz.getMethod(methodName);
        } else {
            Class[] classArgs = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                classArgs[i] = args[i].getClass();
            }
            method = clazz.getMethod(methodName,classArgs);
        }

    }


    /*
    通过反射对注解进行操作
     */
    @After("execution(* com.itheima.ssm.controller.*.*(..))")
    public void doAfter(JoinPoint jp) throws Exception {

        //获取访问的时长
        long time = new Date().getTime() - visitDate.getTime();

        //获取url
        if (clazz!=null&&method!=null&&clazz!= LogAop.class) {
            RequestMapping classAnnotation = (RequestMapping) clazz.getAnnotation(RequestMapping.class);

            if (classAnnotation!=null) {
                String[] classValues = classAnnotation.value();  //类上面的注解的value值
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                if (methodAnnotation!=null) {
                    String[] methodValues = methodAnnotation.value();
                    url = classValues[0] + methodValues[0];
                }
            }
        }

        //获取访问的IP
        String ip = request.getRemoteAddr();

        //获取操作者
        /*
            登陆都是受Spring-security控制的,所以可以通过它要
         */
        SecurityContext context = SecurityContextHolder.getContext();//从上下文中获取当前登陆的对象
        User user = (User) context.getAuthentication().getPrincipal();
        String username = user.getUsername();

        //封装
        SysLog sysLog = new SysLog();
        sysLog.setExecutionTime(time);
        sysLog.setUrl(url);
        sysLog.setUsername(username);
        sysLog.setIp(ip);
        sysLog.setMethod("[类名] " + clazz.getName() + "[方法名] " + method.getName());
        sysLog.setVisitTime(visitDate);

        //数据库交互
        sysLogService.save(sysLog);
    }
}
