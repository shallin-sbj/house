package com.insu.house.aop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.insu.house.service.ILogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * @author sucl
 */
@Aspect
@Component
public class SystemAspect {

    private final Logger logger = LoggerFactory.getLogger(SystemAspect.class);

    protected final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy.MM.dd HH:mm:ss").create();

    @Autowired
    private ILogService logService;

    @Pointcut("@annotation(com.insu.house.aop.SystemControllerAnnotation)")
    public void controllerPoint() {
    }


    /**
     * Service层切点
     */
    @Pointcut("@annotation(com.insu.house.aop.SystemServiceLog)")
    public void serviceAspect() {
    }

    @Before("controllerPoint()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容
        logger.info("===================================================== " );
        logger.info("/n 请求URL : " + request.getRequestURL().toString());
//        logger.info("HTTP_METHOD : " + request.getMethod());
        logger.info("SystemControllerAnnotation :" + getServiceMthodDescription(joinPoint));
//        logger.info("IP : " + request.getRemoteAddr());
        logger.info("调用的方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        StringBuffer parameters = new StringBuffer();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                parameters.append(paramName);
                parameters.append("=");
                parameters.append(paramValues[0]);
                parameters.append(";");
            }
        }
        logger.info("请求参数 " + parameters.toString());
    }


    @AfterReturning(returning = "ret", pointcut = "controllerPoint()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("返回结果 : " + ret);
    }


    /**
     * 获取注解中对方法的描述信息 用于service层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getServiceMthodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SystemControllerAnnotation.class)
                            .description();
                    break;
                }
            }
        }
        return description;
    }

    @AfterThrowing(pointcut = "controllerPoint(),serviceAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        //获取请求ip
        String ip = request.getRemoteAddr();
        //获取用户请求方法的参数并序列化为JSON格式字符串
        String params = "";
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            for (int i = 0; i < joinPoint.getArgs().length; i++) {
                params += this.gson.toJson(joinPoint.getArgs()[i]) + ";";
            }
        }
        try {
            /* ========控制台输出========= */
            logger.info("=====异常通知开始=====");
            logger.info("异常代码:" + e.getClass().getName());
            logger.info("异常信息:" + e.getMessage());
            logger.info("异常方法:" + joinPoint.getTarget().getClass().getName()
                    + "." + joinPoint.getSignature().getName() + "()");
            logger.info("方法描述:" + getServiceMthodDescription(joinPoint));
            logger.info("请求IP:" + ip);
            logger.info("请求参数:" + params);
            logger.info("=====异常通知结束=====");
        } catch (Exception e1) {
        }
    }
}
