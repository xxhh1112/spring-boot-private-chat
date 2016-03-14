package org.privatechat.user.controllers;

import java.security.Principal;
import java.util.List;
import org.privatechat.chat.DTOs.ChatMessageDTO;
import org.privatechat.shared.exceptions.ValidationException;
import org.privatechat.shared.http.JSONResponseHelper;
import org.privatechat.user.DTOs.RegistrationDTO;
import org.privatechat.user.DTOs.UserDTO;
import org.privatechat.user.exceptions.UserNotFoundException;
import org.privatechat.user.interfaces.IUserController;
import org.privatechat.user.services.UserService;
import org.privatechat.user.strategies.UserRetrievalBySecurityContextStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController implements IUserController {
  @Autowired
  private UserService userService;
  
  @RequestMapping(value="/api/user/register", method= RequestMethod.POST, produces="application/json", consumes="application/json")
  public ResponseEntity<String> register(@RequestBody RegistrationDTO registeringUser) throws ValidationException {
    userService.addUser(registeringUser);

    return JSONResponseHelper.createResponse("", HttpStatus.OK);
  }

  // TODO: actually implement concept of a "friendslist"
  @RequestMapping(value="/api/user/requesting/friendslist", method=RequestMethod.GET, produces="application/json")
  public ResponseEntity<String> retrieveRequestingUserFriendsList(Principal principal) throws UserNotFoundException {
    List<UserDTO> friendslistUsers = userService.given(SecurityContextHolder.getContext()).retrieveFriendsList();
    
    return JSONResponseHelper.createResponse(friendslistUsers, HttpStatus.OK);
  }

  @RequestMapping(value="/api/user/requesting/info", method=RequestMethod.GET, produces="application/json")
  public ResponseEntity<String> retrieveRequestUserInfo() throws UserNotFoundException {
    UserDTO userDetails = userService.given(SecurityContextHolder.getContext()).retrieveUserInfo();

    return JSONResponseHelper.createResponse(userDetails, HttpStatus.OK);
  }
}