package online.danielstefani.paddy.daemon.dto

import online.danielstefani.paddy.daemon.Daemon

class CreateDaemonResponse(daemon: Daemon, val jwt: String)
    : Daemon(daemon)
