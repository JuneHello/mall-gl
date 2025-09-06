package com.siro.gulimall.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DataSourceDebugConfig {

  @Autowired
  private DataSourceProperties dataSourceProperties;

  @PostConstruct
  public void printDataSourceInfo() {
    System.out.println("=================================================");
    System.out.println("==> DB URL      : " + dataSourceProperties.getUrl());
    System.out.println("==> Username    : " + dataSourceProperties.getUsername());
    System.out.println("==> DriverClass : " + dataSourceProperties.getDriverClassName());
    System.out.println("=================================================");
  }
}
