package sample.gradle.plugin

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty

class L10nSupportPluginExtension {
    final Property<CharSequence> sourceLang
    final ListProperty<CharSequence> targetLangs
    final Property<CharSequence> baseDir
    final ListProperty<ResourceDefinition> sources
    final Property<CharSequence> outputDir
    final ListProperty<CharSequence> prohibitedTerms

    L10nSupportPluginExtension(Project project) {
        sourceLang = project.objects.property(CharSequence)
        sourceLang.set("ja")

        targetLangs = project.objects.listProperty(CharSequence)
        targetLangs.add("en")//gradle 4.5

        baseDir = project.objects.property(CharSequence)
        baseDir.set("src/main/resources")

        sources = project.objects.listProperty(ResourceDefinition)

        outputDir = project.objects.property(CharSequence)
        outputDir.set("${project.rootProject.buildDir}/l10n")

        prohibitedTerms = project.objects.listProperty(CharSequence)
    }

    void sourceLang(String lang) {
        this.sourceLang.set(lang)
    }

    void targetLang(String... langs) {
        this.targetLangs.set(langs.toList())
    }
    void baseDir(String baseDir) {
        this.baseDir.set(baseDir)
    }
    void source(String include, Class loader) {
        this.sources.add(new ResourceDefinition().setPattern(include).setLoaderClass(loader))
    }
    void source(String include) {
        source(include, classFor(include))
    }
    void source(Map m) {
        assert m != null

        java.lang.Object i = m.include
        assert i != null

        java.lang.Object o = m.loader
        if (o == null) {
            source(i)
        } else {
            source(i, o)
        }

    }

    void outputDir(String outputDir) {
        this.outputDir.set(outputDir)
    }
    void prohibitedTerms(String... prohibitedTerms) {
        this.prohibitedTerms.set(prohibitedTerms.toList())
    }
    void prohibitedTerms(List<String> prohibitedTerms) {
        this.prohibitedTerms.set(prohibitedTerms)
    }

    private static Class<? extends ResourceDefinition> classFor(String include) {
        if (include.endsWith(".properties")) {
            return PropertiesLoader.class
        } else if (include.endsWith(".json")) {
            return JsonLoader.class
        } else if (include.endsWith(".csv")) {
            return CsvLoader.class
        } else {
            throw new GradleException("extension of file[${include}] not suppoted for include")
        }
    }
}
