package sample.gradle.plugin

class L10nFile {

    final File dir

    class Record {
        String path
        String key
        L10nStatus status
        String source
        String translation
        String note
    }

    L10nFile(File dir) {
        this.dir = dir
    }

    def append(String lang, Record r) {
        final def recordFile = new File(dir, "l10n_${lang}.csv")
        recordFile.withWriterAppend('utf-8') { writer ->
            writer.writeLine "${r.path ?: ''},${r.key ?: ''},${r.status ?: ''}, \"${r.source ?: ''}\",\"${r.translation ?: ''}\",\"${r.note ?: ''}\""
        }
    }

}
