package discord.bots.calendarlinker.listeners;

import discord.bots.calendarlinker.service.DiscordNotificationService;
import discord.bots.calendarlinker.service.GoogleCalendarACLManager;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import java.util.regex.Pattern;


public abstract class MessageListener {
    @Autowired
    private GoogleCalendarACLManager calendarService;
    @Autowired
    private DiscordNotificationService discordNotificationService;

    private Mono<Void> toDoCommand(Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Things to do today:\n - write a bot\n - eat lunch\n - play a game"))
                .then();
    }

    private Mono<Void> insultJbCommand(Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Grosse merde!"))
                .then();
    }

    private Mono<Void> calendarCommand(Message eventMessage) {
        String calendarUrl = this.calendarService.getCalendarUrl();
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(calendarUrl))
                .then();
    }

    private Mono<Void> roleAccessCommand(Message eventMessage, String commandAndArgs) {
        this.calendarService.insertUserInAclRule(commandAndArgs, "reader");
        return this.discordNotificationService.success(
                eventMessage,
                "An email has been sent to your email to access the calendar"
        );
    }

    public Mono<Void> processCommand(Message eventMessage) {
        User author = eventMessage.getAuthor().get();
        String contentMessage = eventMessage.getContent();
        String[] commandAndArgs = contentMessage.split(" ", 2);

        if (!author.isBot()) {
            switch (commandAndArgs[0].toUpperCase()) {
                case "!TODO":
                    return toDoCommand(eventMessage);
                case "!INSULTEJB":
                    return insultJbCommand(eventMessage);
                case "!CALENDRIER":
                    return calendarCommand(eventMessage);
                case "!ROLEACCESS":
                    String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                    if (commandAndArgs.length == 2 && Pattern.matches(regexPattern, commandAndArgs[1])) {
                        return roleAccessCommand(eventMessage, commandAndArgs[1]);
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