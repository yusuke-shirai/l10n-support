package sample.gradle.plugin

import groovy.json.JsonSlurper

class JsonLoader extends ResourceLoaderBase implements ResourceLoader {

    @Override
    Properties load(File file, String lang) {
        assert file.name.endsWith(".json")

        File f = new File(file.parent, file.name.replace(sourceLang, lang))

        def p = new Properties()
        if (f.exists()) {
            Map json = new JsonSlurper().parseText(f.getText("utf-8"))
            json.each {k, v ->
                p.setProperty(k, v)
            }
        }
        return p
    }

}
