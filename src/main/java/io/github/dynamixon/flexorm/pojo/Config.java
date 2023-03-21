package io.github.dynamixon.flexorm.pojo;

import io.github.dynamixon.flexorm.enums.LoggerLevel;

import java.util.Collections;
import java.util.List;

/**
 * @author maojianfeng
 * @date 2021/6/15
 */
public class Config {
    private boolean logStack;
    private List<String> logStackPackages;
    private LoggerLevel loggerLevel;

    private static final Config defaultConfig;

    static {
        defaultConfig = new Config();
        defaultConfig.setLogStack(false);
        defaultConfig.setLogStackPackages(Collections.emptyList());
        defaultConfig.setLoggerLevel(LoggerLevel.DEBUG);
    }

    public static Config defaultConfig(){
        return defaultConfig;
    }

    public boolean isLogStack() {
        return logStack;
    }

    public void setLogStack(boolean logStack) {
        this.logStack = logStack;
    }

    public List<String> getLogStackPackages() {
        return logStackPackages;
    }

    public void setLogStackPackages(List<String> logStackPackages) {
        this.logStackPackages = logStackPackages;
    }

    public LoggerLevel getLoggerLevel() {
        return loggerLevel;
    }

    public void setLoggerLevel(LoggerLevel loggerLevel) {
        this.loggerLevel = loggerLevel;
    }
}
