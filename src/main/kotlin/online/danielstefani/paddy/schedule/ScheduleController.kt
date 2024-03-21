package online.danielstefani.paddy.schedule

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import online.danielstefani.paddy.mqtt.RxMqttClient
import online.danielstefani.paddy.util.username
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.*

@Path("/daemon/{daemonId}/schedule")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class ScheduleController(
    private val securityIdentity: SecurityIdentity,
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
        return scheduleRepository.getAll(daemonId, securityIdentity.username())
    }

    @POST
    fun postSchedule(
        @RestPath daemonId: Long,
        @NotNull @Valid schedule: Schedule
    ): RestResponse<Schedule?> {
        if (schedule.single == null && schedule.periodic == null) {
            return status(Status.BAD_REQUEST)
        }

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
            it.single = (schedule.single ?: it.single)
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