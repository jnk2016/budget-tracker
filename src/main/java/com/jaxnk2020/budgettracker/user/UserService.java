package com.jaxnk2020.budgettracker.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;

@Service
public class UserService {
    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApplicationUser getApplicationUser(Authentication auth) {
        return applicationUserRepository.findByUsername((auth.getName()));  // Obtains the current user
    }

    public boolean newUser(HashMap<String, String> body) {
        ApplicationUser user = applicationUserRepository.findByUsername(body.get("username"));
        if(user == null){
            applicationUserRepository.save(new ApplicationUser(body.get("username"), bCryptPasswordEncoder.encode(body.get("password")),
                                            body.get("firstname"), body.get("lastname"), LocalDate.now()));
            return true;
        }
        else{ return false; }
    }

    public LocalDate getDateJoined(Authentication auth){
        ApplicationUser user = getApplicationUser(auth);
        if(user != null) { return user.getDateJoined(); }
        else{ return null; }
    }
}
