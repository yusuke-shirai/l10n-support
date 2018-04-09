package sample.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class L10nSupportPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('l10n', L10nSupportPluginExtension, project)

        project.tasks.create('l10n', L10nTask).configure {
            sourceLang = extension.sourceLang
            targetLangs = extension.targetLangs
            baseDir = extension.baseDir
            sourceFiles = extension.sourceFiles
            workDir = extension.workDir
            prohibitedWordFile = extension.prohibitedWordFile
        }
    }
}
