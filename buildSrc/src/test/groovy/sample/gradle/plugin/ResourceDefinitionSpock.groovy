package sample.gradle.plugin

import org.gradle.api.GradleException
import spock.lang.Specification

class ResourceDefinitionSpock extends Specification {
    def "初期値"() {
        setup:
        def t = new ResourceDefinition()

        expect:
        null == t.pattern
        null == t.charset
        null == t.loaderClass
    }
    def "SetPattern_初期値"() {
        setup:
        def t = new ResourceDefinition()

        when:
        t.setPattern("test")

        then:
        "test" == t.pattern
        null == t.charset
        null == t.loaderClass
    }

    def "SetCharset"() {
        setup:
        def t = new ResourceDefinition()

        when:
        t.setCharset("test")

        then:
        null == t.pattern
        "test" == t.charset
        null == t.loaderClass
    }

    def "SetLoaderClass"() {
        setup:
        def t = new ResourceDefinition()

        when:
        t.setLoaderClass(PropertiesLoader.class)

        then:
        null == t.pattern
        null == t.charset
        PropertiesLoader.class == t.loaderClass
    }

    def "From_Definition"() {
        setup:
        def d = new ResourceDefinition().setCharset("my charset").setPattern("my pattern").setLoaderClass(JsonLoader.class)

        when:
        def t = ResourceDefinition.from(d)

        then:
        "my charset" == t.charset
        "my pattern" == t.pattern
        JsonLoader.class == t.loaderClass
    }
    def "From_Map単一要素"() {
        when:
        def t = ResourceDefinition.from(["my pattern" : JsonLoader.class])

        then:
        null == t.charset
        "my pattern" == t.pattern
        JsonLoader.class == t.loaderClass
    }
    def "From_Map複数要素"() {
        when:
        def o = ["pattern1" : JsonLoader.class, "pattern2" : PropertiesLoader.class]
        ResourceDefinition.from(o)

        then:
        GradleException e = thrown()
        e.message == "size of map should be 1 [${o}]"

    }
    def "From_Mapの値に知らない型"() {
        when:
        def o = java.lang.Object.class
        ResourceDefinition.from(["pattern1" : o])

        then:
        GradleException e = thrown()
        "Not a child class of ResourceLoader [${o}]" == e.message
    }
    def "From_JsonPatternString"() {
        when:
        def t = ResourceDefinition.from('*.json')

        then:
        null == t.charset
        "*.json" == t.pattern
        JsonLoader.class == t.loaderClass
    }
    def "From_PropertiesPatternString"() {
        when:
        def t = ResourceDefinition.from('*.properties')

        then:
        null == t.charset
        "*.properties" == t.pattern
        PropertiesLoader.class == t.loaderClass
    }
    def "From_知らないPatternString"() {
        when:
        ResourceDefinition.from('*.unknown')

        then:
        GradleException e = thrown()
        "extension of file[*.unknown] not supported" == e.message
    }
    def "From_知らない型"() {
        when:
        def v = new Date()
        ResourceDefinition.from(v)

        then:
        GradleException e = thrown()
        "type[${v.class}] not supported" == e.message
    }
}
