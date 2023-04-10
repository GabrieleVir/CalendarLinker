package discord.bots.calendarlinker.listeners;

import discord.bots.calendarlinker.config.DiscordServerConfigurationProperties;
import discord.bots.calendarlinker.service.CalendarLinkerCommands.FunCommandsService;
import discord.bots.calendarlinker.service.CalendarLinkerCommands.GoogleCalendarCommandsService;
import discord.bots.calendarlinker.service.DiscordNotificationService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import java.util.regex.Pattern;


public abstract class MessageListener {

    @Autowired
    private GoogleCalendarCommandsService googleCalendarCommandsService;

    @Autowired
    private FunCommandsService funCommandsService;





    public Mono<Void> processCommand(Message eventMessage) {
        User author = eventMessage.getAuthor().get();
        String contentMessage = eventMessage.getContent();
        String[] commandAndArgs = contentMessage.split(" ", 2);

        if (!author.isBot()) {
            switch (commandAndArgs[0].toUpperCase()) {
                case "!INSULTEJB":
                    return this.funCommandsService.insultJbCommand(eventMessage);
                case "!CALENDRIER":
                    return this.googleCalendarCommandsService.getCalendarUrl(eventMessage);
                case "!ROLEACCESS":
                    return this.googleCalendarCommandsService.roleAccessCommand(eventMessage, commandAndArgs);
                default:
                    break;
            }
        }
        return Mono.just(eventMessage).then();
    }
}