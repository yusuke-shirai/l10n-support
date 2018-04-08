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

    static ResourceDefinition from(Object object) {
        if (object instanceof ResourceDefinition) {
            return new ResourceDefinition().setPattern(object.pattern).setLoaderClass(object.loaderClass)
        } else if (object instanceof Map<String, Class<? extends ResourceLoader>>) {
            String pattern = null
            Class<? extends ResourceLoader> loaderClass = null
            object.each {k, v ->
                pattern = k
                loaderClass = v
            }
            return new ResourceDefinition().setPattern(pattern).setLoaderClass(loaderClass)
        } else if (object instanceof String) {
            if (object.endsWith(".properties")) {
                return new ResourceDefinition().setPattern(object).setLoaderClass(PropertiesLoader.class)
            } else if (object.endsWith(".json")) {
                return new ResourceDefinition().setPattern(object).setLoaderClass(JsonLoader.class)
            } else {
                throw new GradleException("extension of file[${object}] not suppoted for sourceFile")
            }
        } else {
            throw new GradleException("type[${object.class}] not suppoted for sourceFile")
        }
    }
}
