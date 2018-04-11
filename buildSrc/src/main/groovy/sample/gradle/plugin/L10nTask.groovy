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
    final ListProperty<ResourceDefinition> sourceFiles = project.objects.listProperty(ResourceDefinition)
    final Property<CharSequence> workDir = project.objects.property(CharSequence)
    final Property<CharSequence> prohibitedWordFile = project.objects.property(CharSequence)

    final def counter = new ResourceCounter()
    ProhibitedChecker checker = ProhibitedChecker.EMPTY
    L10nFile record = null

    @TaskAction
    void requestL10n() {
        initialize()

        sourceFiles.get().each { ResourceDefinition d ->
            project.fileTree(baseDir.get()).matching {
                include d.pattern
            }.visit { FileTreeElement e ->
                if (!e.directory) {
                    recordFile(e, d.loaderClass)
                }
            }
        }

        logger.lifecycle("L10n Result:")
        counter.get().each { k,v ->
            logger.lifecycle("- {}: {}", k, v)
        }
    }

    void initialize() {
        new File("${workDir.get()}").mkdirs()

        String path = prohibitedWordFile.getOrNull()
        if (path != null) {
            this.checker = new ProhibitedChecker(new File(path).readLines("utf-8"))
        }

        this.record = new L10nFile(new File(workDir.get()))
    }

    void recordFile(FileTreeElement e, Class c) {
        def f = e.file
        def src = loadProperties(f, sourceLang.get(), c)
        targetLangs.get().each { lang ->
            def tgt = loadProperties(f, lang, c)
            recordFileLocale("${project.name}/${e.relativePath}", lang, src, tgt)
        }
    }

    void recordFileLocale(String path, String lang, Properties src, Properties tgt) {
        src.keys().each { key ->
            def r = new L10nFile.Record()

            r.path = path

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

    Properties loadProperties(File f, String l, Class<? extends ResourceLoader> c) {
        if (!f.exists()) {
            return new Properties()
        }
        ResourceLoader loader = c.getDeclaredConstructor().newInstance()
        loader.sourceLang = sourceLang.get()
        return loader.load(f, l)
    }
}
