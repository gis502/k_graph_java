package com.ruoyi.web.listener;

import com.ruoyi.web.controller.system.DatabaseBackup;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class StartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private DatabaseBackup DatabaseBackup;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // 系统启动后运行任务
//        DatabaseBackup.backupAndDownload();
    }
}
