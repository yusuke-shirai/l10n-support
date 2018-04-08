package sample.gradle.plugin

class PropertiesLoader extends ResourceLoaderBase implements ResourceLoader {

    @Override
    Properties load(File file, String lang) {
        assert file.name.endsWith(".properties")

        File f = new File(file.parent, file.name.replace(sourceLang, lang))
        def p = new Properties()
        f.withInputStream { s ->
            p.load(s)
        }
        return p
    }
}
