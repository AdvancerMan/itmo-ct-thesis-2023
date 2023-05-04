package ru.itmo.kazakov.autoschedule.configuration

import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import ru.itmo.kazakov.autoschedule.nsp.factory.AutoscheduleWeightName
import ru.itmo.kazakov.autoschedule.nsp.factory.AutoscheduleWeightsContainer
import java.util.Properties

class PropertiesAutoscheduleWeightsContainer(

    propertiesPath: String,
) : AutoscheduleWeightsContainer {

    companion object {
        private val LOG = LoggerFactory.getLogger(PropertiesAutoscheduleWeightsContainer::class.java)
    }

    private val weights: Map<AutoscheduleWeightName, Double>

    init {
        val resource = ClassPathResource(propertiesPath)
        val properties = if (resource.exists()) {
            LOG.info("Loading nsp weights properties: {}", resource.description)
            Properties().apply { load(resource.inputStream) }
        } else {
            LOG.warn("Nsp weights not found: {}", resource.description)
            Properties()
        }

        weights = AutoscheduleWeightName.values()
            .mapNotNull { weightName ->
                properties.getProperty(weightName.propertyName)
                    ?.toDoubleOrNull()
                    ?.let { weightName to it }
            }
            .associate { it }

        LOG.debug("Loaded weights: {}", weights)
    }

    override fun getOrDefault(name: AutoscheduleWeightName, defaultValue: Double): Double {
        return weights.getOrDefault(name, defaultValue)
    }
}
