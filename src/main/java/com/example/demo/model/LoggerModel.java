package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description:
 * @date 2019/1/14/014 22:02
 */
@Data
@Entity
@Table(name = "t_logger")
@AllArgsConstructor
@NoArgsConstructor
public class LoggerModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String clientIp;

    private String uri;

    private String type;

    private String method;

    private String sessionId;

    private Integer time;

    private String returnTime;

    private String httpStatusCode;

    private Integer timeConsuming;

    private String paramData;

    private String returnData;

    private String username;


    /**
     * 注解上的描述
     */
    private String operation;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
}
