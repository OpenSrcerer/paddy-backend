package online.danielstefani.paddy.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.neo4j.ogm.model.Result

val objectMapper = ObjectMapper()

inline fun <reified T: Any> Result.get(): List<T> {
    if (T::class == Unit::class)
        return emptyList()

    return with(this.queryResults().iterator()) {
        if (!this.hasNext())
            emptyList<T>()

        this.asSequence().toList().flatMap<Map<String, Any>, T> { resMap ->
            if (resMap.size == 1)
                resMap.values.map { it as T }
            else
                listOf(objectMapper.convertValue(resMap, T::class.java))
        }
    }

}