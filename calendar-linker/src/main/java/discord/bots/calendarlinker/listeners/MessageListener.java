package discord.bots.calendarlinker.listeners;

import discord.bots.calendarlinker.service.CalendarLinkerCommands.FunCommandsService;
import discord.bots.calendarlinker.service.CalendarLinkerCommands.GoogleCalendarCommandsService;
import discord.bots.calendarlinker.service.DiscordNotificationService;
import discord.bots.calendarlinker.service.GoogleCalendarApi.GoogleCalendarACLManager;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import java.util.regex.Pattern;


public abstract class MessageListener {

    @Autowired
    private DiscordNotificationService discordNotificationService;

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
                    eventMessage.getChannel();
                    return this.funCommandsService.insultJbCommand(eventMessage);
                case "!CALENDRIER":
                    return this.googleCalendarCommandsService.getCalendarUrl(eventMessage);
                case "!ROLEACCESS":
                    String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                    if (
                            commandAndArgs.length == 2 &&
                            Pattern.matches(regexPattern, commandAndArgs[1])
                    ) {
                        return this.googleCalendarCommandsService.roleAccessCommand(eventMessage, commandAndArgs[1]);
                    } else {
                        return this.discordNotificationService.error(
                            eventMessage,
                            "Please insert a valid email after the command !roleAccess. " +
                            "For example: !roleAccess test@hotmail.com"
                        );
                    }
                default:
                    break;
            }
        }
        return Mono.just(eventMessage).then();
    }
}