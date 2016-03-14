package org.privatechat.chat.services;

import org.privatechat.chat.DTOs.ChatChannelInitializationDTO;
import org.privatechat.chat.DTOs.ChatMessageDTO;
import org.privatechat.chat.interfaces.IChatService;
import org.privatechat.chat.mappers.ChatMessageMapper;
import org.privatechat.chat.models.ChatChannel;
import org.privatechat.chat.models.ChatMessage;
import org.privatechat.chat.repositories.ChatChannelRepository;
import org.privatechat.chat.repositories.ChatMessageRepository;
import org.privatechat.user.DTOs.NotificationDTO;
import org.privatechat.user.exceptions.IsSameUserException;
import org.privatechat.user.exceptions.UserNotFoundException;
import org.privatechat.user.services.UserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import java.util.List;

@Service
public class ChatService implements IChatService {
  private ChatChannelRepository chatChannelRepository;

  private ChatMessageRepository chatMessageRepository;

  private UserService userService;
  
  private final int MAX_PAGABLE_CHAT_MESSAGES = 100;

  @Autowired
  public ChatService(
      ChatChannelRepository chatChannelRepository,
      ChatMessageRepository chatMessageRepository,
      UserService userService) {
    this.chatChannelRepository = chatChannelRepository;
    this.chatMessageRepository = chatMessageRepository;
    this.userService = userService;
  }

  private String getExistingChannel(ChatChannelInitializationDTO chatChannelInitializationDTO) {
    List<ChatChannel> channel = chatChannelRepository
      .findExistingChannel(
        chatChannelInitializationDTO.getUserIdOne(),
        chatChannelInitializationDTO.getUserIdTwo()
      );
    
    return (channel != null && !channel.isEmpty()) ? channel.get(0).getUuid() : null;
  }

  private String newChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO) {
    ChatChannel channel = new ChatChannel(
      chatChannelInitializationDTO.getUserIdOne(),
      chatChannelInitializationDTO.getUserIdTwo()
    );
    
    chatChannelRepository.save(channel);

    return channel.getUuid();
  }

  public String establishChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO)
      throws IsSameUserException {
    if (chatChannelInitializationDTO.getUserIdOne() == chatChannelInitializationDTO.getUserIdTwo()) {
      throw new IsSameUserException();
    }

    String uuid = getExistingChannel(chatChannelInitializationDTO);
  
    return (uuid != null) ? uuid : newChatSession(chatChannelInitializationDTO);
  }
  
  public void submitMessage(ChatMessageDTO chatMessageDTO) {
    ChatMessage chatMessage = ChatMessageMapper.mapChatDTOtoMessage(chatMessageDTO);

    chatMessageRepository.save(chatMessage);
    
    try {
      String fromUserFullName = userService.given(
        chatMessage.getAuthorUserId()).retrieveUserInfo().getFullName();
        
      userService.given(chatMessage.getRecipientUserId())
        .notifyUser(
          new NotificationDTO(
            "ChatMessageNotification",
            fromUserFullName + " has sent you a message",
            chatMessage.getAuthorUserId()
          )
        );
    } catch (UserNotFoundException | BeansException e) {
      e.printStackTrace();
    }
  }
 
  public List<ChatMessageDTO> getExistingChatMessages(String channelUuid) {
    ChatChannel channel = chatChannelRepository.getChannelDetails(channelUuid);
    
    List<ChatMessage> chatMessages = 
      chatMessageRepository.getExistingChatMessages(
        channel.getUserIdOne(),
        channel.getUserIdTwo(),
        new PageRequest(0, MAX_PAGABLE_CHAT_MESSAGES)
      );
    
    List<ChatMessage> messagesByLatest = Lists.reverse(chatMessages); 
    
    return ChatMessageMapper.mapMessagesToChatDTOs(messagesByLatest);
  }
}