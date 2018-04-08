package sample.gradle.plugin

enum L10nStatus {

    FINE("fine", false),

    SRC_PROHIBITED("profibited word in original", true),

    TGT_PROHIBITED("profibited word in translation", true),

    NO_TRANSLATION("no translation", false),


    final String displayName
    final boolean isSevere
    L10nStatus(String displayName, boolean isSevere) {
        this.displayName = displayName
        this.isSevere = isSevere
    }

    static L10nStatus from(String srcProhibited, String tgtProhibited, String tgt) {
        if (srcProhibited != null) {
            return SRC_PROHIBITED
        } else if (tgtProhibited != null) {
            return TGT_PROHIBITED
        } else if (tgt == null || !tgt.isEmpty()) {
            return NO_TRANSLATION
        }
        return FINE
    }
}