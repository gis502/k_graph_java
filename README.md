### 后端项目

> 问题1：postgreSql 16版本安装时可能会出现和操作系统冲突问题

解决方案：降低postgreSql版本

> 问题2：postgre安装时，在选择路径这步出现`ERROR The chosen installation directory exists and is non-empty. Please chcose a different directory ns t aII nstaIIBuiIder ctory`。

解决方案：重新打开下载器并在要安装路径下的文件夹里建立版本号名的文件夹，比如16，再进行选择16文件夹安装。如果再不行更换postgreSql版本。

>数据sql导入导出命令

```sql
--第一步
pg_dump -h yourhost -p 7654 -U postgres -W -F p -b -v -f ruoyi_backup.sql ruoyi
--第二步
createdb -h localhost -p 5432 -U postgres mylocaldb
--第三步
psql -h localhost -p 5432 -U postgres -d mylocaldb -f ruoyi_backup.sql
```
>导入模块如果出现导入失败之类的错误，请尝试在启动的配置上打开modify options里面的add vm option添加以上语句，看是否能解决。
```md
--add-opens java.base/java.util=ALL-UNNAMED（错误）
--add-opens java.base/java.lang=ALL-UNNAMED（应该使用这个）
```

>备份backup文件
```sql
--1
D:/App/PostgreSQL/16/bin/pg_dump.exe -h 47.92.216.173 -p 7654 -U postgres -F c -b -v -E UTF8 -f D:/App/PostgreSQL/backup/yaan.backup --exclude-table=sichuan_popdensity_point yaan
--2
D:/App/PostgreSQL/16/bin/pg_restore.exe -U postgres -d yaan -v "D:\yaan_backup_1728195811573.backup"
```

>后端进行部署时的命令
```shell
nohup java --add-opens java.base/java.lang=ALL-UNNAMED -jar ruoyi-admin.jar &
```