package discord.bots.calendarlinker.service;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
public class DiscordNotificationService {
    public Mono<Void> error(Message eventMessage, String message) {
        return Mono.just(eventMessage)
                .filter(
                        messageFromDiscordUser ->
                                messageFromDiscordUser.getAuthor().map(user -> !user.isBot()).orElse(false)
                )
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("ERROR" + message))
                .then();
    }

    public Mono<Void> success(Message eventMessage, String message) {
        return Mono.just(eventMessage)
                .filter(
                        messageFromDiscordUser ->
                                messageFromDiscordUser.getAuthor().map(user -> !user.isBot()).orElse(false)
                )
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("SUCCESS: " + message))
                .then();
    }
}
