package discord.bots.calendarlinker.service.GoogleCalendarApi;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class GoogleCalendarACLManager {

    private static final String APPLICATION_NAME = "EventManager";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final String calendarUrl;
    private final Calendar service;
    // For now, I will use only one calendar.
    private final String calendarId;


    public GoogleCalendarACLManager(
            @Value("${calendarUrl}") String calendarUrl,
            @Value("${serviceAccountJSONPath}") String serviceAccountJsonPath,
            @Value("${calendarId}") String calendarId
    ) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT_STATIC = GoogleNetHttpTransport.newTrustedTransport();
        this.calendarUrl = calendarUrl;
        this.calendarId = calendarId;
        InputStream inputStream = GoogleCalendarACLManager.class.getResourceAsStream(serviceAccountJsonPath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        this.service = new Calendar.Builder(HTTP_TRANSPORT_STATIC, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME).build();
    }

    public Acl getAclList() throws IOException {
        // Use the Calendar API to list access rules.
        return this.service.acl().list(this.calendarId).execute();
    }

    public CalendarList getCalendarList() {
        // List the calendars for the authenticated user
        try {
            return this.service.calendarList().list().execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // HOW TO USE
//        List<CalendarListEntry> items = calendarList.getItems();
//
//        System.out.println("Calendars:");
//        for (CalendarListEntry calendar : items) {
//            System.out.println(calendar.getSummary() + " (" + calendar.getId() + ")");
//        }
    }

    public void insertUserInAclRule(String emailOfUser, String role) {
        AclRule rule = new AclRule();
        AclRule.Scope scope = new AclRule.Scope();
        scope.setType("user").setValue(emailOfUser);
        rule.setScope(scope);
        rule.setRole(role);
        try {
            service.acl().insert(this.calendarId, rule).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCalendarUrl() {
        return this.calendarUrl;
    }

    public static boolean creationAclUser(Calendar service, String email, String calendarId, String role) {
        // Create a new ACL rule for the calendar
        AclRule rule = new AclRule();
        rule.setScope(new AclRule.Scope().setType("user").setValue(email));
        rule.setRole(role);

        try {
            service.acl().insert(calendarId, rule).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private static CalendarListEntry createCalendar(Calendar service, String calendarName, String calendarDescription) {
        // Create a new calendar
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary(calendarName);
        calendar.setDescription(calendarDescription);

        com.google.api.services.calendar.model.Calendar createdCalendar = null;
        try {
            createdCalendar = service.calendars().insert(calendar).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.printf("Created calendar with ID: %s\n", createdCalendar.getId());

        try {
            return service.calendarList().get(createdCalendar.getId()).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}