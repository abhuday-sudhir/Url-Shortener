package com.project.URLShortener.service;

import com.project.URLShortener.entity.UrlMapping;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.project.URLShortener.repository.UrlRespository;
import com.project.URLShortener.utils.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class ShortenerService {

    private Base62Encoder base62Encoder;

    @Autowired
    private UrlRespository urlRespository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String shorten(String longURL,String alias)
    {
        if(alias!=null && !alias.isEmpty())
        {
            if(urlRespository.existsByShortCode(alias))
            {
                throw new RuntimeException("Alias already exists");
            }

            UrlMapping urlMapping= UrlMapping.builder()
                    .longurl(longURL)
                    .shortCode(alias.toLowerCase())
                    .createdAt(Instant.now())
                    .build();
            try
            {
                urlRespository.save(urlMapping);
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Alias already exists");
            }
            return urlMapping.getShortCode();
        }

        UrlMapping urlMapping= UrlMapping.builder()
                .longurl(longURL)
                .createdAt(Instant.now())
                .build();

        urlMapping=urlRespository.save(urlMapping);

        String shortCode = Base62Encoder.encode(urlMapping.getId());

        urlMapping.setShortCode(shortCode);

        urlRespository.save(urlMapping);

        return shortCode;

    }

    public String redirect(String shortUrl)
    {
        String key="url:"+shortUrl;
        String clickKey="clicks:"+shortUrl;
        String url = redisTemplate.opsForValue().get(key);

        if(url!=null)
        {
            redisTemplate.opsForValue().increment(clickKey);
            return url;
        }

        UrlMapping urlMapping =urlRespository.findByShortCode(shortUrl).orElseThrow(() -> new RuntimeException("Short Url not found"));

        String longURL=urlMapping.getLongurl();

        redisTemplate.opsForValue().set(key,longURL,24, TimeUnit.HOURS);
        redisTemplate.opsForValue().increment(clickKey);

        return urlMapping.getLongurl();

    }
}
