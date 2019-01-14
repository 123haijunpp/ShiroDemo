package com.example.demo.mapper;

import com.example.demo.model.LoggerModel;
import org.springframework.data.repository.CrudRepository;

/**
 * program: ShiroDemo
 *
 * @author Qiu
 * Description:
 * @date 2019/1/14/014 22:10
 */
public interface LoggerRepository extends CrudRepository<LoggerModel, Long> {

}
