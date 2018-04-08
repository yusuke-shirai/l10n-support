package sample.gradle.plugin;

import java.io.File;
import java.util.Properties;

interface ResourceLoader<T> {

    Properties load(File file, String lang)

    T setSourceLang(String lang)
}
