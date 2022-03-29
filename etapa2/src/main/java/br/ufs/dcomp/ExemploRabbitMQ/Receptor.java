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
			if(destination == "none"
					|| destination.charAt(0) == '!')
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

			Message.Mensagem mensagem = Message.Mensagem
				.parseFrom(body);

			String emissor = mensagem.getEmissor();
			String data = mensagem.getData();
			String hora = mensagem.getHora();
			String grupo = mensagem.getGrupo();

			Message.Conteudo conteudo = mensagem.getConteudo();
			String tipo = conteudo.getTipo();
			String nome = conteudo.getNome();
			byte[] corpo = conteudo.getCorpo().toByteArray();

			String corpoStr = new String(corpo);
			String toPrint  = "(" + data + " às " + hora + ")"+" "+emissor+" diz: " + corpoStr;
			System.out.println("\n" + toPrint);
			showPrompt("doNothing");
				

		}
	};

	Scanner sc = new Scanner(System.in);
	while(true){

		channel.basicConsume(queueName, true,    consumer);
		showPrompt("none");
		String destination = sc.nextLine();
		String grupo = "";

		if(destination.charAt(0) == '!'){

			if(destination.equals("!exit"))
				break;

			String[] fPart = destination.split(" ");

			if(fPart[0].equals("!addGroup"))
				channel.exchangeDeclare(
						'#'+fPart[1], 
						"fanout");

			if(fPart[0].equals("!addUser")){

				String queue = fPart[1];
				String exchange = fPart[2];
				System.out.println(queue);
				System.out.println(exchange);
				channel.queueBind(queue, exchange, "");

			}

			if(fPart[0].equals("!delFromGroup")){

				String queue = fPart[1];
				String exchange = fPart[2];
				channel.queueUnbind(queue, exchange, "");

			}

			if(fPart[0].equals("!removeGroup")){

				String exchange = fPart[1];
				channel.exchangeDelete(exchange);

			}

		}

		if(destination.charAt(0) == '@' 
				|| destination.charAt(0) == '#'){

			String message;
			while(true){

				showPrompt(destination);
				message = sc.nextLine();

				if(destination.charAt(0) == '#')
					grupo = destination;

				if(message.charAt(0) == '@'){

					destination = message;
					grupo = "";
					break;

				}

				if(message.equals("!exit"))
					break;

				Emissor.sendMessage(host, 
						username, 
						password, 
						destination, 
						message,
						grupo
					);

			} 

		}

	}

	sc.close();

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
