package discord.bots.calendarlinker.service.CalendarLinkerCommands;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
public class FunCommandsService extends Commands {
    public Mono<Void> insultJbCommand(Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Grosse merde!"))
                .then();
    }

}
