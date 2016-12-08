package cn.jony.compress.png
// Android Packer Plugin Extension
class PngCompressExtension {
    /**
     * 目标文件夹
     */
    List<String> targets

    /**
     * 额外文件夹或文件
     */
    List<String> extPaths

    /**
     * 白名单
     */
    List<String> blackLists

    /**
     * 最小大小
     */
    int minSize

    /**
     * 速度，1-10
     */
    int speed

    /**
     * 压缩文件的后缀名
     */
    String outSuffix

    /**
     * log 文件夹名称
     */
    static final String logDir = "pngLog"

    PngCompressExtension() {
        targets = []
        blackLists = []
        extPaths = []
        minSize = 5
        speed = 3
        outSuffix = '.png'
    }


}
