package com.danny.test;

import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RecvMQ1
{
	//队列名称
	private final static String QUEUE_NAME = "workqueue";

	public static void main(String[] argv) throws java.io.IOException,
			java.lang.InterruptedException, TimeoutException
	{
		//区分不同工作进程的输出
		int hashCode = RecvMQ1.class.hashCode();
		//创建连接和频道
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		int prefetchCount = 1;  
        channel.basicQos(prefetchCount); 
		//声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(hashCode
				+ " [*] Waiting for messages. To exit press CTRL+C");
		QueueingConsumer consumer = new QueueingConsumer(channel);
		// 指定消费队列
		boolean ack = false ; //打开应答机制
		channel.basicConsume(QUEUE_NAME, ack, consumer);
		while (true)
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());

			System.out.println(hashCode + " [x] Received '" + message + "'");
			doWork(message);
			System.out.println(hashCode + " [x] Done");
			//发送应答
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

		}

	}
	private static void doWork(String task) throws InterruptedException  
    {  
        for (char ch : task.toCharArray())  
        {  
            if (ch == '.')  
                Thread.sleep(1000);  
        }  
    }
}
