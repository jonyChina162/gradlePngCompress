package cn.jony.compress.png

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project

class PngCompressPlugin implements Plugin<Project> {
    static final String TAG = "CompressPng"
    static final String PLUGIN_NAME = "pngCompress"
    static final String TASK_NAME = "pngCompress"

    PngCompressExtension extension

    @Override
    void apply(Project project) {
        this.extension = project.extensions.create(PLUGIN_NAME, PngCompressExtension)

        if (project.plugins.hasPlugin(AppPlugin)) {
            applyAndroid(project, (DomainObjectCollection<BaseVariant>) project.android.applicationVariants)
        } else if (project.plugins.hasPlugin(LibraryPlugin)) {
            applyAndroid(project, (DomainObjectCollection<BaseVariant>) project.android.libraryVariants)
        } else {
            throw new IllegalArgumentException('pngCompress gradle plugin only works in with Android module.')
        }
    }

    void applyAndroid(Project project, DomainObjectCollection<BaseVariant> variants) {
        List<String> targets = this.extension.targets
        Set<String> pathSet = []
        if (targets.empty) {
            variants.all { BaseVariant variant ->
                addDefaultDir(variant, pathSet, targets)
            }
            this.extension.targets = targets
        }

        project.task(type: PngCompressTask, overwrite: true, TASK_NAME) {
            mExtension = extension
        }

        project.task("clearPngLog", type: CleanLogTask) {}
    }

    static void addDefaultDir(BaseVariant variant, Set<String> pathSet, List<String> targetDirs) {
        variant.sourceSets.each { sourceSet ->
            sourceSet.resDirectories.each { res ->
                if (res.exists()) {
                    res.eachDir { File it ->
                        if (!pathSet.contains(it.absolutePath) && it.directory
                                && (it.name.startsWith("drawable") || it.name.startsWith("mipmap") || it.name.startsWith("assets"))) {
                            pathSet << it.absolutePath
                            targetDirs << it.absolutePath
                        }
                    }
                }
            }
        }
    }

}
