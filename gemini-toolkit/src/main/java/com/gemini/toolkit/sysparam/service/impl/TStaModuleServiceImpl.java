package com.gemini.toolkit.sysparam.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.gemini.toolkit.common.exception.PgInputCheckException;
import com.gemini.toolkit.common.utils.CommonConsts;
import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.sysparam.entity.TStaModuleEntity;
import com.gemini.toolkit.sysparam.mapper.TStaModuleMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gemini.toolkit.sysparam.dto.StaModuleTree;
import com.gemini.toolkit.sysparam.service.TStaModuleService;

/**
 * <p>
 * 树code 服务实现类
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */
@Service
public class TStaModuleServiceImpl extends ServiceImpl<TStaModuleMapper, TStaModuleEntity> implements TStaModuleService {
	
	@Autowired
	private MessageSource messageSource;
	
	/**
	 * 获取树
	 * @param sysName
	 * @return
	 */
	public R getTreeCode(String sysName) {
		if(StringUtils.isEmpty(sysName.trim())) {
			throw new PgInputCheckException("sysparam.config.sysname",
					messageSource.getMessage("sysparam.config.sysname", null, LocaleContextHolder.getLocale()));
		}
		sysName = "%" + sysName + "%";
		List<TStaModuleEntity> tStaModuleEntityList= baseMapper.getTreeCode(sysName);
		List<StaModuleTree> staModuleTree = makeTree(tStaModuleEntityList);
		return R.ok().put("data", staModuleTree);
	}
	
	/**
	 * 获取busId
	 * @param sysName
	 * @return
	 */
	public String getTreeCodeString(String sysName) {
		String busId = "";
		List<TStaModuleEntity> tStaModuleEntityList = baseMapper.getTreeCode("%" + sysName + "%");
		if(tStaModuleEntityList.size() <= 0) {
			return "('--')";
		}
		for(int i=0; i<tStaModuleEntityList.size(); i++) {
			if(i==0) {
				busId = "('" + tStaModuleEntityList.get(i).getBusId() + "'";
			}else {
				busId = busId + ",'" + tStaModuleEntityList.get(i).getBusId() + "'";
			}
		}
		busId = busId + ")";
		
		return busId;
	}
	
	/**
	 * 生成树
	 * @param tStaModuleEntityList
	 * @return
	 */
	private List<StaModuleTree> makeTree(List<TStaModuleEntity> tStaModuleEntityList) {
		List<StaModuleTree> staModuleTreeList = new ArrayList<StaModuleTree>();
		StaModuleTree staModuleTree = new StaModuleTree();
		if(tStaModuleEntityList == null || tStaModuleEntityList.size() == 0) {
			staModuleTreeList.add(staModuleTree);
			return staModuleTreeList;
		}else {
			staModuleTree.setBusId(CommonConsts.ALLTEXT);
			staModuleTree.setLabel(messageSource.getMessage("sysparam.label.all", null, LocaleContextHolder.getLocale()));
			for(TStaModuleEntity tStaModuleEntity:tStaModuleEntityList) {
				StaModuleTree children = new StaModuleTree();
				children.setBusId(tStaModuleEntity.getBusId());
				children.setLabel(tStaModuleEntity.getBusName());
				staModuleTree.addChild(children);
			}
			staModuleTreeList.add(staModuleTree);
			return staModuleTreeList;
		}
	}
}
