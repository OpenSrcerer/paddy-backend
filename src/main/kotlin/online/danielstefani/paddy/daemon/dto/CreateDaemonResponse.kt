package online.danielstefani.paddy.daemon.dto

import online.danielstefani.paddy.daemon.Daemon

class CreateDaemonResponse(daemon: Daemon, private val jwt: String)
    : Daemon(daemon)
