package discord.bots.calendarlinker.service.CalendarLinkerCommands;

import discord.bots.calendarlinker.config.DiscordServerConfigurationProperties;
import discord.bots.calendarlinker.service.DiscordNotificationService;
import discord.bots.calendarlinker.service.GoogleCalendarApi.GoogleCalendarACLManager;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class Commands {
    @Autowired
    protected GoogleCalendarACLManager calendarService;

    @Autowired
    protected DiscordNotificationService discordNotificationService;

    @Autowired
    protected DiscordServerConfigurationProperties discordServerConfiguration;

}
