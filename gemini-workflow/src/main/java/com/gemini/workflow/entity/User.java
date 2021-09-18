package com.gemini.workflow.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User implements UserDetails {
    private Long id;
    private String userId;
    private String passwd;
    private String staffId;
    private String staffName;
    private boolean enableFlg;
    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public boolean isEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", passwd='" + passwd + '\'' +
                ", staffId='" + staffId + '\'' +
                ", staffName='" + staffName + '\'' +
                ", enableFlg=:" + enableFlg +
                ", roles=" + roles +
                '}';
    }
    /**
     * 实现SpringSecurity UserDetails的方法
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            //按ctms的角色添加group和role到Security
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleId()));
            authorities.add(new SimpleGrantedAuthority("GROUP_"+role.getRoleId()));
        }
        //给用户添加activiti角色
        if(roles.contains("admin")){
            authorities.add(new SimpleGrantedAuthority("ROLE_ACTIVITI_ADMIN"));
        }else{
            authorities.add(new SimpleGrantedAuthority("ROLE_ACTIVITI_USER"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.passwd;//密码
    }

    @Override
    public String getUsername() {
        return this.userId;//账号
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enableFlg;
    }
}
