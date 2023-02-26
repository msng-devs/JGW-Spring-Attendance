package com.jaramgroupware.attendance.web.aop.authorization;

import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerErrorCode;
import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@Order(1)
@Slf4j
@Component
public class AuthorizationAdvisor {

    @Before("@annotation(RBAC)")
    public void processCustomAnnotation(JoinPoint joinPoint,RBAC RBAC) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        var userUid = request.getHeader("user_pk");
        var roleId = (request.getHeader("role_pk") != null)? Integer.parseInt(request.getHeader("role_pk")) : null;

        if(userUid == null || roleId == null||RBAC.role() > roleId) throw new ControllerException(ControllerErrorCode.FORBIDDEN_ROLE);
    }

    @Before("@annotation(Auth)")
    public void processCustomAnnotation(JoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        var userUid = request.getHeader("user_pk");
        if(userUid == null) throw new ControllerException(ControllerErrorCode.FORBIDDEN_ROLE);
    }
}
