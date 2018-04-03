package sample.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip

class L10nSupportPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('l10n', L10nSupportPluginExtension, project)

        def requestDirTask = project.tasks.create('requestL10nDir', RequestL10n).configure {
            sourceLang = extension.sourceLang
            targetLang = extension.targetLang
            baseDir = extension.baseDir
            sourceFile = extension.sourceFile
            workDir = extension.workDir
            previousRequest = extension.previousRequest
        }
        def requestTask = project.rootProject.tasks.findByName('requestL10n')
        if (requestTask == null) {
            requestTask = project.tasks.create('requestL10n', Zip).configure {
                from extension.workDir
                destinationDir project.rootProject.distsDir
            }
        }
        requestTask.dependsOn(requestDirTask)
    }
}
