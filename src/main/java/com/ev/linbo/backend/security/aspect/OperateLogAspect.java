package com.ev.linbo.backend.security.aspect;

import com.ev.linbo.backend.security.annotation.OperateLog;
import com.ev.linbo.backend.security.annotation.SelectPrimaryKey;
import com.ev.linbo.backend.security.annotation.SelectTable;
import com.ev.linbo.backend.security.model.OperateLogInfo;
import com.ev.linbo.security.util.JwtTokenUtil;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class OperateLogAspect {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Pointcut("@annotation(com.ev.linbo.backend.security.annotation.OperateLog)")
    private void operateLogPointCut(){
    }

    @Around("operateLogPointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object responseObj = null;
        OperateLogInfo operateLogInfo = new OperateLogInfo();
        String flag = "success";
        try{
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            String authHeader = request.getHeader(this.tokenHeader);
            if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
                String authToken = authHeader.substring(this.tokenHead.length());// The part after "Bearer "
                String username = jwtTokenUtil.getUserNameFromToken(authToken);
                operateLogInfo.setUserName(username);
            }
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            OperateLog declaredAnnotation = signature.getMethod().getDeclaredAnnotation(OperateLog.class);
            operateLogInfo.setOperation(declaredAnnotation.operation());
            operateLogInfo.setModule(declaredAnnotation.module());
            operateLogInfo.setOperateType(declaredAnnotation.operateType());
            //获取执行的方法
            String method = signature.getDeclaringType().getName() + "."  + signature.getName();
            operateLogInfo.setMethod(method);
            String operateType = declaredAnnotation.operateType();
            if (pjp.getArgs().length > 0){
                if ("OPERATE_MOD_USER".equals(operateType)) {
                    StringBuilder modifiedData = new StringBuilder();
                    if (pjp.getArgs().length > 0) {
                        for (Object arg : pjp.getArgs()) {
                            modifiedData.append(ObjectUtils.isEmpty(arg)? "," : arg + ",");
                        }
                        operateLogInfo.setModifiedData(String.valueOf(modifiedData));
                    }
                } else {
                    Object args = pjp.getArgs()[0];
                    operateLogInfo.setModifiedData(new Gson().toJson(args));
                }
            }
            if ("OPERATE_MOD".equals(operateType) || "OPERATE_DELETE".equals(operateType)) {
                String tableName = "";
                String idName = "";
                String selectPrimaryKey = "";
                if(pjp.getArgs().length>0){
                    Object args = pjp.getArgs()[0];
                    //获取操作前的数据
                    boolean selectTableFlag = args.getClass().isAnnotationPresent(SelectTable.class);
                    if(selectTableFlag){
                        tableName = args.getClass().getAnnotation(SelectTable.class).tableName();
                        idName = args.getClass().getAnnotation(SelectTable.class).idName();
                    }else {
                        throw new RuntimeException("操作日志类型为修改或删除，实体类必须指定表面和主键注解！");
                    }
                    Field[] fields = args.getClass().getDeclaredFields();
                    Field[] fieldsCopy = fields;
                    boolean isFindField = false;
                    int fieldLength = fields.length;
                    for(int i = 0; i < fieldLength; ++i) {
                        Field field = fieldsCopy[i];
                        boolean hasPrimaryField = field.isAnnotationPresent(SelectPrimaryKey.class);
                        if (hasPrimaryField) {
                            isFindField = true;
                            field.setAccessible(true);
                            selectPrimaryKey = field.get(args).toString();
                        }
                    }
                    if(!isFindField){
                        throw new RuntimeException("实体类必须指定主键属性！");
                    }
                }
                if(!StringUtils.isEmpty(tableName) &&
                        !StringUtils.isEmpty(idName)&&
                        !StringUtils.isEmpty(selectPrimaryKey)){
                    StringBuffer sb = new StringBuffer();
                    sb.append(" select * from  ");
                    sb.append(tableName);
                    sb.append(" where ");
                    sb.append(idName);
                    sb.append(" = ? ");
                    String sql = sb.toString();
                    try{
                        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, selectPrimaryKey);
                        if(maps!=null){
                            operateLogInfo.setPreModifiedData(new Gson().toJson(maps));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new RuntimeException("查询操作前数据出错！");
                    }
                }else {
                    throw new RuntimeException("表名、主键名或主键值 存在空值情况，请核实！");
                }
            } else {
                operateLogInfo.setPreModifiedData("");
            }
            //操作时间
            Date beforeDate = new Date();
            Long startTime = beforeDate.getTime();
            operateLogInfo.setExecuteTime(beforeDate);
            responseObj = pjp.proceed();
            Date afterDate = new Date();
            Long endTime = afterDate.getTime();
            Long duration = endTime - startTime;
            operateLogInfo.setDuration(duration);
            operateLogInfo.setIp(getIp(request));
            operateLogInfo.setResult(flag);
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }catch (Exception e){
            flag = "fail";
            operateLogInfo.setResult(flag);
            operateLogInfo.setErrorMessage(e.getMessage());
            operateLogInfo.setErrorStackTrace(e.getStackTrace().toString());
            e.printStackTrace();
        }finally {
            insertIntoLogTable(operateLogInfo);
        }
        return responseObj;
    }

    private void insertIntoLogTable(OperateLogInfo operateLogInfo){
        operateLogInfo.setId(UUID.randomUUID().toString().replace("-",""));
        String sql="insert into operate_log values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,operateLogInfo.getId(), operateLogInfo.getUserName(),
                operateLogInfo.getOperation(),operateLogInfo.getMethod(),
                operateLogInfo.getModifiedData(),operateLogInfo.getPreModifiedData(),
                operateLogInfo.getResult(),operateLogInfo.getErrorMessage(),operateLogInfo.getErrorStackTrace(),
                operateLogInfo.getExecuteTime(),operateLogInfo.getDuration(),operateLogInfo.getIp(),
                operateLogInfo.getModule(),operateLogInfo.getOperateType());
    }

    private String getIp(HttpServletRequest request){
        String ip = request.getHeader("X-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
