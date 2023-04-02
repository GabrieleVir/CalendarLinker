package discord.bots.calendarlinker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix="discord.server")
public class DiscordServerConfigurationProperties {
    DiscordServerProperty eventManager = new DiscordServerProperty("1088953785453396092", "1089378548080455752", "1091514098669338644");
    DiscordServerProperty deratiseur = new DiscordServerProperty("fame", "dc", "DC");

    public String getTestChannelId(String discordServerId) {
        String testChannelId;
        if (Objects.equals(eventManager.getDiscordServerId(), discordServerId)) {
            testChannelId = eventManager.getTestChannelId();
        } else {
            testChannelId = deratiseur.getTestChannelId();
        }
        return testChannelId;
    }

    public String getAccessDemandId(String discordServerId) {
        String getAccessDemandId;
        if (Objects.equals(eventManager.getDiscordServerId(), discordServerId)) {
            getAccessDemandId = eventManager.getAccessDemandId();
        } else {
            getAccessDemandId = deratiseur.getAccessDemandId();
        }
        return getAccessDemandId;
    }
}