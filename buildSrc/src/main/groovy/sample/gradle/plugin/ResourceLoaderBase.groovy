package sample.gradle.plugin;

abstract class ResourceLoaderBase<T> implements ResourceLoader<T> {

    String sourceLang = null

    @Override
    final T setSourceLang(String lang) {
        this.sourceLang = lang
        return this
    }

}
