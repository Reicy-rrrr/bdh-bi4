package com.deloitte.bdh.data.collation.mq;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class KafkaSyncDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6748211934352013044L;
	
	
	private String code;
	
	private String groupCode;
	
	private String type;

}
