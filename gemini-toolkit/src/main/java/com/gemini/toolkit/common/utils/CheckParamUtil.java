package com.gemini.toolkit.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.gemini.toolkit.base.BaseEntity;
import org.apache.commons.lang.StringUtils;

import com.gemini.toolkit.sysparam.entity.TCodeEntity;

/**
*
* @author BHH
*
*/
public class CheckParamUtil {
	
	private static final String BASE_CODE_STRING = "0123456789ABCDEFGHJKLMNPQRTUWXY";
    private static final char[] BASE_CODE_ARRAY = BASE_CODE_STRING.toCharArray();
    private static final List<Character> BASE_CODES = new ArrayList<Character>();
    private static final String BASE_CODE_REGEX = "[" + BASE_CODE_STRING + "]{18}";
    private static final int[] WEIGHT = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};

    static {
        for (char c : BASE_CODE_ARRAY) {
            BASE_CODES.add(c);
        }
    }
    
	
	/**
	 * 0:check 通过
	 * 1:null错误
	 * 2:长度超出
	 * @param TCodeEntity
	 * @return
	 */
	public static R checkTCode(TCodeEntity tCodeEntity) {
		
		if(tCodeEntity.getBusId() != null) {
			if(tCodeEntity.getBusId().length() > 256) {
				return R.error(2, CommonConsts.BUS_ID);
			}
		}
		
		if(tCodeEntity.getCodeType() == null || StringUtils.isEmpty(tCodeEntity.getCodeType().trim())) {
			return R.error(1, CommonConsts.CODE_TYPE);
		}else {
			if(tCodeEntity.getCodeType().length() > 64) {
				return R.error(2, CommonConsts.CODE_TYPE);
			}
		}
		
		if(tCodeEntity.getCodeId() == null || StringUtils.isEmpty(tCodeEntity.getCodeId().trim())) {
			return R.error(1, CommonConsts.CODE_ID);
		}else {
			if(tCodeEntity.getCodeId().length() > 64) {
				return R.error(2, CommonConsts.CODE_ID);
			}
		}
		
		if(tCodeEntity.getCodeName() == null || StringUtils.isEmpty(tCodeEntity.getCodeName().trim())) {
			return R.error(1, CommonConsts.CODE_NAME);
		}else {
			if(tCodeEntity.getCodeName().length() > 128) {
				return R.error(2, CommonConsts.CODE_NAME);
			}
		}
		
		if(tCodeEntity.getCodeRnm() != null) {
			if(tCodeEntity.getCodeRnm().length() > 128) {
				return R.error(2, CommonConsts.CODE_RNM);
			}
		}
		
		if(tCodeEntity.getCodeValue1() != null) {
			if(tCodeEntity.getCodeValue1().length() > 3000) {
				return R.error(2, CommonConsts.CODE_VALUE1);
			}
		}
		
		if(tCodeEntity.getCodeValue2() != null) {
			if(tCodeEntity.getCodeValue2().length() > 128) {
				return R.error(2, CommonConsts.CODE_VALUE2);
			}
		}
		
		if(tCodeEntity.getCodeValue3() != null) {
			if(tCodeEntity.getCodeValue3().length() > 128) {
				return R.error(2, CommonConsts.CODE_VALUE3);
			}
		}
		
		if(tCodeEntity.getCodeValue4() != null) {
			if(tCodeEntity.getCodeValue4().length() > 128) {
				return R.error(2, CommonConsts.CODE_VALUE4);
			}
		}
		
		if(tCodeEntity.getCodeValue5() != null) {
			if(tCodeEntity.getCodeValue5().length() > 128) {
				return R.error(2, CommonConsts.CODE_VALUE5);
			}
		}
		
		if(tCodeEntity.getCodeRemark() != null) {
			if(tCodeEntity.getCodeRemark().length() > 768) {
				return R.error(2, CommonConsts.CODE_REMARK);
			}
		}
		
		return checkBase(tCodeEntity);
		
	}
	
	public static R checkBase(BaseEntity baseEntity) {
		if(baseEntity.getPreparation1() != null) {
			if(baseEntity.getPreparation1().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION1);
			}
		}
		
		if(baseEntity.getPreparation2() != null) {
			if(baseEntity.getPreparation2().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION2);
			}
		}
		
		if(baseEntity.getPreparation3() != null) {
			if(baseEntity.getPreparation3().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION3);
			}
		}
		
		if(baseEntity.getPreparation4() != null) {
			if(baseEntity.getPreparation4().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION4);
			}
		}
		
		if(baseEntity.getPreparation5() != null) {
			if(baseEntity.getPreparation5().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION5);
			}
		}
		
		if(baseEntity.getPreparation6() != null) {
			if(baseEntity.getPreparation6().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION6);
			}
		}
		
		if(baseEntity.getPreparation7() != null) {
			if(baseEntity.getPreparation7().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION7);
			}
		}
		
		if(baseEntity.getPreparation8() != null) {
			if(baseEntity.getPreparation8().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION8);
			}
		}
		
		if(baseEntity.getPreparation9() != null) {
			if(baseEntity.getPreparation9().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION9);
			}
		}
		
		if(baseEntity.getPreparation10() != null) {
			if(baseEntity.getPreparation10().length() > 384) {
				return R.error(2, CommonConsts.PREPARATION10);
			}
		}
		
		if(baseEntity.getDeleteFlg() != null) {
			if(baseEntity.getDeleteFlg().length() > 1) {
				return R.error(2, CommonConsts.DELETE_FLG);
			}
		}
		
		if(baseEntity.getUpdateKey() == null) {
			return R.error(1, CommonConsts.UPDATE_KEY);
		}
		
		return R.ok();
	}
	
	/**
	 * 非空check
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null)
			return true;
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).size() == 0;
		}
		if ("".equals(obj.toString().trim()))
			return true;
		return false;
	}

	/**
	 * 手机号check
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkMobile(String value) {
		String mobile = "(^0?[1][0-9]{10}$)";
		if (!value.matches(mobile)) {
			return false;
		}
		return true;
	}

	/**
	 * 固定电话check
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkTel(String value) {
		String tel = "^(((0[1-9]{3})?(0[12][0-9])?[-])?\\d{6,8})?(0?[1][35678][0-9]{9})?(-\\d{1,6})?$";
		if (!value.matches(tel)) {
			return false;
		}
		return true;
	}

	/**
	 * 邮箱check
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkMail(String value) {
		String mail = "\\w{1,}[@][\\w\\-]{1,}([.]([\\w\\-]{1,})){1,3}$";
		if (!value.matches(mail)) {
			return false;
		}
		return true;
	}

	/**
	 * 日期check
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkDate(String value) {
		String date ="\\d{4}/\\d{1,2}/\\d{1,2}";
		if (!value.matches(date)) {
			return false;
		}
		return true;
	}

	/**
	 * 统一社会信用代码check
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkSocialCreditCode(String value) {
        if (StringUtils.isBlank(value) || !Pattern.matches(BASE_CODE_REGEX, value)) {
            return false;
        }
        char[] businessCodeArray = value.toCharArray();
        char check = businessCodeArray[17];
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char key = businessCodeArray[i];
            sum += (BASE_CODES.indexOf(key) * WEIGHT[i]);
        }
        int socialCreditCode = 31 - sum % 31;
        return check == BASE_CODE_ARRAY[socialCreditCode % 31];
	}

	/**
	 * 传真check
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkFax(String value) {
		String fax = "^[a-zA-Z0-9\\(\\)\\-\\.|,+_&\\/ ]+$";
		if (!value.matches(fax)) {
			return false;
		}
		return true;
	}

	/**
	 * 邮编check
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkZipCode(String value) {
		String zipCode = "^[a-zA-Z0-9\\(\\)\\-\\.|_&\\/ ]+$";
		if (!value.matches(zipCode)) {
			return false;
		}
		return true;
	}

	/**
	 * 长度check
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkLength(String length, String value) {
		if (!isEmpty(value)) {
			if (value.length() > Integer.parseInt(length)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * excel导入check
	 * 
	 * @param
	 * @return
	 */
	public static boolean valueCheck(String checkType, String value) {
		if (!isEmpty(value)) {
			if (CommonConsts.SOCIALCREDITCODE.equals(checkType)) {
				if (!checkSocialCreditCode(value)) {
					return false;
				}
			}
			if (CommonConsts.ZIPCODE.equals(checkType)) {
				if (!checkZipCode(value)) {
					return false;
				}
			}
			if (CommonConsts.MOBILE.equals(checkType)) {
				if (!checkMobile(value)) {
					return false;
				}
			}
			if (CommonConsts.TEL.equals(checkType)) {
				if (!checkTel(value)) {
					return false;
				}
			}
			if (CommonConsts.MAIL.equals(checkType)) {
				if (!checkMail(value)) {
					return false;
				}
			}
//			if (CommonConsts.DATE.equals(checkType)) {
//				if (!checkDate(value)) {
//					return false;
//				}
//			}
			if (CommonConsts.FAX.equals(checkType)) {
				if (!checkFax(value)) {
					return false;
				}
			}
		}
		return true;
	}
}
