package com.xiaoli.legal.ms.legalcase.service;

import com.xiaoli.legal.ms.legalcase.model.vo.JudgeProfileVO;

/**
 * 法官画像分析服务接口
 */
public interface JudgeProfileService {

    /**
     * 分析法官画像
     *
     * @param judgeName 法官姓名
     * @param caseType  案件类型(可选)
     * @return 法官画像分析结果
     */
    JudgeProfileVO analyzeJudgeProfile(String judgeName, String caseType);
}
