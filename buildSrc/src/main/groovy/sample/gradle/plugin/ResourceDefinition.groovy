package sample.gradle.plugin

import org.gradle.api.GradleException

class ResourceDefinition {
    String pattern
    Class<? extends ResourceLoader> loaderClass

    ResourceDefinition setPattern(String pattern) {
        this.pattern = pattern
        return this
    }
    ResourceDefinition setLoaderClass(Class<? extends ResourceLoader> loaderClass) {
        this.loaderClass = loaderClass
        return this
    }

}
