# StaticsSDK

#### 作者

by Lujie

#### 介绍

自动化脚本统计SDK，用于收集android端用户行为、界面展示退出、app使用时长等。

#### 软件架构

1. sdk插件（trackplugin）：一款自定义gradle插件，采用groovy脚本语言+ASM9.0（字节码操作技术），实现编译期动态插入代码，实现插桩。
2. sdk资源库 (tracksource)：上述sdk插件的对外基础资源库，采用java语言，实现sdk的初始化、自定义配置、数据上报

#### 使用教程

1.  在项目根目录build.gradle文件中添加以下配置

    repositories {
            
             maven {
                 url 'https://jitpack.io'
             }
            
    }
    
    dependencies {
        
        //引入sdk插件
        classpath 'com.github.lanshaoze.staticsSDK:trackplugin:{version}'
        
    }
    
    allprojects {
    
        repositories {
            
            maven {
                 url 'https://jitpack.io'
            }
            
        }
    }

2.  在主app目录build.gradle文件下添加以下配置：

    //声明sdk插件
    
    apply plugin: 'autotrack'
    
    //插件基础配置
    
    noTracePoint {
    
        // 是否打印日志,可选,默认false
        
        isDebug = true
        
        // 是否打开SDK的日志全埋点采集,可选,默认true
        
        isOpenLogTrack = true
        
        //是否输出插桩后的文件至build文件夹
        
        isOutputModifyFile = true
        
        // 因为默认系统包[android.support, androidx]是被过滤掉的,想对默认过滤掉的类插桩的话可以手动添加包名,但是不建议对系统类进行插桩,容易出现不可预期错误,可选,默认空
        
        include = ["android.support.v7.widget"]
        
        // [android.support, androidx]默认过滤，可手动追加过滤的包,可选,默认空不追加
        
        exclude = ["xxx", "xxx"]
    }
    
3.  在主app目录build.gradle文件下添加以下配置：
    
    dependencies {
    
        //引入sdk基础资源库
        
        implementation 'com.github.lanshaoze.staticsSDK:tracksource:{version}'
    }
    
4. 在app的application类onCreate()方法中添加sdk初始化方法。

   TrackSDK.getInstance()
   
                        .setBuilder(new TrackSDK.ConfigBuilder(getApplicationContext())) //sdk构造器
                        
                        .init({appid});  //appid
                        
                        
5. 需在项目中的BaseActivity（如果有,否则自己定义），需继承自HookActivityDelegate。 在BaseFragment中（如果有，否则自己定义），需继承自HookFragmentDelegate。
   用于进行Activity与Fragment相关事件捕获。
   
   在具体的activity或fragment重写getDes()方法，定义代表界面含义。
   如若不重写，则代表该界面不进行统计。



