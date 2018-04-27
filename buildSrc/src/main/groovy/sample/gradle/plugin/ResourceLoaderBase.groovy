package sample.gradle.plugin;

abstract class ResourceLoaderBase<T> implements ResourceLoader<T> {

    String charset = null

    String sourceLang = null

    @Override
    final T setSourceLang(String lang) {
        this.sourceLang = lang
        return this
    }

    @Override
    final T setCharset(String charset) {
        this.charset = charset
        return this
    }

}
