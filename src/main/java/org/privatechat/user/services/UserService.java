package org.privatechat.user.services;

import org.privatechat.shared.exceptions.ValidationException;
import org.privatechat.user.DTOs.NotificationDTO;
import org.privatechat.user.DTOs.RegistrationDTO;
import org.privatechat.user.DTOs.UserDTO;
import org.privatechat.user.exceptions.UserNotFoundException;
import org.privatechat.user.interfaces.IUserService;
import org.privatechat.user.mappers.UserMapper;
import org.privatechat.user.models.User;
import org.privatechat.user.repositories.UserRepository;
import org.privatechat.user.strategies.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Component
public class UserService implements UserDetailsService, IUserService {
  private UserRepository userRepository;

  private User currentUser;
  
  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;
  
  @Autowired
  private BeanFactory beanFactory;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  private <T> UserService setUserContext(T userIdentifier, IUserRetrievalStrategy<T> strategy)
      throws UserNotFoundException {
    currentUser = strategy.getUser(userIdentifier);
    
    if (currentUser == null) { throw new UserNotFoundException("User not found."); }
    
    return this;
  }

  public UserService given(String userEmail) throws BeansException, UserNotFoundException {
    setUserContext(userEmail, beanFactory.getBean(UserRetrievalByEmailStrategy.class));
    return this; 
  }
   
  public UserService given(long userId) throws BeansException, UserNotFoundException {
    setUserContext(userId, beanFactory.getBean(UserRetrievalByIdStrategy.class));
    return this; 
  }

  public UserService given(SecurityContext userSecurityContext) throws BeansException, UserNotFoundException {
    setUserContext(userSecurityContext, beanFactory.getBean(UserRetrievalBySecurityContextStrategy.class));
    return this;
  }
  
  @Override
  public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email);

    if (user == null) { return null; }

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
      user.getEmail(),
      user.getPassword(),
      AuthorityUtils.createAuthorityList(user.getRole())
    );

    Authentication authentication = null;
    try {
      authentication = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
      );
    } catch (Exception e) {}

    SecurityContextHolder
      .getContext()
      .setAuthentication(authentication);

    return userDetails;
  }

  public boolean doesUserExist(String email) {
    User user = userRepository.findByEmail(email);

    return user != null;
  }

  public void addUser(RegistrationDTO registrationDTO) throws ValidationException {
    if (this.doesUserExist(registrationDTO.getEmail())) {
      throw new ValidationException("User already exists.");
    }

    String encryptedPassword = new BCryptPasswordEncoder().encode(registrationDTO.getPassword());

    try {
      User user = new User(
        registrationDTO.getEmail(),
        registrationDTO.getFullName(),
        encryptedPassword,
        "STANDARD-ROLE"
      );
      
      userRepository.save(user);
    } catch (ConstraintViolationException e) {
      throw new ValidationException(e.getConstraintViolations().iterator().next().getMessage());
    }
  }

  public List<UserDTO> retrieveFriendsList() {
    List<User> users = userRepository.findFriendsListFor(currentUser.getEmail());

    return UserMapper.mapUsersToUserDTOs(users);
  }
  
  public UserDTO retrieveUserInfo() {
    return new UserDTO(
      currentUser.getId(),
      currentUser.getEmail(),
      currentUser.getFullName()
    );
  }  
  
  // TODO: switch to a TINYINT field called "numOfConnections" to add/subtract
  // the total amount of user connections
  public void setIsPresent(Boolean stat) {
    currentUser.setIsPresent(stat);
     
    userRepository.save(currentUser);
  }

  public Boolean isPresent() {
    return currentUser.getIsPresent(); 
  }

  public void notifyUser(NotificationDTO notification) {
    if (this.isPresent()) {
      simpMessagingTemplate
        .convertAndSend("/topic/user.notification." + currentUser.getId(), notification);
    } else {
      System.out.println("sending email notification to " + currentUser.getFullName());
      // TODO: send email
    }
  }
}