package sample.gradle.plugin

enum L10nStatus {

    FINE("fine", false),

    SRC_PROHIBITED("prohibited word in original", true),

    TGT_PROHIBITED("prohibited word in translation", true),

    NO_TRANSLATION("no translation", false),


    final String displayName
    final boolean isSevere
    L10nStatus(String displayName, boolean isSevere) {
        this.displayName = displayName
        this.isSevere = isSevere
    }

    static L10nStatus from(boolean srcProhibited, boolean tgtProhibited, String tgt) {
        if (srcProhibited) {
            return SRC_PROHIBITED
        } else if (tgtProhibited) {
            return TGT_PROHIBITED
        } else if (tgt == null || tgt.isEmpty()) {
            return NO_TRANSLATION
        }
        return FINE
    }

    String toString() {
        return this.displayName
    }
}