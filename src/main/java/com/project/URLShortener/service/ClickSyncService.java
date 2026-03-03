package com.project.URLShortener.service;


import com.project.URLShortener.repository.UrlRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ClickSyncService {


    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UrlRespository urlRespository;

    @Scheduled(fixedRate = 60000)
    public void syncClicks()
    {
        Set<String> keys=redisTemplate.keys("clicks:*");
        if(keys==null ||keys.isEmpty())
        {
            return;
        }
        for(String key:keys)
        {
            String shortUrl=key.replace("clicks:","");
            String countStr=redisTemplate.opsForValue().get(key);
            if(countStr==null)
            {
                continue;
            }
            long count=Long.parseLong(countStr);
            urlRespository.incrementClickCountBy(shortUrl,count);
            redisTemplate.delete(key);
        }
    }
}
