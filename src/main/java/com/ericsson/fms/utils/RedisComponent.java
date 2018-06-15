package com.ericsson.fms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisComponent {
	@Autowired  
    //操作字符串的template，StringRedisTemplate是RedisTemplate的一个子集  
    private StringRedisTemplate stringRedisTemplate;  
      
    @Autowired  
    // RedisTemplate，可以进行所有的操作    
    private RedisTemplate<Object,Object> redisTemplate;  
      
    public void set(String key, String value){  
        ValueOperations<String, String> ops = this.stringRedisTemplate.opsForValue();  
        boolean bExistent = this.stringRedisTemplate.hasKey(key);  
        if (bExistent) {  
            System.out.println("this key is bExistent!");  
        }else{  
            ops.set(key, value);  
        }  
    }

    public void set(String key, String value,long days){
        ValueOperations<String, String> ops = this.stringRedisTemplate.opsForValue();
        boolean bExistent = this.stringRedisTemplate.hasKey(key);
        if (bExistent) {
            System.out.println("this key="+key+" is bExistent!");
        }else{
            ops.set(key, value, days, TimeUnit.DAYS);
        }
    }

    public String get(String key){  
        return this.stringRedisTemplate.opsForValue().get(key);  
    }  
    
    public void setObject(String key,String hashKey, Object value){  
    	this.redisTemplate.opsForHash().put(key,hashKey, value); 
    }  
      
    public Object getObject(String key,String hashKey){  
        return this.redisTemplate.opsForHash().get(key,hashKey);  
    }  
    
    public void del(String key){  
        this.stringRedisTemplate.delete(key);  
    }  
      
    public void sentinelSet(String key,String value){  
        redisTemplate.opsForValue().set(key, value);  
    }  
      
    public String sentinelGet(String key){  
        return stringRedisTemplate.opsForValue().get(key);  
    } 
}
