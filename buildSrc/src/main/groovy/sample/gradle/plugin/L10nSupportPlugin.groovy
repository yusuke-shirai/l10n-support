package sample.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class L10nSupportPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('l10n', L10nSupportPluginExtension, project)

        def requestDirTask = project.tasks.create('l10n', L10nTask).configure {
            sourceLang = extension.sourceLang
            targetLang = extension.targetLang
            baseDir = extension.baseDir
            sourceFile = extension.sourceFile
            workDir = extension.workDir
        }
    }
}
