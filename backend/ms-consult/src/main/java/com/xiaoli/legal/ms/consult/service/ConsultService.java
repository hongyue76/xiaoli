package com.xiaoli.legal.ms.consult.service;

import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.ms.consult.model.dto.ChatRequest;
import com.xiaoli.legal.ms.consult.model.dto.ChatResponse;
import com.xiaoli.legal.ms.consult.model.entity.ConsultConversation;
import com.xiaoli.legal.ms.consult.model.entity.ConsultMessage;
import com.xiaoli.legal.ms.consult.model.vo.ConsultRecordVO;
import com.xiaoli.legal.ms.consult.model.vo.ConsultResponse;

import java.util.List;

/**
 * 咨询业务接口
 */
public interface ConsultService {

    /**
     * 保存咨询记录
     */
    ConsultResponse saveConsultRecord(ChatRequest request, ChatResponse response);

    /**
     * 获取咨询历史
     */
    PageResult<ConsultRecordVO> getConsultHistory(Long userId, Long current, Long size);

    /**
     * 获取会话详情
     */
    ConsultRecordVO getConversationDetail(Long conversationId);

    /**
     * 获取会话消息
     */
    List<ConsultMessage> getConversationMessages(Long conversationId);

    /**
     * 创建会话
     */
    ConsultConversation createConversation(ConsultConversation conversation);

    /**
     * 关闭会话
     */
    void closeConversation(Long conversationId);
}
