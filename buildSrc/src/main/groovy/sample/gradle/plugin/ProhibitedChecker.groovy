package sample.gradle.plugin;

import java.util.ArrayList;
import java.util.List;

class ProhibitedChecker {

    final def prohibited = new ArrayList<String>()
    static final def EMPTY = new ProhibitedChecker(new ArrayList<String>())

    ProhibitedChecker(List<String> prohibited) {
        this.prohibited = prohibited
    }

    List<String> check(String string) {
        def matched = new ArrayList<String>()
        if (string != null) {
            prohibited.each {
                if (string.toLowerCase().contains(it.toLowerCase())) {
                    matched.add(it)
                }
            }
        }
        matched
    }
}
