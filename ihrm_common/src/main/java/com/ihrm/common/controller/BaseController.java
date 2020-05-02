package com.ihrm.common.controller;

import com.ihrm.domain.system.response.ProfileResult;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/21 15:07
 */
public class BaseController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Claims claims;
    protected String companyId;
    protected String companyName;
    private String userId;
    //使用jwt的方式获取
    /*@ModelAttribute
    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
        Object obj = request.getAttribute("user_claim");
        if(!StringUtils.isEmpty(obj)){
            this.claims=(Claims)obj;
            this.companyId=(String) claims.get("companyId");
            this.companyName=(String) claims.get("companyName");
        }
    }*/
    //企业id，（暂时使用1,以后会动态获取）
    //使用shiro的方式获取
    @ModelAttribute
    public void setReqAndResp(HttpServletRequest request,HttpServletResponse response){
        this.request=request;
        this.response=response;
        //获取session中的安全数据
        Subject subject = SecurityUtils.getSubject();
        //1.subject获取所有的安全数据集合
        PrincipalCollection principals = subject.getPrincipals();
        //2.获取安全数据
        if(principals!=null&&!principals.isEmpty()){
            ProfileResult result = (ProfileResult) principals.getPrimaryPrincipal();
            this.companyName = result.getCompany();
            this.companyId = result.getCompanyId();
            this.userId = result.getUserId();
        }
    }
    /*public String parseCompanyId() {
        return "1";
    }
    public String parseCompanyName() {
        return "贵州茅台集团股份有限公司";
    }*/
}
