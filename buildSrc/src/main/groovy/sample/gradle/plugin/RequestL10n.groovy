package sample.gradle.plugin

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileTreeElement

class RequestL10n extends DefaultTask {
    final Property<CharSequence> sourceLang = project.objects.property(CharSequence)
    final ListProperty<CharSequence> targetLang = project.objects.listProperty(CharSequence)
    final Property<CharSequence> baseDir = project.objects.property(CharSequence)
    final ListProperty<CharSequence> sourceFile = project.objects.listProperty(CharSequence)
    final Property<CharSequence> workDir = project.objects.property(CharSequence)
    final Property<CharSequence> previousRequest = project.objects.property(CharSequence)

    @TaskAction
    void requestL10n() {
        copy()
        record()
    }
    void copy() {
        project.copy {
            from allTree()
            into "${workDir.get()}/${project.name}"
        }
    }
    void record() {
        tree(sourceFile.get()).visit { FileTreeElement e ->
            if (!e.directory) {
                recordFile(e)
            }
        }
    }

    void recordFile(FileTreeElement e) {
        Properties src1 = loadProperties(e.file)
        Properties src0 = loadPrevious(e)
        targetLang.get().each { lang ->
            Properties tgt1 = loadProperties(new File(e.file.parent, e.file.name.replaceAll(sourceLang.get(), lang)))
            recordFileLocale(relativePath(e), lang, src0, src1, tgt1)
        }
    }

    CharSequence relativePath(FileTreeElement e) {
        "${project.name}/${e.relativePath}"
    }

    Properties loadPrevious(FileTreeElement e) {
        if (previousRequest.get() == null) {
            return new Properties()
        }
        try {
            FileTree prevTree = project.zipTree(previousRequest.get()).matching {
                include relativePath(e)
            }
            return loadProperties(prevTree.singleFile)
        } catch (Throwable ignore) {
            return new Properties()
        }
    }

    void recordFileLocale(String path, String lang, Properties src0, Properties src1, Properties tgt1) {
        new File("${workDir.get()}/l10nRequest_${lang}.csv").withWriterAppend('utf-8') { writer ->
            src1.keys().each { key ->
                def s1 = src1.getProperty(key)
                def s0 = src0.getProperty(key)
                def t1 = tgt1.getProperty(key)
                boolean needsTranslation = (s1 != s0) || t1 == null || t1.isEmpty()
                writer.writeLine "${path},${key},${needsTranslation},\"${s1}\",\"${s0}\",\"${t1}\""
            }
        }
    }

    //TODO 汎用的な仕組みにする
    Properties loadProperties(File f) {
        def p = new Properties()
        if (!f.exists()) {
            return p
        }
        if (f.path.endsWith(".properties")) {
            f.withInputStream { stream ->
                p.load(stream)

            }
        } else if (f.path.endsWith(".json")) {
            Map json = new JsonSlurper().parseText(f.getText("utf-8"))
            json.keySet().each {key ->
                p.setProperty(key, json.get(key))
            }
        } else {
            throw new UnsupportedOperationException("format not supported, ${f}",)
        }
        return p
    }

    FileTree allTree() {
        def includes = []
        sourceFile.get().each { i ->
            includes.add(i)
            targetLang.get().each { t ->
                includes.add(i.replaceAll(sourceLang.get(), t))
            }
        }
        return tree(includes)
    }
    FileTree tree(List<CharSequence> includes) {
        FileTree tree = project.fileTree(baseDir.get())
        if(!includes.isEmpty()) {
            tree = tree.matching { include includes }
        }
        return tree
    }
}
