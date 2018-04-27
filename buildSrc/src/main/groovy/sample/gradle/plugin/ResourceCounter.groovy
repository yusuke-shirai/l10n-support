package sample.gradle.plugin

import org.gradle.internal.impldep.com.google.gson.internal.LinkedHashTreeMap

class ResourceCounter {

    final def counters = new TreeMap<L10nStatus, Integer>()
    final int total = 0

    ResourceCounter() {
        L10nStatus.values().each {
            counters.put(it, Integer.valueOf(0))
        }
    }

    def up(L10nStatus status) {
        total++
        counters.put(status, Integer.valueOf(counters.get(status).intValue() + 1))
    }

    Map<L10nStatus, Integer> get() {
        return counters.asImmutable()
    }

    int total() {
        return total
    }
}
