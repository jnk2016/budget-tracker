package com.jaxnk2020.budgettracker.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /** Register a new account */
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody HashMap<String, String> body) {
        if(body.keySet().size() == 4 && !body.containsValue(null) && !(body.containsKey("username") && body.containsKey("password") && body.containsKey("firstname") && body.containsKey("lastname"))){
            return ResponseEntity.badRequest().body("Request body is incorrect!");
        }

        if(userService.newUser(body)) {
            return ResponseEntity.ok("Your account has been registered!");
        }
        else {
            return ResponseEntity.badRequest().body("This username already exists. Please choose a different username.");
        }
    }

    /** Get the water intake for the user */
    @GetMapping("/joined")
    public ResponseEntity<HashMap<String,String>> dateJoined(Authentication auth) {
        LocalDate date = userService.getDateJoined(auth);
        HashMap<String, String> response = new HashMap<>();
        if(date != null){
            response.put("Date Joined", date.toString());
            return ResponseEntity.ok(response);
        }
        else {
            response.put("Error", "Bad authentication credentials!");
            return ResponseEntity.badRequest().body(response);
        }
    }
}