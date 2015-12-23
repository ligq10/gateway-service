package com.changhongit.loving.upactor;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.changhongit.loving.entity.Authentication;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.message.AuthFailed;
import com.changhongit.loving.message.AuthSucceed;
import com.changhongit.loving.message.RegisterConnection;
import com.changhongit.loving.message.SendMessage;
import com.changhongit.loving.message.UnRegisterConnection;
import com.changhongit.loving.repository.AuthenticationRepository;
import com.changhongit.loving.repository.TerminalRestRepository;
import com.changhongit.loving.response.CommonResponse;

public class AuthenticationHandler extends UntypedActor {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	final ActorRef mainHandler;
	
	private ActorSelection connectionManager;
	
	final ApplicationContext ctx;
	
	private AuthenticationRepository authenticationRepository;
	
	private TerminalRestRepository terminalRestRepository;
	
	private int retryTimes = 0;
	
	public AuthenticationHandler(ActorRef mainHandler, ApplicationContext ctx) {
		this.mainHandler = mainHandler;
		this.ctx = ctx;
		authenticationRepository = ctx.getBean(AuthenticationRepository.class);
		terminalRestRepository = ctx.getBean(TerminalRestRepository.class);
	}
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		connectionManager = getContext().actorSelection(
				"akka://cp150/user/cp150Connections");
	}
	
	public boolean isValidAuthMessage(String message) {
		retryTimes++;
		return message.startsWith("C P") && message.contains("|0x0005|");
	}
	
	public String getResponse(String message) {
		String[] params = message.split(" ");
		CommonResponse response = new CommonResponse(params[2], params[3], "0");
		return response.formatToString();
	}
	
	public void processMessage(String message) {
		registerToManager(message);
		saveMessage(message);
	}
	
	private void registerToManager(String message) {
		String[] params = message.split(" ");
		String imei = params[3];
		Terminal terminal = terminalRestRepository.findByImei(imei);
		if (terminal != null
				&& ("未激活".equals(terminal.getStatus()) || terminal.getStatus() == null)) {
			terminal.setActivateTime(new Date());
			terminal.setStatus("正常");
			terminalRestRepository.save(terminal);
		}
		connectionManager.tell(new RegisterConnection(imei, mainHandler),
				getSelf());
	}
	
	private void saveMessage(String message) {
		String[] params = message.split(" ");
		Authentication authentication = new Authentication();
		authentication.setImei(params[3]);
		authentication.setMessage(message);
		authentication.setDate(new Date());
		authenticationRepository.save(authentication);
	}
	
	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			String message = (String) msg;
			if (isValidAuthMessage(message)) {
				log.debug("message matched authentication.");
				processMessage(message);
				mainHandler.tell(new AuthSucceed(message.split(" ")[3]),
						getSelf());
				String response = getResponse(message);
				if (!StringUtils.isEmpty(response)) {
					mainHandler.tell(new SendMessage(response), getSelf());
				}
			} else if (retryTimes > 2) {
				mainHandler.tell(new AuthFailed(), getSelf());
				log.error("auth failed with message: {}", message);
			}
		} else if (msg instanceof UnRegisterConnection) {
			connectionManager.tell(msg, getSelf());
		}
	}
}
