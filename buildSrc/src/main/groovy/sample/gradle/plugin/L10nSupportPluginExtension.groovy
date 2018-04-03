package sample.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty

class L10nSupportPluginExtension {
    final Property<CharSequence> sourceLang
    final ListProperty<CharSequence> targetLang
    final Property<CharSequence> baseDir
    final ListProperty<CharSequence> sourceFile
    final Property<CharSequence> workDir
    final Property<CharSequence> previousRequest

    L10nSupportPluginExtension(Project project) {
        sourceLang = project.objects.property(CharSequence)
        sourceLang.set("ja")

        targetLang = project.objects.listProperty(CharSequence)
        // targetLang.add("en")//gradle 4.5

        baseDir = project.objects.property(CharSequence)
        baseDir.set("src/main/resources")

        sourceFile = project.objects.listProperty(CharSequence)
        // sourceFile.get().add('**/*_ja.properties')//gradle 4.5
        // sourceFile.add('**/*_ja.json')//gradle 4.5

        workDir = project.objects.property(CharSequence)
        workDir.set("${project.rootProject.buildDir}/l10nRequest")

        previousRequest  = project.objects.property(CharSequence)
        previousRequest.set("")
    }
}
