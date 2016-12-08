package cn.jony.compress.png

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.util.regex.Pattern

class PngCompressTask extends DefaultTask {
    static final TAG = PngCompressPlugin.TAG

    @Input
    PngCompressExtension mExtension

    HashMap<String, Pattern> mFilters = [:]

    PrintWriter mWriter

    long minSize
    int succeed = 0;
    int skipped = 0;
    int failed = 0;

    PngCompressTask() {
        setDescription('compress png tool use pngquant')
    }

    @TaskAction
    void compressAll() {
        logger.info(TAG, "====================compress png BEGIN====================")
        initParams()
        Set<String> traversed = []
        try {
            getTargetFiles().each { path ->
                compress(path, traversed)
            }
        } finally {
            log("Complete! succeed [${succeed}], failed [${failed}], skipped [${skipped}]")
            logger.info(TAG, "succeed [${succeed}], failed [${failed}], skipped [${skipped}]")

            closeLog()
        }


        logger.info(TAG, "====================compress png END====================")
    }

    void initParams() {
        PngquantUtil.copyPngquant2BuildFolder(project)
        minSize = mExtension.minSize * 1024
        getBlackFilters()
        initLogFile()
    }

    private void initLogFile() {
        File out = new File("${PngCompressExtension.logDir}/pngquant_${project.name}_${new Date().format('yyyyMMddHHmmss')}.log")
        if (!out.exists()) {
            out.parentFile.mkdirs() && out.createNewFile()
        }
        mWriter = new PrintWriter(out)
    }

    void compress(File path, Set<String> traversed) {
        if (path.exists()) {
            try {
                if (traversed.contains(path.absolutePath))
                    return;

                traversed << path.absolutePath

                if (path.isDirectory() && !shouldFiltered(path)) {
                    for (File file : path.listFiles()) {
                        compress(file, traversed)
                    }
                } else {
                    compressFile(path)
                }
            } catch (ex) {
                logger.info(TAG, "compress ${path.canonicalPath} fail [\n${ex.toString()}\n]")
            }
        }
    }

    void compressFile(File path) {
        if (!path.isDirectory()) {
            if (shouldFiltered(path)) {
                skipped++
                log("filtered! ${path.absolutePath}")
                return
            }

            long originalSize = path.length()
            def pngquant = PngquantUtil.getOutPngquantFilePath(project)
            Process process = new ProcessBuilder(pngquant, "-v", "--force", "--skip-if-larger",
                    "--speed=${mExtension.speed}", "--ext=${mExtension.outSuffix}", "--quality=5-95", path.absolutePath).start();

            int exitCode = process.waitFor()

            if (exitCode == 0) {
                succeed++
                String output = path.absolutePath.substring(0, path.absolutePath.lastIndexOf(".")).concat(mExtension.outSuffix)
                long optimizedSize = new File(output).length()
                float rate = 1.0f * (originalSize - optimizedSize) / originalSize * 100
                log("Succeed! [${path.absolutePath}] [${originalSize}]-->[${optimizedSize}], ${rate}% saved!")
            } else if (exitCode == 98) {
                skipped++
                log("Skipped! ${path.absolutePath}")
                logger.error(TAG, "Skipped! ${path.absolutePath}")
            } else {
                failed++

                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))
                StringBuilder error = new StringBuilder()
                String line
                while (null != (line = br.readLine())) {
                    error.append(line)
                }

                log("Failed! ${path.absolutePath}")
                logger.error(TAG, "Failed! ${path.absolutePath}")
                logger.error(TAG, "Exit: ${exitCode}. " + error.toString())
            }
        }
    }

    void getBlackFilters() {
        mExtension.blackLists.each { String str ->
            Pattern pattern = Pattern.compile(str)
            mFilters << ["${str}": pattern]
        }
    }

    List<File> getTargetFiles() {
        List<File> targetDirs = []
        Set<String> pathSet = []

        addToTarget(targetDirs, pathSet, mExtension.targets)
        addToTarget(targetDirs, pathSet, mExtension.extPaths)

        return targetDirs
    }

    static void addToTarget(List<File> targetDir, Set<String> pathSet, List<String> list) {
        if (!list.empty) {
            list.each { String str ->
                File file = new File(str)
                if (!pathSet.contains(file.absolutePath) && file.exists()) {
                    pathSet << file.absolutePath
                    targetDir << file
                }
            }
        }
    }

    boolean shouldFiltered(File file) {
        if (!mFilters.empty) {
            for (Map.Entry<String, Pattern> entry : mFilters.entrySet()) {
                if (entry.getValue().matcher(file.absolutePath).find()) {
                    log("filtered! [${file.absolutePath} filtered by pattern [${entry.getKey()}]]")
                    return true
                }
            }
        }

        return !file.isDirectory() && (!file.name.endsWith(".png") || file.size() <= minSize)
    }

    void log(String str) {
        if (mWriter != null) {
            mWriter.println(str)
        }
    }

    void closeLog() {
        try {
            if (mWriter != null) {
                mWriter.close()
            }
        } finally {
            mWriter = null
        }
    }
}
