package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * program: ShiroDemo
 * Date: 2019/1/12/012 18:03
 *
 * @author: Mr.Qiu
 * Description:
 */
@Entity
@Data
public class SysPermission implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue
    private long id;
    /**
     * 名称
     */
    private String name;

    /**
     * 资源类型，[menu|button]
     */
    @Column(columnDefinition = "enum('menu','button')")
    private String resourceType;
    /**
     * 资源路径.
     */
    private String url;
    /**
     * 权限字符串,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view
     */
    private String permission;
    /**
     * 父编号
     */
    private Long parentId;
    /**
     * 父编号列表
     */
    private String parentIds;
    private Boolean available = Boolean.FALSE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SysRolePermission", joinColumns = {@JoinColumn(name = "permissionId")}, inverseJoinColumns = {@JoinColumn(name = "roleId")})
    private List<SysRole> roles;

}
