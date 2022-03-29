package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.Scanner;
import br.ufs.dcomp.ExemploRabbitMQ.Emissor;

public class Receptor {

	private static String queueName;
	private static String prompt;

	public static void showPrompt(String destination){

		if(destination != "doNothing")
			if(destination == "none")
				prompt = ">> ";
			else
				prompt = destination + ">> ";

		System.out.print(prompt);

	}

	public static void startReceiving(
		String host,
		String username,
		String password
		) throws Exception 
{
	ConnectionFactory factory = new ConnectionFactory();
	factory.setHost(host); 
	factory.setUsername(username); 
	factory.setPassword(password); 
	factory.setVirtualHost("/");
	Connection connection = factory.newConnection();
	Channel channel = connection.createChannel();

	queueName = '@' + username;

	channel.queueDeclare(queueName, false,   false,     false,       null);

	Consumer consumer = new DefaultConsumer(channel) {
		public void handleDelivery(
				String consumerTag, 
				Envelope envelope, 
				AMQP.BasicProperties properties, 
				byte[] body) throws IOException 
		{

			String message = new String(body, "UTF-8");
			System.out.print("\n" + message + "\n");
			showPrompt("doNothing");

		}
};

	while(true){

		channel.basicConsume(queueName, true,    consumer);
		Scanner sc = new Scanner(System.in);
		showPrompt("none");
		String destination = sc.nextLine();
		if(destination.charAt(0) == '@'){

			String message;
			while(true){

				showPrompt(destination);
				message = sc.nextLine();
				if(message.charAt(0) == '!')
					break;
				if(message.charAt(0) == '@'){

					destination = message;
					break;

				}

				Emissor.sendMessage(host, 
						username, 
						password, 
						destination, 
						message
					);

			} 

		}

	}

}

  public static void main(String[] argv) throws Exception{

	  if(argv.length < 3){

		  String str = "Uso: java -jar <jarfile> <host_ip> <usuario> <senha>";
		  System.out.println(str);
		  return;

	  }

	  String host = argv[0];
	  String username = argv[1];
	  String password = argv[2];

	  startReceiving(host, username, password);

  }

}
