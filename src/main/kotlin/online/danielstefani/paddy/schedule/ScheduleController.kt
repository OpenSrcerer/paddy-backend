package online.danielstefani.paddy.schedule

import io.quarkus.security.Authenticated
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import online.danielstefani.paddy.mqtt.RxMqttClient
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.notFound
import org.jboss.resteasy.reactive.RestResponse.ok

@Path("/daemon/{daemonId}/schedule")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class ScheduleController(
    private val scheduleService: ScheduleService,
    private val scheduleRepository: ScheduleRepository,
    private val mqttClient: RxMqttClient
) {
    @GET
    @Path("/{id}")
    fun getSchedule(
        @RestPath id: Long,
        @RestPath daemonId: Long
    ): RestResponse<Schedule?> {
        return scheduleRepository.get(id, daemonId.toString())
            ?.let { ok(it) }
            ?: notFound()
    }

    @GET
    fun getAllSchedules(@RestPath daemonId: String): List<Schedule> {
        return scheduleRepository.getAll(daemonId)
    }

    @POST
    fun postSchedule(
        @RestPath daemonId: Long,
        @NotNull @Valid schedule: Schedule
    ): RestResponse<Schedule?> {
        return scheduleService.createSchedule(daemonId.toString(), schedule)
            ?.let {
                mqttClient.publish(it.id.toString())?.subscribe()
                ok(it)
            }
            ?: notFound()
    }

    @PATCH
    @Path("/{id}")
    fun patchSchedule(
        @RestPath id: Long,
        @RestPath daemonId: Long,
        @NotNull @Valid schedule: Schedule
    ): RestResponse<Schedule?> {
        val updatedSchedule = scheduleRepository.update(id, daemonId.toString()) {

            it.type = (schedule.type ?: it.type)
            it.periodic = (schedule.periodic ?: it.periodic)
            it.finish = (schedule.finish ?: it.finish)
            it.timezone = (schedule.timezone ?: it.timezone)

        }?.also {
            mqttClient.publish(it.id.toString())?.subscribe()
        }

        return if (updatedSchedule != null) ok(updatedSchedule)
        else notFound()
    }

    @DELETE
    @Path("/{id}")
    fun deleteSchedule(
        @RestPath id: Long,
        @RestPath daemonId: Long,
    ): RestResponse<Schedule?> {
        return scheduleRepository.delete(id, daemonId.toString())
            ?.let {
                mqttClient.publish(it.id.toString())?.subscribe()
                ok(it)
            }
            ?: notFound()
    }

}