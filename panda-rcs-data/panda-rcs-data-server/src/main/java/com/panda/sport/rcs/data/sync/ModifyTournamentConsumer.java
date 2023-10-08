package com.panda.sport.rcs.data.sync;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.panda.merge.bo.StandardSportMarketCategoryBO;
import com.panda.merge.bo.StandardSportOddsFieldsTempletBO;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.IStandardSportTournamentService;
import com.panda.sport.rcs.data.service.RcsLanguageInternationService;
import com.panda.sport.rcs.data.utils.DataCache;
import com.panda.sport.rcs.data.utils.WordsTools;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.pojo.dto.I18nItemDTO;
import com.panda.sport.rcs.pojo.dto.StandardSportTeamDTO;
import com.panda.sport.rcs.pojo.dto.StandardSportTournamentDTO;
import com.panda.sport.rcs.utils.WordToPinYinUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "modify_tournament",
        consumerGroup = "RCS_DATA_MODIFY_TOURNAMENT_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ModifyTournamentConsumer extends RcsConsumer<Request<List<StandardSportTournamentDTO>>> {

    //入库的语言类型
    private static final List<String> saveLanguages= Arrays.asList("zs","zh","en");

    @Autowired
    IStandardSportTournamentService iStandardSportTournamentService;

    @Autowired
    RcsLanguageInternationService rcsLanguageInternationService;

    @Override
    protected String getTopic() {
        return "modify_tournament";
    }

    @Override
    @Trace
    public Boolean handleMs(Request<List<StandardSportTournamentDTO>> dto){
        try {
            List<StandardSportTournamentDTO> sportTournamentDatas = dto.getData();
            List<StandardSportTournament> standardSportTournaments = copyList(StandardSportTournament.class, sportTournamentDatas);
            List<I18nItemDTO> i18nItemBOS = new ArrayList<>();
            Map<String, String> leaguageMap = new HashMap<String, String>();
            for (StandardSportTournamentDTO sportTournamentDatum : sportTournamentDatas) {
                if (!CollectionUtils.isEmpty(sportTournamentDatum.getIl8nNameList())) {
                    Map<String, I18nItemDTO> languageMap = new HashMap<String, I18nItemDTO>();
                    sportTournamentDatum.getIl8nNameList().stream().forEach(nameBean -> {
                        if(Arrays.asList("en","zs","zh").contains(nameBean.getLanguageType())) {
                            languageMap.put(nameBean.getLanguageType(), nameBean);
                        }
                    });
                    i18nItemBOS.addAll(languageMap.values());
//                    i18nItemBOS.addAll(sportTournamentDatum.getIl8nNameList());
                    try {
                        leaguageMap.put(String.valueOf(sportTournamentDatum.getId()), languageMap.containsKey("zs") ? languageMap.get("zs").getText() : languageMap.get("en").getText());
                    }catch (Exception e){
                        log.error( "::{}::{},{},{}","RDMTG_"+dto.getLinkId(),JsonFormatUtils.toJson(sportTournamentDatum),e.getMessage(), e);
                    }
                }
            }
            standardSportTournaments.forEach(bean -> {
                if(leaguageMap.containsKey(String.valueOf(bean.getId()))) {
                    String nameConcate = WordToPinYinUtil.getFirshChar(leaguageMap.get(String.valueOf(bean.getId())));
                    bean.setNameConcat(nameConcate);
                }
            });
            insertLanguageInternation(i18nItemBOS,dto.getLinkId());
            iStandardSportTournamentService.batchInsertOrUpdate(standardSportTournaments);
        } catch (Exception e) {
            log.error( "::{}::{},{},{}","RDMTG_"+dto.getLinkId(),JsonFormatUtils.toJson(dto),e.getMessage(), e);
        }
        return true;
    }

    /**
     * @MethodName:
     * @Description: 插入国际化数据
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/9/28
     **/
    private void insertLanguageInternation(List<I18nItemDTO> il8nMatchPositionList, String linkId)  {
        try {
            Map map = insertLanguageInternationCheck(il8nMatchPositionList,linkId);
            Object newVersion = map.get("newVersion");
            if (null != newVersion) {
                ArrayList<RcsLanguageInternation> rcsLanguageInternations = (ArrayList<RcsLanguageInternation>) newVersion;
                rcsLanguageInternationService.batchInsertOrUpdate(rcsLanguageInternations);
            }
        } catch (Exception e) {
            log.error( "::{}::{},{},{}","RDMTG_"+linkId,JsonFormatUtils.toJson(il8nMatchPositionList),e.getMessage(), e);
        }
    }


    /**
     * 国际化检测
     * @param list
     * @param linkId
     * @return
     */
    private static Map insertLanguageInternationCheck(List<I18nItemDTO> list, String linkId) {
        log.info("::{}::国际化过滤进来size" + list.size(),linkId);
        ArrayList<RcsLanguageInternation> rcsLanguageInternations = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Map<Long, List<I18nItemDTO>> collect = list.stream().filter(a -> saveLanguages.contains(a.getLanguageType())).collect(Collectors.groupingBy(I18nItemDTO::getNameCode));
        Iterator<Map.Entry<Long, List<I18nItemDTO>>> iterator = collect.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<I18nItemDTO>> next = iterator.next();
            Long key = next.getKey();
            try {
                StringBuilder stb = new StringBuilder();
                List<I18nItemDTO> valueList = next.getValue();
                for (I18nItemDTO i18nItemBO : valueList) {
                    stb.append(i18nItemBO.getNameCode())
                            .append(i18nItemBO.getLanguageType())
                            .append(i18nItemBO.getText());
                }
                LinkedHashMap map = transferMapJson(valueList);
                String oMd52 = DataCache.insertCheckCache.getIfPresent(String.valueOf(key));
                String l2 = JsonFormatUtils.toJson(map);
                String nMd52 = DigestUtils.md5DigestAsHex(stb.toString().getBytes());
                if (StringUtils.isBlank(oMd52) || (!oMd52.equals(nMd52))) {
                    RcsLanguageInternation rcsLanguageInternation = new RcsLanguageInternation();
                    rcsLanguageInternation.setNameCode(WordsTools.stringValueOf(key));
                    rcsLanguageInternation.setText(l2);
                    rcsLanguageInternations.add(rcsLanguageInternation);
                }
                DataCache.insertCheckCache.put(String.valueOf(key), nMd52);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("::{}::国际化过滤出去size{}" ,linkId, rcsLanguageInternations.size());
        HashMap<Object, Object> map = new HashMap<>();
        map.put("newVersion", rcsLanguageInternations);
        return map;
    }

    private static LinkedHashMap transferMapJson(List<I18nItemDTO> valueList) {
        LinkedHashMap<Object, Object> objectObjectHashMap = new LinkedHashMap<>();
        for (I18nItemDTO i18nItemBO : valueList) {
            objectObjectHashMap.put(i18nItemBO.getLanguageType(),i18nItemBO.getText());
        }
        return objectObjectHashMap;
    }

    /**
     * @MethodName:
     * @Description: list拷贝
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/30
     **/
    public static <T, E> List<E> copyList(Class<E> classOfE, List<T> list) throws IllegalAccessException, InstantiationException {
        List<E> eLis = new ArrayList<E>();
        for (T t : list) {
            E e = classOfE.newInstance();
            //高速反射值进createTime
            e = BeanCopyUtils.copyProperties(t, classOfE);
            MethodAccess access = MethodAccess.get(classOfE);
            int index = access.getIndex("setCreateTime", Long.class);
            access.invoke(e,index,System.currentTimeMillis());

            try {
                if(t instanceof StandardSportTournamentDTO) {
                    Method method = t.getClass().getMethod("getIl8nNameList");
                    List<I18nItemDTO> i18List = (List<I18nItemDTO>) method.invoke(t);

                    int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                    //区域id
                    Method method2 = t.getClass().getMethod("getStandardSportRegionId");
                    Long regionId = (Long) method2.invoke(t);
                    int code2 = access.getIndex("setRegionId", Long.class);
                    access.invoke(e,code2,regionId);
                }else if(t instanceof StandardSportTeamDTO) {
                    Method method = t.getClass().getMethod("getIl8nNameList");
                    List<I18nItemDTO> i18List = (List<I18nItemDTO>) method.invoke(t);

                    int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                }else if(t instanceof StandardSportOddsFieldsTempletBO) {
                    Method method = t.getClass().getMethod("getI18nNameList");
                    List<I18nItemDTO> i18List = (List<I18nItemDTO>) method.invoke(t);

                    int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                }else if(t instanceof StandardSportMarketCategoryBO) {
                    Method method = t.getClass().getMethod("getI18nNameList");
                    List<I18nItemDTO> i18List = (List<I18nItemDTO>) method.invoke(t);

                    int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                }
            }catch (Exception e1) {
                log.warn("::{}::{}{}","RDMTG_",e1.getMessage(),e1);
            }
            eLis.add(e);
        }
        return eLis;
    }

    private static Long getNameCode(List<I18nItemDTO> il8nNameList) {
        if(il8nNameList != null && il8nNameList.size() > 0 ) {
            return il8nNameList.get(0).getNameCode();
        }
        return null;
    }
}
