package sample.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty
import org.gradle.api.file.FileTreeElement

import java.util.regex.Pattern

class L10nTask extends DefaultTask {
    final Property<CharSequence> sourceLang = project.objects.property(CharSequence)
    final ListProperty<CharSequence> targetLang = project.objects.listProperty(CharSequence)
    final Property<CharSequence> baseDir = project.objects.property(CharSequence)
    final ListProperty<Object> sourceFile = project.objects.listProperty(Object)
    final Property<CharSequence> workDir = project.objects.property(CharSequence)

    final def sourceDef = new ArrayList<ResourceDefinition>()
    final def prohibited = new ArrayList<String>()
    L10nFile record = null


    @TaskAction
    void requestL10n() {
        initialize()

        sourceDef.each { ResourceDefinition d ->
            project.fileTree(baseDir.get()).matching {
                include d.pattern
            }.visit { FileTreeElement e ->
                if (!e.directory) {
                    recordFile(e, d.loaderClass)
                }
            }
        }
    }

    void initialize() {
        new File("${workDir.get()}").mkdirs()

        sourceFile.get().each {
            sourceDef.add(ResourceDefinition.from(it))
        }
        final def pFile = new File("prohibited.txt")
        if (pFile.exists()) {
            prohibited.add(pFile.readLines("utf-8"))
        }
        this.record = new L10nFile(new File(workDir.get()))
    }

    void recordFile(FileTreeElement e, Class c) {
        def f = e.file
        def src = loadProperties(f, sourceLang.get(), c)
        targetLang.get().each { lang ->
            def tgt = loadProperties(f, lang, c)
            recordFileLocale("${project.name}/${e.relativePath}", lang, src, tgt)
        }
    }

    void recordFileLocale(String path, String lang, Properties src, Properties tgt) {
        src.keys().each { key ->
            def r = new L10nFile.Record()

            r.source = src.getProperty(key)
            def sProhibited = checkProhibited(r.source)

            r.translation = tgt.getProperty(key)
            def tProhibited = checkProhibited(r.translation)

            r.status = L10nStatus.from(sProhibited, tProhibited, r.translation)

            r.note = ""
            switch (r.status) {
                case L10nStatus.SRC_PROHIBITED:
                    r.note = sProhibited
                    break
                case L10nStatus.TGT_PROHIBITED:
                    r.note = tProhibited
                    break
                default:
                    break
            }

            if (r.status != L10nStatus.FINE) {
                record.append(lang, r)
            }
        }
    }

    String checkProhibited(String s) {
        if (s != null) {
            for (p in prohibited) {
                def matched = s.find(p)
                if (matched != null) {
                    return p
                }
            }
        }
        return null
    }

    Properties loadProperties(File f, String l, Class<? extends ResourceLoader> c) {
        if (!f.exists()) {
            return new Properties()
        }
        ResourceLoader loader = c.getDeclaredConstructor().newInstance()
        loader.sourceLang = sourceLang.get()
        logger.info("f={}", f)
        return loader.load(f, l)
    }
}
