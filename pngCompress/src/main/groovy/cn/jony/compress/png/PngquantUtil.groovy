package cn.jony.compress.png

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project;

/**
 * @Author: chenenyu
 * @Created: 16/6/27 17:44.
 */
class PngquantUtil {

    private static final def name = "pngquant";

    static def copyPngquant2BuildFolder(Project project) {
        def pngquantDir = getPngquantDirectory(project)
        if (!pngquantDir.exists()) {
            pngquantDir.mkdirs()
        }
        def pngFile = new File(getOutPngquantFilePath(project))
        if (!pngFile.exists()) {
            new FileOutputStream(pngFile).withStream {
                def inputStream = PngquantUtil.class.getResourceAsStream("/$name/${getInputDirectory()}/${getFilename()}")
                it.write(inputStream.getBytes())
            }
        }
        pngFile.setExecutable(true, false)
    }

    /**
     * .../build/pngquant
     * @return String
     */
    private static def getPngquantDirectoryPath(Project project) {
        return project.buildDir.absolutePath + File.separator + "$name"
    }

    /**
     * .../build/pngquant
     * @return File (Directory)
     */
    private static def getPngquantDirectory(Project project) {
        return new File(getPngquantDirectoryPath(project))
    }

    /**
     * .../build/pngquant/{mac/win/linux/x64}/{pngquant/pngquant.exe}.
     *
     * @return String
     */
    static def getOutPngquantFilePath(Project project) {
        return getPngquantDirectoryPath(project) + File.separator + getFilename()
    }

    static def getInputDirectory() {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "win"
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            return "mac"
        } else {
            return "linux/x64"
        }
    }

    static def getFilename() {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${name}.exe"
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            return "${name}"
        } else {
            return "$name"
        }
    }

}