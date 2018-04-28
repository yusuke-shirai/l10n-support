package sample.gradle.plugin

class L10nStatusSpock extends spock.lang.Specification {
    def "Fromソースに禁止文字列"() {
        expect:
        L10nStatus.SRC_PROHIBITED == L10nStatus.from(true, false, "test")
        L10nStatus.SRC_PROHIBITED == L10nStatus.from(true, true, "test")
        L10nStatus.SRC_PROHIBITED == L10nStatus.from(true, false, "")
        L10nStatus.SRC_PROHIBITED == L10nStatus.from(true, false, null)
    }
    def "Fromターゲットに禁止文字列"() {
        expect:
        L10nStatus.TGT_PROHIBITED == L10nStatus.from(false, true, "test")
        L10nStatus.TGT_PROHIBITED == L10nStatus.from(false, true, "")
        L10nStatus.TGT_PROHIBITED == L10nStatus.from(false, true, null)
    }
    def "From翻訳なし"() {
        expect:
        L10nStatus.NO_TRANSLATION == L10nStatus.from(false, false, "")
        L10nStatus.NO_TRANSLATION == L10nStatus.from(false, false, null)
    }
    def "From問題なし"() {
        expect:
        L10nStatus.FINE == L10nStatus.from(false, false, "a")
    }

    def "ToString"() {
        expect:
        "fine" == L10nStatus.FINE.toString()
        "prohibited word in original" == L10nStatus.SRC_PROHIBITED.toString()
        "prohibited word in translation" == L10nStatus.TGT_PROHIBITED.toString()
        "no translation" == L10nStatus.NO_TRANSLATION.toString()
    }
}
