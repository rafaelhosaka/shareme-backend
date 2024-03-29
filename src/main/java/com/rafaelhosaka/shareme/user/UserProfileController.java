package com.rafaelhosaka.shareme.user;

import com.rafaelhosaka.shareme.email.OnRegistrationCompleteEvent;
import com.rafaelhosaka.shareme.exception.UserProfileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("api/user")
@Slf4j
public class UserProfileController {

    private UserProfileService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserProfileController(UserProfileService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.eventPublisher = applicationEventPublisher;
    }

    @GetMapping("/all")
    public List<UserProfile> getUserProfiles(){
        return userService.getUserProfiles();
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<UserProfile>> searchUsersContainsName(@RequestParam("query") String searchedName) {
        return ResponseEntity.ok(userService.searchUsersContainsName(searchedName));
    }

    @GetMapping("/email/{email:.+}")
    public ResponseEntity<UserProfile> getUserByEmail(@PathVariable("email") String email){
        try {
            return ResponseEntity.ok().body(userService.getUserProfileByEmail(email));
        }catch (UserProfileNotFoundException e){
            log.error("Exception : {}",e.getMessage());
            return new ResponseEntity(
                    e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserProfile> getUserById(@PathVariable("id") String id){
        try {
            return ResponseEntity.ok().body(userService.getUserProfileById(id));
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
            UserProfile user = userService.save(userProfile);
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user,userProfile.getLanguagePreference()));
            return ResponseEntity.created(uri).body(user);
        }catch(IllegalStateException e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<List<String>> downloadProfileUserImage(@PathVariable("id") String id)  {
        try {
            return ResponseEntity.ok().body(userService.downloadProfileImage(id));
        }catch (Exception e){
            log.error("Exception : {}",e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/downloadCoverImage/{id}")
    public ResponseEntity<List<String>> downloadCoverImage(@PathVariable("id") String id)  {
        try {
            return ResponseEntity.ok().body(userService.downloadCoverImage(id));
        }catch (Exception e){
            log.error("Exception : {}",e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping(
            path = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<UserProfile> uploadProfileUserImage(@RequestPart("userId") String userId, @RequestPart(value = "file") MultipartFile file) {
        try {
            return ResponseEntity.ok().body(userService.uploadProfileImage(userId, file));
        } catch (Exception e) {
            log.error("Exception : {}",e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(
            path = "/uploadCoverImage",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<UserProfile> uploadCoverImage(@RequestPart("userId") String userId, @RequestPart(value = "file") MultipartFile file) {
        try {
            return ResponseEntity.ok().body(userService.uploadCoverImage(userId, file));
        } catch (Exception e) {
            log.error("Exception : {}",e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("getUsersFromIds")
    public ResponseEntity<List<UserProfile>> getUsersFromIds(@RequestBody List<String> ids){
        return ResponseEntity.ok(userService.getUserProfileFromIds(ids));
    }

    @GetMapping("/{id}/getUserFriend")
    public ResponseEntity<List<UserProfile>> getUserFriends(@PathVariable("id") String id){
        try {
            return ResponseEntity.ok(userService.getUserFriends(id));
        } catch (UserProfileNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserProfile> updateUser(@RequestBody UserProfile user){
        return ResponseEntity.ok(userService.update(user));
    }

}
