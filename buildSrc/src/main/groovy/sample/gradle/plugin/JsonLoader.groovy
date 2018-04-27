package sample.gradle.plugin

import groovy.json.JsonSlurper
import org.gradle.api.GradleException

class JsonLoader extends ResourceLoaderBase implements ResourceLoader {

    @Override
    Properties load(File file, String lang) {
        assert file.name.endsWith(".json")

        def p = new Properties()

        File f = getFile(file, lang)
        if (f.exists()) {
            def str = f.getText((charset) ?: "utf-8")
            if (str.startsWith("\uFEFF")) {
                str = str.substring(1);
            }
            Map json = new JsonSlurper().parseText(str)
            json.each {k, v ->
                if (!(v instanceof String)) {
                    throw new GradleException("Invalid translation (File=${file.path},Key=${k},Value=${v},type=${v.class})")
                }
                p.setProperty(k, v)
            }
        }
        return p
    }

    @Override
    File getFile(File source, String lang) {
        return new File(source.parent, source.name.replace(sourceLang, lang))
    }

}
