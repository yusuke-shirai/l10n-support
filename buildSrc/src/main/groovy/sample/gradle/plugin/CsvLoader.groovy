package sample.gradle.plugin

import com.orangesignal.csv.Csv
import com.orangesignal.csv.CsvConfig
import com.orangesignal.csv.handlers.ColumnPositionMapListHandler

import java.util.regex.Pattern

class CsvLoader extends ResourceLoaderBase implements ResourceLoader {

    @Override
    Properties load(File file, String lang) {
        assert file.name.endsWith(".csv")

        def p = new Properties()

        File f = getFile(file, lang)
        if (f.exists()) {
            CsvConfig cfg = new CsvConfig()
            cfg.setQuoteDisabled(false)
            cfg.setEscapeDisabled(false)
            cfg.setNullString("NULL")
            cfg.setIgnoreLeadingWhitespaces(true)
            cfg.setIgnoreTrailingWhitespaces(true)
            cfg.setIgnoreEmptyLines(true)
            cfg.setIgnoreLinePatterns(Pattern.compile("^#.*"))
            cfg.setVariableColumns(false)

            final List<Map<Integer, String>> rows = Csv.load(f, cfg, new ColumnPositionMapListHandler())
            if (rows.size() >= 2) {
                final Integer col = langColumn(rows.get(0), lang)
                for (int i = 1; i < rows.size(); i++) {
                    final Map<Integer, String> row = rows.get(i)

                    p.setProperty(
                            row.get(0),
                            (col != null) ? row.get(col) : "")
                }
            }
        }
        return p
    }

    Integer langColumn(Map<Integer, String> header, String lang) {
        Integer ret = null

        header.each {k,v ->
            if (v.toLowerCase() == lang.toLowerCase()) ret = k
        }
        if (ret < 0) {
            header.each { k, v ->
                if (v.toLowerCase().contains(lang.toLowerCase())) ret = k
            }
        }
        return ret
    }

    @Override
    File getFile(File source, String lang) {
        return new File(source.parent, source.name.replace(sourceLang, lang))
    }

}
