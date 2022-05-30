package com.example.cmservice.common.config

import org.springframework.boot.context.event.ApplicationStartingEvent
import org.springframework.context.ApplicationListener
import java.time.ZoneOffset
import java.util.Locale
import java.util.TimeZone

class DefaultLocaleApplicationListener : ApplicationListener<ApplicationStartingEvent> {
    override fun onApplicationEvent(event: ApplicationStartingEvent) {
        Locale.setDefault(Locale.ENGLISH)
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC))
    }
}
