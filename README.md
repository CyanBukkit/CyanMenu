# CyanMenu - 跨版本菜单插件

## 1. 项目信息
- **名称**: CyanMenu
- **功能**: 全版本兼容的Minecraft服务器菜单插件，支持网页菜单与本地配置
- **适用版本**: 全版本Bukkit/Spigot/Paper服务端
- **特性**: 
  - 支持YAML/MySQL配置存储
  - 可视化菜单编辑
  - 多类型指令执行
  - 权限节点控制

## 2. 使用教程

### 安装方法
1. 下载插件jar文件
2. 放入服务器plugins文件夹
3. 重启/重载服务器

### 指令列表
```
/cmenu reload         # 重载配置文件
/cmenu open <菜单名>  # 打开指定菜单
/cmenu list           # 列出所有菜单
/cmenu create <名称> <大小>  # 创建新菜单(大小需为9的倍数)
/cmenu edit <菜单名>   # 进入编辑模式
/cmenu delete <菜单名>  # 删除菜单
```

### 配置文件示例
```yaml
# 基础配置
Title: '&c&l主菜单'
Size: 54  # 菜单槽位数(必须是9的倍数)

Item:
  '0':
    ItemStackCompiler: |
      item:
        ==: org.bukkit.inventory.ItemStack
        type: STAINED_GLASS_PANE
        damage: 14
        meta:
          ==: ItemMeta
          meta-type: UNSPECIFIC
          display-name: '&aCyanBukkit'
    PermissionShow: ''
    RightClick: []
    LeftClick: []
    MiddleClick: []
    ShiftRightClick: []
    ShiftLeftClick: []
```


默认菜单文件格式

```yml
    RightClick:  # 左键点击事件
      - "player: cyanjoiner gui version"
    LeftClick:  # 右键点击事件
      - "player: cyanjoiner gui version"
    MiddleClick: # 中键点击事件
      - "player: cyanjoiner gui version"
    ShiftRightClick:  # 左键shift点击事件
      - "player: cyanjoiner gui version"
    ShiftLeftClick:  # 右键shift点击事件
      - "player: cyanjoiner gui version"
```


指令

```string
op: say %player%  # 让玩家以op的格式跑（一瞬间关服会导致玩家永久op）
console: say %player%  # 让玩家以控制台的格式跑
player: say %player%  # 让玩家以玩家的格式跑
servercmd: say %player%  # 让服务器的格式跑
tellmsg: say %player%  # 让玩家以私聊的格式跑
bungee: lobby # 让玩家传送到lobby服务器
cyanbukkit: xxxx  # 啦啦啦啦啦啦啦啦从前有个小玄易他创建了一个桶.........
demand: "%player_health% >= 5 ? op: 'say 我没病' ∴ op: 'say 我有病' "
```

## 3. 编译方法
1. 安装JDK17+
2. 执行构建命令：
```bash
./gradlew clean build
```
3. 构建产物位于 `build/libs/CyanMenu-{version}.jar`

## 4. 项目结构
```
src/main/kotlin/
├── cn.cyanbukkit.cyanmenu
│   ├── command/        # 指令处理
│   ├── core/           # 核心逻辑
│   ├── data/           # 数据存储
│   ├── event/          # 事件监听
│   └── utils/          # 工具类

gradle/                 # Gradle配置
build.gradle.kts        # 项目依赖配置
```

## 5. 其他信息
- **依赖项**:
  - CyanPluginLib (内置)
  - VaultAPI (可选)
- **权限节点**:
  - cyanmenu.admin - 管理指令权限
  - cyanmenu.use.<菜单名> - 菜单使用权限

## 6. 更新日志
### v1.0.0 (2024-02-01)
- 实现基础菜单功能
- 支持YAML配置文件
- 添加权限控制系统

