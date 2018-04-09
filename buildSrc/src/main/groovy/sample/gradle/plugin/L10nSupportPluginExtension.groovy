package sample.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty

class L10nSupportPluginExtension {
    final Property<CharSequence> sourceLang
    final ListProperty<CharSequence> targetLangs
    final Property<CharSequence> baseDir
    final ListProperty<Object> sourceFiles
    final Property<CharSequence> workDir
    final Property<CharSequence> prohibitedWordFile

    L10nSupportPluginExtension(Project project) {
        sourceLang = project.objects.property(CharSequence)
        sourceLang.set("ja")

        targetLangs = project.objects.listProperty(CharSequence)
        targetLangs.add("en")//gradle 4.5

        baseDir = project.objects.property(CharSequence)
        baseDir.set("src/main/resources")

        sourceFiles = project.objects.listProperty(Object)
        sourceFiles.add(ResourceDefinition.from('**/*_ja.properties'))//gradle 4.5
        sourceFiles.add(ResourceDefinition.from('**/*_ja.json'))//gradle 4.5

        workDir = project.objects.property(CharSequence)
        workDir.set("${project.rootProject.buildDir}/l10n")

        prohibitedWordFile = project.objects.property(CharSequence)
    }
}
