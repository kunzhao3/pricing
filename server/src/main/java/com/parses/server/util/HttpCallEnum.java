package com.parses.server.util;


import lombok.Getter;

@Getter
public enum HttpCallEnum {

	SEND_FAILED("1003", "发送失败"),
	SEND_SUCCESS("1000", "成功"),
	SEND_EXCEPTION("9998", "发送异常"),
	PARSE_EXCEPTION("9999", "解析异常"),
	;

	private final String code;
	private final String desc;

	HttpCallEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

    public static HttpCallEnum getHttpCallEnum(String value) {
		HttpCallEnum[] enums = HttpCallEnum.values();
		for (int i = 0; i < enums.length; i++) {
			if (value.equals(enums[i].getCode())) {
				return enums[i];
			}
		}
		return null;
	}
}
