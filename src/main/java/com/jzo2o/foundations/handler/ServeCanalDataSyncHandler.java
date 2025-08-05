package com.jzo2o.foundations.handler;

import com.jzo2o.canal.listeners.AbstractCanalRabbitMqMsgListener;
import com.jzo2o.es.core.ElasticSearchTemplate;
import com.jzo2o.foundations.model.domain.ServeSync;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 实现cannal 同步数据
 */
@Component
public class ServeCanalDataSyncHandler extends AbstractCanalRabbitMqMsgListener<ServeSync> {

    @Resource
    private ElasticSearchTemplate elasticSearchTemplate;
    //监听mq
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "canal-mq-jzo2o-foundations",arguments={@Argument(name="x-single-active-consumer", value = "true", type = "java.lang.Boolean") }),
            exchange = @Exchange(name = "exchange.canal-jzo2o",type = ExchangeTypes.TOPIC),
            key = "canal-mq-jzo2o-foundations"

    ),concurrency = "1")//concurrency 指定消费线程个数为1
    public void onMessage(Message message) throws Exception {
        // 调用抽象类中的方法
        parseMsg(message);
    }
    /**
     * 向es中保存数据，解析到binlog中的新增，更新消息执行此方法
     * @param data
     */
    @Override
    public void batchSave(List<ServeSync> data) {
        //向es中保存索引，有索引则更新索引，没有索引则添加索引
        Boolean serve_aggregation = elasticSearchTemplate.opsForDoc().batchInsert("serve_aggregation", data);
        //如果执行失败，要抛出异常，给mq回nack，此时消息仍然在mq里面
        if (!serve_aggregation) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("向es中保存数据失败");
        }
    }

    /**
     * 向es中删除数据，解析到binlog中的删除消息执行此方法
     * @param ids
     */
    @Override
    public void batchDelete(List<Long> ids) {
        Boolean serve_aggregation = elasticSearchTemplate.opsForDoc().batchDelete("serve_aggregation", ids);
        //如果执行失败，要抛出异常，给mq回nack，此时消息仍然在mq里面
        if (!serve_aggregation) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("向es中保存数据失败");
        }
    }


}
