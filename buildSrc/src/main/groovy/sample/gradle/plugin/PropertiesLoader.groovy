package sample.gradle.plugin

class PropertiesLoader extends ResourceLoaderBase implements ResourceLoader {

    @Override
    Properties load(File file, String lang) {
        assert file.name.endsWith(".properties")

        def p = new Properties()
        if (charset != null) {
            getFile(file, lang).withReader(charset) { s ->
                p.load(s)
            }
        } else {
            getFile(file, lang).withInputStream { s ->
                p.load(s)
            }
        }
        return p
    }

    @Override
    File getFile(File source, String lang) {
        return new File(source.parent, source.name.replace(sourceLang, lang))
    }
}
