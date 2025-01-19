# XiaolianShowerSense

[![totallines](https://tokei.rs/b1/github/aquamarine5/XiaolianWebHelper)](https://github.com/XAMPPRocky/tokei)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Faquamarine5%2FXiaolianWebHelper.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Faquamarine5%2FXiaolianWebHelper?ref=badge_shield)
![2-XiaolianShowerSense-DesignedApplicationAndDeployed.png](https://s2.loli.net/2024/11/22/ca7gxQzTAFeR8bw.png)

> XiaolianShowerSense participated in **Designed an application** and successfully deployed it.

- 适用于任何使用智慧笑联的学校，[http://wash.aquamarine5.fun/](http://wash.aquamarine5.fun/)目前仅适用于河北大学，不过可以更改参数以适配所有宿舍。
> [!NOTE]
> 请勿使用HTTPS访问。

## Changelog

- **v0.8:** 针对MySQL的性能更新，关于从低版本的MySQL结构迁移到新版本的结构步骤，详见[SQL Structure Migration to v0.8](https://github.com/aquamarine5/XiaolianShowerSense/releases/tag/0.8)。
- **v0.7:** 加入了分析图标功能。
- **v0.6:** 加入了浴室地图功能。
- **v0.5:** 更新了UI设计。
- **v0.4:** 支持多浴室查询。

## For developers

### Tech stack

- 前端：Vue.js
- 后端：Springboot with MySQL, JDK21

### Build and run

### MySQL

```mysql
CREATE DATABASE xiaolian;

CREATE TABLE xiaolian.residenceIndex (
    residenceId INT PRIMARY KEY,
    floorId INT NOT NULL,
    buildingId INT NOT NULL,
    name TEXT NOT NULL,
    mapdata JSON,
    contributors TEXT,
    analyseStartTime MEDIUMINT UNSIGNED,
    analyseEndTime MEDIUMINT UNSIGNED
) CHARACTER SET utf8mb4;

CREATE TABLE xiaolian.showers (
    deviceId INT PRIMARY KEY,
    location VARCHAR(50) NOT NULL,
    displayNo TINYINT(3) NOT NULL,
    status TINYINT(2) NOT NULL,
    lastUsedTime TIMESTAMP NOT NULL,
    lastWashTime TIMESTAMP NOT NULL,
    residenceId INT NOT NULL
) CHARACTER SET utf8mb4;

CREATE TABLE xiaolian.analyses (
    minv SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    avgv SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    maxv SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    timePos SMALLINT UNSIGNED NOT NULL,
    count SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    residenceId INT UNSIGNED NOT NULL,
    PRIMARY KEY (residenceId, timePos)
) CHARACTER SET utf8mb4;

CREATE TABLE xiaolian.config (
    accessToken TEXT NOT NULL,
    refreshToken TEXT NOT NULL,
    avgWashCount MEDIUMINT UNSIGNED DEFAULT 0,
    avgWashTime MEDIUMINT UNSIGNED DEFAULT 0,
    requestTimes MEDIUMINT UNSIGNED DEFAULT 0,
    isAnalysisEnabled BOOLEAN DEFAULT TRUE NOT NULL
) CHARACTER SET utf8mb4;
```

#### 启动后端 (for server)

```bash
gradle build
java -jar build/libs/XiaolianShowerSense-${version}.jar 
```

- 环境变量`MYSQL_PASSWORD`是必须的。
- Springboot 将会监听`localhost:3017`, 请确保MySQL服务正在运行并有一个名为`xiaolian`的数据库，在`application.properties`更改更多数据库配置。

#### 启动前端

```bash
npm run dev
```

- Vue 将会把页面呈现在`localhost:5173`上。

### HTTP API

- `/wash?id=${residenceId}`  
返回浴室的所有数据，包括淋浴头名称。
- `/refresh?id=${residenceId}`  
返回浴室的关键数据，不包括淋浴头名称。
- `/list`  
返回可供查询的浴室列表。
- `/map?id=${residenceId}`  
返回浴室的地图数据（如果存在）。
- `/analysis?residenceId=${residenceId}`  
返回浴室的分析数据（若地图数据不存在，则分析数据无效，将返回'x'）。
- `/analyser_enabled` & `/analyser_disabled`  
分别启用和禁用分析功能。


#### Only for test

- `/force_analyse`  
将`ResidenceController.shouldUpdateAnalysis`强制设置为`true`，从而在下一次数据更新中统计分析数据。
- `/force_update`  
强制执行`ResidenceController.updateAllResidences()`，从而更新所有浴室的数据。

## Features

![2-XiaolianShowerSense-DesignedApplicationAndDeployed.p2.png](https://s2.loli.net/2024/11/22/FUmnPDz4eY7qfGB.png)
![2-XiaolianShowerSense-DesignedApplicationAndDeployed.p3.png](https://s2.loli.net/2024/11/22/BUvASNGf4kFPbYE.png)
![2-XiaolianShowerSense-DesignedApplicationAndDeployed.p4.png](https://s2.loli.net/2024/11/22/A49tpWI8olP75iK.png)
![2-XiaolianShowerSense-DesignedApplicationAndDeployed.p6.png](https://s2.loli.net/2024/11/22/ZyN9mJAjPUtCV5b.png)  

![Alt](https://repobeats.axiom.co/api/embed/f0821a2b9a53baa242030873157e39fd678e61c0.svg "Repobeats analytics image")

## License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Faquamarine5%2FXiaolianWebHelper.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Faquamarine5%2FXiaolianWebHelper?ref=badge_large)
