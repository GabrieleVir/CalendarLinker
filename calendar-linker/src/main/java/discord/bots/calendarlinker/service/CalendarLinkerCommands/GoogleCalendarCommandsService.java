package discord.bots.calendarlinker.service.CalendarLinkerCommands;

import discord.bots.calendarlinker.config.DiscordServerConfigurationProperties;
import discord.bots.calendarlinker.service.DiscordNotificationService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Service
public class GoogleCalendarCommandsService extends Commands {

    public Mono<Void> getCalendarUrl(Message eventMessage) {
        String calendarUrl = this.calendarService.getCalendarUrl();
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(calendarUrl))
                .then();
    }

    public boolean isATestOrAccessDemandChannel(Message eventMessage) {
        Snowflake channelId = eventMessage.getChannelId();
        String guildId =  eventMessage.getGuildId().get().asString();
        return channelId.asString().equals(discordServerConfiguration.getTestChannelId(guildId)) ||
                channelId.asString().equals(discordServerConfiguration.getAccessDemandId(guildId));

    }

    public Mono<Void> roleAccessCommand(Message eventMessage, String[] commandAndArgs) {
        String emailRegexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        if (
                commandAndArgs.length == 2 &&
                        Pattern.matches(emailRegexPattern, commandAndArgs[1]) &&
                        isATestOrAccessDemandChannel(eventMessage)
        ) {
            this.calendarService.insertUserInAclRule(commandAndArgs[1], "reader");
            return this.discordNotificationService.success(
                    eventMessage,
                    "An email has been sent to your email to access the calendar"
            );
        } else {
            return this.discordNotificationService.error(
                    eventMessage,
                    "Please insert a valid email after the command !roleAccess. " +
                            "For example: !roleAccess test@hotmail.com"
            );
        }
    }

}
