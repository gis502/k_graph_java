package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/backup")
public class DatabaseBackup {
    @Value("${backup.path}")
    private String BACKUP_PATH;
    @Value("${backup.db_port}")
    private int DB_PORT;  // 远程数据库的端口号
    @Value("${backup.db_name}")
    private String DB_NAME;
    @Value("${backup.db_user}")
    private String DB_USER;
    @Value("${backup.db_password}")
    private String DB_PASSWORD;
    @Value("${backup.pg_dump_path}")
    private String PG_DUMP_PATH;
    @Value("${backup.db_host}")
    private String DB_HOST;  // 远程数据库服务器的地址

    private String backupAllPath = "";

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> backupAndDownload(@RequestParam String tableName) {
        // 生成备份文件名
        String backupFileName = DB_NAME + "_backup_" + tableName + "_" + System.currentTimeMillis() + ".backup";
        String backupFilePath = BACKUP_PATH + backupFileName;

        // 构建pg_dump命令
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.environment().put("PGPASSWORD", DB_PASSWORD);
        processBuilder.command(PG_DUMP_PATH, "-h", DB_HOST, "-p", String.valueOf(DB_PORT), "-U", DB_USER, "-F", "c", "-b", "-v", "-E", "UTF8", "-f", backupFilePath, "-t", tableName, DB_NAME);

        try {
            // 执行命令
            Process process = processBuilder.start();
            // 捕获输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.out.println("pg_dump 命令执行失败，退出码：" + exitCode);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("pg_dump 命令执行时发生异常");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // 返回备份文件到前端
        File file = new File(backupFilePath);
        if (!file.exists()) {
            System.out.println("备份文件未生成");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + backupFileName)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @RequestMapping(value = "/downloadAll", method = RequestMethod.GET)
    public ResponseEntity<Resource> backupAndDownloadAll() {

        File file = new File(backupAllPath);
        if (!file.exists()) {
            System.out.println("备份文件未生成");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + backupAllPath)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

//    @Scheduled(cron = "0 0 0 * * ?")
    public void backupAndDownload() {
        String backupFileName = DB_NAME + "_backup_" + System.currentTimeMillis() + ".backup";
        String backupFilePath = BACKUP_PATH + backupFileName;
        ProcessBuilder processBuilder = new ProcessBuilder();

        // 设置环境变量（数据库用户名和密码）
        processBuilder.environment().put("PGPASSWORD", DB_PASSWORD);

        // 构建pg_dump命令以备份整个数据库
        // 构建pg_dump命令，排除sichuan_popdensity_point表
        processBuilder.command(PG_DUMP_PATH, "-h", DB_HOST, "-p", String.valueOf(DB_PORT), "-U", DB_USER,
                "-F", "c", "-b", "-v", "-E", "UTF8", "-f", backupFilePath,
                "--exclude-table=sichuan_popdensity_point", DB_NAME);


        try {
            // 执行命令
            Process process = processBuilder.start();

            // 捕获输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.out.println("pg_dump 命令执行失败，退出码：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("pg_dump 命令执行时发生异常");
            e.printStackTrace();
        } finally {
            File file = new File(backupFilePath);
            if (file.exists()) {
                backupAllPath = backupFilePath;
            }
        }
    }

}
