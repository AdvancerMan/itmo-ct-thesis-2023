package ru.itmo.kazakov.autoschedule.nsp.solver.statistics

import com.fasterxml.jackson.databind.json.JsonMapper
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.formatter.StatisticsFormatter
import java.nio.file.Path
import java.time.Clock
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

class FileStatisticsWriter(

    private val targetDirectory: Path,

    private val formatters: List<StatisticsFormatter>,

    private val clock: Clock,
) : StatisticsWriter {

    companion object {
        private val JSON_MAPPER = JsonMapper()
    }

    override fun <ID> write(solutionCollector: NspStatisticsCollector<ID>) {
        formatters.forEach { formatter ->
            val formattedJson = formatter.format(solutionCollector)

            val entriesDirectory = targetDirectory.resolve(formatter::class.simpleName!!)
            entriesDirectory.createDirectories()

            entriesDirectory
                .resolve("${clock.instant()}.json")
                .writeText(JSON_MAPPER.writeValueAsString(formattedJson))
        }
    }
}
