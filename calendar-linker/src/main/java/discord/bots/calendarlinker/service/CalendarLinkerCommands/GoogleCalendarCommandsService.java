package discord.bots.calendarlinker.service.CalendarLinkerCommands;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
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

    public Mono<Void> roleAccessCommand(Message eventMessage, String commandAndArgs) {
        this.calendarService.insertUserInAclRule(commandAndArgs, "reader");
        return this.discordNotificationService.success(
                eventMessage,
                "An email has been sent to your email to access the calendar"
        );
    }

}
