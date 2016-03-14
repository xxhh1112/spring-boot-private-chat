package org.privatechat.chat.interfaces;

import java.util.List;
import org.privatechat.chat.DTOs.ChatChannelInitializationDTO;
import org.privatechat.chat.DTOs.ChatMessageDTO;
import org.privatechat.chat.repositories.ChatChannelRepository;
import org.privatechat.chat.repositories.ChatMessageRepository;
import org.privatechat.user.exceptions.IsSameUserException;
import org.privatechat.user.services.UserService;

public interface IChatService {
  String establishChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO)
    throws IsSameUserException;

  void submitMessage(ChatMessageDTO chatMessageDTO);
  
  List<ChatMessageDTO> getExistingChatMessages(String channelUuid);
}