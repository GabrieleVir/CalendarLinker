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
    private DiscordNotificationService discordNotificationService;

    @Autowired
    private GoogleCalendarCommandsService googleCalendarCommandsService;

    @Autowired
    private FunCommandsService funCommandsService;

    @Autowired
    private DiscordServerConfigurationProperties discordServerConfiguration;

    public boolean isATestOrAccessDemandChannel(Message eventMessage) {
        Snowflake channelId = eventMessage.getChannelId();
        String guildId =  eventMessage.getGuildId().toString();
        return channelId.asString().equals(discordServerConfiguration.getTestChannelId(guildId)) ||
                channelId.asString().equals(discordServerConfiguration.getAccessDemandId(guildId));

    }


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
                    String emailRegexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                    if (
                            commandAndArgs.length == 2 &&
                            Pattern.matches(emailRegexPattern, commandAndArgs[1]) &&
                            isATestOrAccessDemandChannel(eventMessage)
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