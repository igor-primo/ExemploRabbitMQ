package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Calendar;
import java.util.TimeZone;

public class Emissor {

//private final static String QUEUE_NAME = "";

	public static void sendMessage(
		String host,
		String username,
		String password,
		String destination,
		String message
	) throws Exception 

	{

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host); // Alterar
		factory.setUsername(username); // Alterar
		factory.setPassword(password); // Alterar
		factory.setVirtualHost("/");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		TimeZone tz = TimeZone.getTimeZone("GMT-3");

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(tz);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH)+1;
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		String realMessage = "("+day+"/"+month+"/"+year+" às "+hour+":"+minute+")"+" "+username+" diz: " +message;

		channel.queueDeclare(destination, false,   false,     false,       null);
		channel.basicPublish("", destination, null,  realMessage.getBytes("UTF-8"));

		channel.close();
		connection.close();
	}

}
