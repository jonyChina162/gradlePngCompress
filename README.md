# gradlePngCompress
gradle plugin to compress android png by using pngquant 

refer to https://github.com/imagemin/pngquant-bin and https://github.com/pornel/pngquant

该插件包可以自动对drawable和mipmap资源包的png进行遍历压缩，并可以添加额外路径或者自定义路径进行遍历压缩，还提供文件大小的过滤功能、黑名单过滤功能以及日志功能；并且完全不影响项目构建过程。
集成和压缩步骤如下：
1、总工程的build配置文件的dependencies加入依赖 classpath 'cn.jony.compress.png:pngcompress:1.0.3'
dependencies {
        classpath 'com.android.tools.build:gradle:2.2.2'
        classpath 'cn.jony.compress.png:pngcompress:1.0.3'
    }
在想要执行压缩的模块（只能是android application模块和android library模块）的build.gradle应用插件apply plugin: 'cn.jony.compress.png'  ，并配置pngCompress属性，如果想采用默认配置的话，直接应用插件不配置pngCompress属性也可以，具体如下所示：
apply plugin: 'cn.jony.compress.png'
 
pngCompress {
    // 文件最小大小，KB
    minSize 3
    // 黑名单
    blackLists = []
    // 需要压缩的文件夹，默认为空即遍历res下所有drawable和mipmap，所有的路径都是从执行task的路径计算
    targets[]
    // 除了默认资源，需要额外压缩的文件夹，所有的路径都是从执行task的路径计算
    extPaths = ['app/src/main/assets']
    // 压缩速度
    speed = 3
    // 压缩文件的后缀名，默认为.png，即直接覆盖原文件
    outSuffix = '.png'
}
 
 
配置完成之后只需要在as的Terminal执行gradle pngCompress，等待任务执行完毕，去主目录下会有pngLog目录，包括本次执行的简单结果日志：
日志包括三种标签（succeed，failed，filtered）其中filter的原因如下：1、文件名不以.png结尾；2、匹配到黑名单里；3、pngquant判断不需要压缩
