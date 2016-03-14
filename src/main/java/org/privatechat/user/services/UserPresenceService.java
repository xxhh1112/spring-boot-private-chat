package org.privatechat.user.services;

import org.privatechat.user.exceptions.UserNotFoundException;
import org.privatechat.user.interfaces.IUserPresenceService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

@Component
public class UserPresenceService extends ChannelInterceptorAdapter implements IUserPresenceService {
  @Autowired 
  private UserService userService;
 
  @Override
  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    StompHeaderAccessor stompDetails = StompHeaderAccessor.wrap(message);
                
    if(stompDetails.getCommand() == null) { return; }

    switch(stompDetails.getCommand()) {
      case CONNECT:    
      case CONNECTED:
        toggleUserPresence(stompDetails.getUser().getName().toString(), true);
        break;
      case DISCONNECT:
        toggleUserPresence(stompDetails.getUser().getName().toString(), false);
        break;
      default:
        break;
    }
  }
  
  private void toggleUserPresence(String userEmail, Boolean isPresent) {
    try {
      userService.given(userEmail).setIsPresent(isPresent);
	  } catch (BeansException | UserNotFoundException e) {
	    e.printStackTrace();
	  }
  }
}