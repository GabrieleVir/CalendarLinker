package discord.bots.calendarlinker.config;

public class DiscordServerProperty {


    public String getDiscordServerId() {
        return discordServerId;
    }

    public String getTestChannelId() {
        return testChannelId;
    }

    public String getAccessDemandId() {
        return accessDemandId;
    }

    private final String discordServerId;
    private final String testChannelId;
    private final String accessDemandId;

    public DiscordServerProperty (String discordServerId, String testChannelId, String accessDemandId) {
        this.discordServerId = discordServerId;
        this.testChannelId = testChannelId;
        this.accessDemandId = accessDemandId;
    }
    // getter and Setters
}