package org.privatechat.user.interfaces;

import java.util.List;
import org.privatechat.shared.exceptions.ValidationException;
import org.privatechat.user.DTOs.NotificationDTO;
import org.privatechat.user.DTOs.RegistrationDTO;
import org.privatechat.user.DTOs.UserDTO;
import org.privatechat.user.exceptions.UserNotFoundException;
import org.privatechat.user.services.UserService;
import org.springframework.beans.BeansException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

public interface IUserService {
  UserService given(String userEmail) throws BeansException, UserNotFoundException;

  UserService given(long userId) throws BeansException, UserNotFoundException;

  UserService given(SecurityContext userSecurityContext) throws BeansException, UserNotFoundException;

  boolean doesUserExist(String email);
  
  void addUser(RegistrationDTO registrationDTO) throws ValidationException;

  List<UserDTO> retrieveFriendsList();
  
  UserDTO retrieveUserInfo();
  
  void setIsPresent(Boolean stat);
  
  Boolean isPresent();
  
  void notifyUser(NotificationDTO notification);
}