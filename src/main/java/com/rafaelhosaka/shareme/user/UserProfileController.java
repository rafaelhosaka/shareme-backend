package com.rafaelhosaka.shareme.user;

import com.rafaelhosaka.shareme.exception.PostNotFoundException;
import com.rafaelhosaka.shareme.exception.UserProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("api/user")
@Slf4j
public class UserProfileController {

    private UserProfileService userService;

    @Autowired
    public UserProfileController(UserProfileService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<UserProfile> getUserProfiles(){
        return userService.getUserProfiles();
    }

    @GetMapping("/{email:.+}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable("email") String email){
        try {
            return ResponseEntity.ok().body(userService.getUserProfileByEmail(email));
        }catch (UserProfileNotFoundException e){
            log.error("Exception : {}",e.getMessage());
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<UserProfile> saveUserProfile(@RequestBody UserProfile userProfile){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        try{
            return ResponseEntity.created(uri).body(userService.save(userProfile));
        }catch(IllegalStateException e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPostImage(@PathVariable("id") String id)  {
        try {
            return ResponseEntity.ok().body(Base64.getEncoder().encode(userService.downloadProfileImage(id)));
        }catch (Exception e){
            log.error("Exception : {}",e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

}
