package com.dh.framework.aop.config;

import lombok.Data;

@Data
public class DhAopConfig {
    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspectAfterReturn;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
