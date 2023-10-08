package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.SyncTimeSettingDTO;
import com.panda.sport.rcs.console.pojo.LanguageInternation;

import java.util.List;

public interface CommonService {

    List<LanguageInternation> getLanguageInternations(int page ,int pageSize);

    int batchInsertOrUpdate(List<LanguageInternation> languageInternations);

    void updatePlaySetNameCodeLanguageInternation(SyncTimeSettingDTO syncTimeSettingDTO);
}
