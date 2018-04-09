package sample.gradle.plugin

import org.gradle.internal.impldep.com.google.gson.internal.LinkedHashTreeMap

class ResourceCounter {

    final def counters = new TreeMap<L10nStatus, Integer>()

    ResourceCounter() {
        L10nStatus.values().each {
            counters.put(it, Integer.valueOf(0))
        }
    }

    def up(L10nStatus status) {
        counters.put(status, Integer.valueOf(counters.get(status).intValue() + 1))
    }

    Map<L10nStatus, Integer> get() {
        return counters.asImmutable()
    }

}
