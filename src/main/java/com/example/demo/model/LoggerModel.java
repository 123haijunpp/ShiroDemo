package com.example.demo.model;

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
    private Integer id;

    private String clientIp;

    private String uri;

    private String type;

    private String method;

    private String sessionId;

    private Date time;

    private String returnTime;

    private String httpStatusCode;

    private Integer timeConsuming;

    private String paramData;

    private String returnData;
}
