# XiaolianShowerSense

[![totallines](https://tokei.rs/b1/github/aquamarine5/XiaolianWebHelper)](https://github.com/XAMPPRocky/tokei)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Faquamarine5%2FXiaolianWebHelper.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Faquamarine5%2FXiaolianWebHelper?ref=badge_shield)
[![2组-XiaolianShowerSense-DesignedApplicationAndDeployed.png](https://img.picui.cn/free/2024/11/10/6730af7803b44.png)](https://img.picui.cn/free/2024/11/10/6730af7803b44.png)

> XiaolianShowerSense participated in **The project of designed an application** also successfully deployed it.

- 适用于任何使用智慧笑联的学校，[http://wash.aquamarine5.fun/](http://wash.aquamarine5.fun/)目前仅适用于河北大学，不过可以更改参数以适配所有宿舍。

## Changelog

- **v0.7:** 加入了分析图标功能。
- **v0.6:** 加入了浴室地图功能。
- **v0.5:** 更新了UI设计。
- **v0.4:** 支持多浴室查询。

## For developers

### Tech stack

- 前端：Vue.js
- 后端：Springboot with MySQL, JDK21

### Build and run

#### 启动后端

```bash
gradle build
java -jar build/libs/XiaolianShowerSense-${version}.jar
```

- Springboot 将会监听`localhost:3017`, 请确保MySQL服务正在运行并有一个名为`xiaolian`的数据库，在`application.properties`更改数据库配置。

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

#### Only for test

- `/force_analyse`  
将`ResidenceController.shouldUpdateAnalysis`强制设置为`true`，从而在下一次数据更新中统计分析数据。
- `/force_update`  
强制执行`ResidenceController.updateAllResidences()`，从而更新所有浴室的数据。

## Features

[![2组-XiaolianShowerSense-DesignedApplicationAndDeployed.p2.png](https://img.picui.cn/free/2024/11/10/6730af780d9b5.png)](https://img.picui.cn/free/2024/11/10/6730af780d9b5.png)
[![2组-XiaolianShowerSense-DesignedApplicationAndDeployed.p3.png](https://img.picui.cn/free/2024/11/10/6730af77ec568.png)](https://img.picui.cn/free/2024/11/10/6730af77ec568.png)
[![2组-XiaolianShowerSense-DesignedApplicationAndDeployed.p4.png](https://img.picui.cn/free/2024/11/10/6730af773b262.png)](https://img.picui.cn/free/2024/11/10/6730af773b262.png)
[![2组-XiaolianShowerSense-DesignedApplicationAndDeployed.p6.png](https://img.picui.cn/free/2024/11/10/6730af77c8cab.png)](https://img.picui.cn/free/2024/11/10/6730af77c8cab.png)
![Alt](https://repobeats.axiom.co/api/embed/f0821a2b9a53baa242030873157e39fd678e61c0.svg "Repobeats analytics image")

## License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Faquamarine5%2FXiaolianWebHelper.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Faquamarine5%2FXiaolianWebHelper?ref=badge_large)
