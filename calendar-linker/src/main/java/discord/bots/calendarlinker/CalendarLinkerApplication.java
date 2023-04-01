package discord.bots.calendarlinker;

import discord.bots.calendarlinker.service.KeepAliveService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CalendarLinkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalendarLinkerApplication.class, args);
        KeepAliveService.reportCurrentTime();
    }

}
