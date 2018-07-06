package com.jit.sensor.api;



import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.base.utils.ReturnStr;
import com.jit.sensor.model.Universaldata;
import com.jit.sensor.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequestMapping("redis")
public class RedisController {

//	@Autowired
//	private StringRedisTemplate strRedis;
//	@Autowired
//	private RedisKeyValueTemplate rkvt;
//
//
//	@RequestMapping("/test1")
//	public String test1() {
//		ReturnStr returnStr = new ReturnStr();
//		strRedis.opsForValue().set("cache2", "2redis !!!");
//		return returnStr.setTMessage(1,"成功",strRedis.opsForValue().get("cache"));
//		//return ResultUtil.success(strRedis.opsForValue().get("cache"));
//	}
//
//	@RequestMapping("/test2")
//	public String test2() {
//		ReturnStr returnStr = new ReturnStr();
//		Universaldata universaldata = new Universaldata();
//		universaldata.setTime(String.valueOf(new Date()));
//		universaldata.setDevtype("2");
//		universaldata.setDeveui("12345");
//		universaldata.setData("haidongdong");
//		System.out.println(universaldata.toString());
//		String key = universaldata.getDeveui()+"-"+universaldata.getDevtype();
//		System.out.println(key);
//		//rkvt.insert(key,"12345");
//		User user = new User();
//		user.setCeshi("123");
//		user.setHaidongdong("789");
//		System.out.println(user.toString());
//		rkvt.insert(1,user);
//		return returnStr.setTMessage(1,"成功", JSONObject.toJSONString(rkvt.findById(key, Universaldata.class)));
//	}
//

	
}
