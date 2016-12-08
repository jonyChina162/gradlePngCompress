package cn.jony.compress.png

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CleanLogTask extends DefaultTask {

    CleanLogTask() {
        setDescription('clean all logs')
    }

    @TaskAction
    void deleteAll() {
        File target = new File(PngCompressExtension.logDir)
        if (target.exists()) {
            target.deleteDir()
        }
    }

}
