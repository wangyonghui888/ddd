package com.panda.sport.rcs.data.sync;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.panda.merge.bo.StandardSportMarketCategoryBO;
import com.panda.merge.bo.StandardSportOddsFieldsTempletBO;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.RcsLanguageInternationService;
import com.panda.sport.rcs.data.service.RcsStandardSportPlayerService;
import com.panda.sport.rcs.data.utils.DataCache;
import com.panda.sport.rcs.data.utils.RcsDataRedis;
import com.panda.sport.rcs.data.utils.WordsTools;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.pojo.RcsStandardSportPlayer;
import com.panda.sport.rcs.pojo.dto.I18nItemDTO;
import com.panda.sport.rcs.pojo.dto.StandardSportPlayerDTO;
import com.panda.sport.rcs.pojo.dto.StandardSportTeamDTO;
import com.panda.sport.rcs.pojo.dto.StandardSportTournamentDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
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
        topic = "modify_player",
        consumerGroup = "RCS_DATA_MODIFY_PLAYER_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ModifyPlayerConsumer extends RcsConsumer<Request<StandardSportPlayerDTO>> {

    //入库的语言类型
    private static final List<String> saveLanguages= Arrays.asList("zs","zh","en");

    public static final String playerNameKey = "rcs:baseData:playearLanguage:%s";

    @Autowired
    RcsStandardSportPlayerService rcsStandardSportPlayerService;
    @Autowired
    RcsLanguageInternationService rcsLanguageInternationService;

    @Autowired
    RcsDataRedis rcsDataRedis;

    @Override
    protected String getTopic() {
        return "modify_player";
    }

    @Override
    public Boolean handleMs(Request<StandardSportPlayerDTO> dto) {
        try {
            log.info("::{}::","RDMPG_"+dto.getLinkId()+"_"+dto.getData().getId());
            StandardSportPlayerDTO data = dto.getData();
            List<I18nItemDTO> i18nItemBOS = new ArrayList<>();
            if(!CollectionUtils.isEmpty(data.getIl8nNameList())){
                i18nItemBOS.addAll(data.getIl8nNameList());
            }
            ArrayList<StandardSportPlayerDTO> datas = new ArrayList<>();
            datas.add(data);
            List<RcsStandardSportPlayer> standardSportTournaments = copyList(RcsStandardSportPlayer.class, datas);
            rcsStandardSportPlayerService.batchInsertOrUpdate(standardSportTournaments);
            insertLanguageInternation(i18nItemBOS,dto.getLinkId());
        } catch (Exception e) {
            log.error( "::{}::{},{},{}","RDMPG_"+dto.getLinkId(),e.getMessage(), e);
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
            log.error("::{}::{},{},{}","RDMPG_"+linkId,JsonFormatUtils.toJson(il8nMatchPositionList),e.getMessage(), e);
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
        log.info("::{}::国际化过滤出去size{}" ,linkId,rcsLanguageInternations.size());
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
                log.warn(e1.getMessage(),e1);
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
