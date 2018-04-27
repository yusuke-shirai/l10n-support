package sample.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty
import org.gradle.api.file.FileTreeElement

class L10nTask extends DefaultTask {
    final Property<CharSequence> sourceLang = project.objects.property(CharSequence)
    final ListProperty<CharSequence> targetLangs = project.objects.listProperty(CharSequence)
    final Property<CharSequence> baseDir = project.objects.property(CharSequence)
    final ListProperty<ResourceDefinition> sources = project.objects.listProperty(ResourceDefinition)
    final Property<CharSequence> outputDir = project.objects.property(CharSequence)
    final ListProperty<CharSequence> prohibitedTerms = project.objects.listProperty(CharSequence)

    final def counter = new ResourceCounter()
    ProhibitedChecker checker = ProhibitedChecker.EMPTY
    L10nFile record = null

    @TaskAction
    void requestL10n() {
        initialize()

        sources.get().each { ResourceDefinition d ->
            project.fileTree(baseDir.get()).matching {
                include d.pattern
            }.visit { FileTreeElement e ->
                if (!e.directory) {
                    recordFile(e, d)
                }
            }
        }

        int total = counter.total()
        if (total == 0) {
            logger.lifecycle("No resource found.")
        } else {
            logger.lifecycle("L10n Result:")
            counter.get().each { k,v ->
                if (v.intValue() > 0) {
                    logger.warn("- {}: {}", k, v)
                }
            }
        }
    }

    void initialize() {
        new File(outputDir.get()).mkdirs()

        this.checker = new ProhibitedChecker(prohibitedTerms.get())
        this.record = new L10nFile(new File(outputDir.get()))
    }

    void recordFile(FileTreeElement e, ResourceDefinition d) {
        def f = e.file
        def src = loadProperties(f, sourceLang.get(), d)
        targetLangs.get().each { lang ->
            def tgt = loadProperties(f, lang, d)
            recordFileLocale("${project.name}/${e.relativePath}", lang, src, tgt)
        }
    }

    void recordFileLocale(String path, String lang, Properties src, Properties tgt) {
        src.keys().each { key ->
            def r = new L10nFile.Record()

            r.path = path
            r.key = key

            r.source = src.getProperty(key)
            def sProhibited = checker.check(r.source)

            r.translation = tgt.getProperty(key)
            def tProhibited = checker.check(r.translation)

            r.status = L10nStatus.from(sProhibited.size() > 0, tProhibited.size() > 0, r.translation)
            counter.up(r.status)

            r.note = ""
            switch (r.status) {
                case L10nStatus.SRC_PROHIBITED:
                    r.note = sProhibited.join("/")
                    break
                case L10nStatus.TGT_PROHIBITED:
                    r.note = tProhibited.join("/")
                    break
                default:
                    break
            }

            if (r.status != L10nStatus.FINE) {
                record.append(lang, r)
            }
        }
    }

    Properties loadProperties(File f, String l, ResourceDefinition d) {
        Class<? extends ResourceLoader> c = d.loaderClass
        ResourceLoader loader = c.getDeclaredConstructor().newInstance()
        loader.sourceLang = sourceLang.get()
        loader.charset = d.charset
        if (!loader.getFile(f, l).exists()) {
            return new Properties()
        }
        return loader.load(f, l)
    }
}
