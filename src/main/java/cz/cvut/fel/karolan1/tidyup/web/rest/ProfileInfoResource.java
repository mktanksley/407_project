package cz.cvut.fel.karolan1.tidyup.web.rest;

import cz.cvut.fel.karolan1.tidyup.config.JHipsterProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/api")
public class ProfileInfoResource {

    @Inject
    Environment env;

    @Inject
    private JHipsterProperties jHipsterProperties;

    @RequestMapping("/profile-info")
    public ProfileInfoResponse getActiveProfiles() {
        return new ProfileInfoResponse(env.getActiveProfiles());
    }

    class ProfileInfoResponse {

        public String[] activeProfiles;

        ProfileInfoResponse(String[] activeProfiles) {
            this.activeProfiles = activeProfiles;
        }
    }
}
