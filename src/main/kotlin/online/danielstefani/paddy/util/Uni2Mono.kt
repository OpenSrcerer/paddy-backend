package online.danielstefani.paddy.util

import io.smallrye.mutiny.Uni
import reactor.core.publisher.Mono

fun <T> Uni<T>.toMono(): Mono<T> {
    return Mono.create { sink ->
        this.onItem().invoke { element -> sink.success(element) }
        this.onFailure().invoke { error -> sink.error(error) }
    }
}