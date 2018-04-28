package sample.gradle.plugin

import org.gradle.api.GradleException

class ResourceDefinition {
    String pattern = null
    String charset = null
    Class<? extends ResourceLoader> loaderClass

    ResourceDefinition setPattern(String pattern) {
        this.pattern = pattern
        return this
    }
    ResourceDefinition setCharset(String charset) {
        this.charset = charset
        return this
    }
    ResourceDefinition setLoaderClass(Class<? extends ResourceLoader> loaderClass) {
        this.loaderClass = loaderClass
        return this
    }

    static ResourceDefinition from(Object o) {
        if (o instanceof ResourceDefinition) {
            return new ResourceDefinition().setPattern(o.pattern).setLoaderClass(o.loaderClass).setCharset(o.charset)
        } else if (o instanceof Map<String, Class<? extends ResourceLoader>>) {
            if (o.size() != 1) {
                throw new GradleException("size of map should be 1 [${o}]")
            }

            String pattern = null
            Class<? extends ResourceLoader> loaderClass = null
            o.each {k, v ->
                if (!ResourceLoader.class.isAssignableFrom(v)) {
                    throw new GradleException("Not a child class of ResourceLoader [${v}]")
                }
                pattern = k
                loaderClass = v
            }
            return new ResourceDefinition().setPattern(pattern).setLoaderClass(loaderClass)
        } else if (o instanceof String) {
            if (o.endsWith(".properties")) {
                return new ResourceDefinition().setPattern(o).setLoaderClass(PropertiesLoader.class)
            } else if (o.endsWith(".json")) {
                return new ResourceDefinition().setPattern(o).setLoaderClass(JsonLoader.class)
            } else {
                throw new GradleException("extension of file[${o}] not supported")
            }
        } else {
            throw new GradleException("type[${o.class}] not supported")
        }
    }
}
