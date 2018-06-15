package com.ericsson.fms.utils;

public class RedisKey {
	/**读KFK消息，ReceiveKFKThread处理完插入，DoBusinessThread等待处理的队列*/
	public static final String R_KFK_MSG = "GIS_MAP_R_KFK_MSG";
	/**写KFK消息，SendKFKMsgThread等待处理的队列*/
	public static final String W_KFK_MSG = "GIS_MAP_W_KFK_MSG";
	/**读Redis消息，DoBusinessThread处理完插入，GoogleApiThread线程等待处理的队列*/
	public static final String R_REDIS_MSG = "GIS_MAP_R_REDIS_MSG";

	/**限速 坐标转换缓存*/
	public static final String SPEED_X_Y = "GIS_SPEED_X_Y_%s";

	/**坐标转换缓存*/
	private static final String X_Y = "GIS_MAP_X_Y_%s";
	
	public static String getRedisKey(String blockId) {
		if(blockId != null) {
			return String.format(RedisKey.X_Y, blockId);
		}
		return null;
	}


	public static String getSpeedRedisKey(double longitude, double latitude, double loblock, double lablock) {
		String key = BlockUtils.getBlockId(longitude, latitude, loblock, lablock);
		return String.format(RedisKey.SPEED_X_Y, key);
	}

	public static String getRedisKey(double longitude, double latitude, double loblock, double lablock) {
		String key = BlockUtils.getBlockId(longitude, latitude, loblock, lablock);
		return String.format(RedisKey.X_Y, key);
	}
	
	public static void main(String[] args){
		String key = getRedisKey(113.315145,24.098997,7.1,7.1);
		System.out.println("redis key:"+key);
		//113.275945,23.11706  GIS_MAP_X_Y_0000016_0000003
		//113.266841,23.128523 GIS_MAP_X_Y_0000016_0000003
		//113.264385,23.12911 GIS_MAP_X_Y_0000016_0000003
		//113.27599525,22.1170553 GIS_MAP_X_Y_0000016_0000003
	}
	
}
