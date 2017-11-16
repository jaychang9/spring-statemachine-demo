package com.choosefine.statemachine.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.UnsupportedEncodingException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachineContextRepository;
import org.springframework.statemachine.support.DefaultStateMachineContext;

/**
 * @author 张洁
 * @date 2017/11/16
 */
public class MyRedisStateMachineContextRepository<S,E>  implements
    StateMachineContextRepository<S, E, StateMachineContext<S, E>> {

    /**
     * Instantiates a new redis state machine context repository.
     *
     * @param redisConnectionFactory the redis connection factory
     */
    public MyRedisStateMachineContextRepository(RedisConnectionFactory redisConnectionFactory) {
        redisOperations = createDefaultTemplate(redisConnectionFactory);
    }

    private static final ThreadLocal<Gson> gsonThreadLocal = new ThreadLocal<Gson>() {

        @SuppressWarnings("rawtypes")
        @Override
        protected Gson initialValue() {
            Gson gson = new GsonBuilder().create();
            return gson;
        }
    };

    private final RedisOperations<String,byte[]> redisOperations;

    @Override
    public void save(StateMachineContext<S, E> context, String id) {
        redisOperations.opsForValue().set(id, serialize(context));
    }

    @Override
    public StateMachineContext<S, E> getContext(String id) {
        return deserialize(redisOperations.opsForValue().get(id));
    }

    private static RedisTemplate<String,byte[]> createDefaultTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String,byte[]> template = new RedisTemplate<String,byte[]>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }

    private byte[] serialize(StateMachineContext<S, E> context) {
        Gson gson = gsonThreadLocal.get();
        String jsonStr = gson.toJson(context);
        try {
            return jsonStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private StateMachineContext<S, E> deserialize(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        Gson gson = gsonThreadLocal.get();
        try {
            return gson.fromJson(new String(data,"UTF-8"),DefaultStateMachineContext.class);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
