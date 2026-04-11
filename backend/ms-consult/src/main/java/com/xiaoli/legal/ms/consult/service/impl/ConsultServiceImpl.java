package com.xiaoli.legal.ms.consult.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaoli.legal.common.core.cache.RedisCacheConfig;
import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.common.core.exception.BusinessException;
import com.xiaoli.legal.common.core.domain.ResultCode;
import com.xiaoli.legal.ms.consult.mapper.ConsultConversationMapper;
import com.xiaoli.legal.ms.consult.mapper.ConsultMessageMapper;
import com.xiaoli.legal.ms.consult.model.dto.ChatRequest;
import com.xiaoli.legal.ms.consult.model.dto.ChatResponse;
import com.xiaoli.legal.ms.consult.model.entity.ConsultConversation;
import com.xiaoli.legal.ms.consult.model.entity.ConsultMessage;
import com.xiaoli.legal.ms.consult.model.vo.ConsultRecordVO;
import com.xiaoli.legal.ms.consult.model.vo.ConsultResponse;
import com.xiaoli.legal.ms.consult.service.ConsultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 咨询业务实现
 */
@Service
public class ConsultServiceImpl implements ConsultService {

    private static final Logger log = LoggerFactory.getLogger(ConsultServiceImpl.class);

    private final ConsultConversationMapper conversationMapper;
    private final ConsultMessageMapper messageMapper;

    public ConsultServiceImpl(ConsultConversationMapper conversationMapper, ConsultMessageMapper messageMapper) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultResponse saveConsultRecord(ChatRequest request, ChatResponse response) {
        // 获取用户问题
        String userQuestion = "";
        if (request.getMessages() != null) {
            for (ChatRequest.ChatMessage msg : request.getMessages()) {
                if ("user".equals(msg.getRole())) {
                    userQuestion = msg.getContent();
                    break;
                }
            }
        }

        // 获取AI回答
        String aiAnswer = response.getContent();

        // 获取或创建会话
        Long conversationId = null;
        List<ConsultConversation> existingConversations = conversationMapper.selectList(
                new LambdaQueryWrapper<ConsultConversation>()
                        .orderByDesc(ConsultConversation::getCreateTime)
                        .last("LIMIT 1")
        );

        if (!existingConversations.isEmpty()) {
            ConsultConversation conversation = existingConversations.get(0);
            if ("ACTIVE".equals(conversation.getStatus())) {
                conversationId = conversation.getId();
            }
        }

        // 保存用户问题
        if (conversationId == null) {
            // 创建新会话
            ConsultConversation conversation = new ConsultConversation();
            conversation.setUserId(1L); // TODO: 从上下文获取用户ID
            conversation.setTitle(userQuestion.length() > 50 ? userQuestion.substring(0, 50) + "..." : userQuestion);
            conversation.setConversationType("GENERAL");
            conversation.setStatus("ACTIVE");
            conversation.setCreateTime(LocalDateTime.now());
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.insert(conversation);
            conversationId = conversation.getId();
        }

        // 保存用户消息
        ConsultMessage userMessage = new ConsultMessage();
        userMessage.setConversationId(conversationId);
        userMessage.setRole("USER");
        userMessage.setContent(userQuestion);
        userMessage.setMessageType("TEXT");
        userMessage.setCreateTime(LocalDateTime.now());
        messageMapper.insert(userMessage);

        // 保存AI回复
        ConsultMessage aiMessage = new ConsultMessage();
        aiMessage.setConversationId(conversationId);
        aiMessage.setRole("ASSISTANT");
        aiMessage.setContent(aiAnswer);
        aiMessage.setMessageType("TEXT");
        aiMessage.setCreateTime(LocalDateTime.now());
        messageMapper.insert(aiMessage);

        // 更新会话时间
        ConsultConversation conversation = conversationMapper.selectById(conversationId);
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.updateById(conversation);

        // 构建响应
        return ConsultResponse.builder()
                .conversationId(conversationId)
                .answer(aiAnswer)
                .title(conversation.getTitle())
                .build();
    }

    @Override
    public PageResult<ConsultRecordVO> getConsultHistory(Long userId, Long current, Long size) {
        Page<ConsultConversation> page = new Page<>(current, size);
        LambdaQueryWrapper<ConsultConversation> wrapper = new LambdaQueryWrapper<ConsultConversation>()
                .eq(userId != null, ConsultConversation::getUserId, userId)
                .orderByDesc(ConsultConversation::getCreateTime);

        Page<ConsultConversation> result = conversationMapper.selectPage(page, wrapper);

        List<ConsultRecordVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), voList);
    }

    @Override
    public ConsultRecordVO getConversationDetail(Long conversationId) {
        ConsultConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ResultCode.CASE_NOT_EXIST);
        }

        return convertToVO(conversation);
    }

    @Override
    public List<ConsultMessage> getConversationMessages(Long conversationId) {
        return messageMapper.selectList(
                new LambdaQueryWrapper<ConsultMessage>()
                        .eq(ConsultMessage::getConversationId, conversationId)
                        .orderByAsc(ConsultMessage::getCreateTime)
        );
    }

    @Override
    @CachePut(value = RedisCacheConfig.CACHE_CONVERSATION, key = "#result.id")
    public ConsultConversation createConversation(ConsultConversation conversation) {
        conversation.setStatus("ACTIVE");
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    @CacheEvict(value = RedisCacheConfig.CACHE_CONVERSATION, key = "#conversationId")
    public void closeConversation(Long conversationId) {
        ConsultConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setStatus("CLOSED");
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);
        }
    }

    /**
     * 异步保存咨询记录（不阻塞AI对话响应）
     */
    @Async("asyncExecutor")
    @Transactional(rollbackFor = Exception.class)
    public CompletableFuture<ConsultResponse> saveConsultRecordAsync(ChatRequest request, ChatResponse response) {
        return CompletableFuture.completedFuture(saveConsultRecord(request, response));
    }

    private ConsultRecordVO convertToVO(ConsultConversation conversation) {
        ConsultRecordVO vo = new ConsultRecordVO();
        vo.setConversationId(conversation.getId());
        vo.setTitle(conversation.getTitle());
        vo.setConversationType(conversation.getConversationType());
        vo.setStatus(conversation.getStatus());
        vo.setCreateTime(conversation.getCreateTime());
        vo.setUpdateTime(conversation.getUpdateTime());

        // 获取消息列表
        List<ConsultMessage> messages = messageMapper.selectList(
                new LambdaQueryWrapper<ConsultMessage>()
                        .eq(ConsultMessage::getConversationId, conversation.getId())
                        .orderByAsc(ConsultMessage::getCreateTime)
        );

        List<ConsultRecordVO.MessageVO> messageVOs = messages.stream()
                .map(msg -> {
                    ConsultRecordVO.MessageVO messageVO = new ConsultRecordVO.MessageVO();
                    messageVO.setId(msg.getId());
                    messageVO.setRole(msg.getRole());
                    messageVO.setContent(msg.getContent());
                    messageVO.setMessageType(msg.getMessageType());
                    messageVO.setCreateTime(msg.getCreateTime());
                    return messageVO;
                })
                .collect(Collectors.toList());

        vo.setMessages(messageVOs);
        return vo;
    }
}
