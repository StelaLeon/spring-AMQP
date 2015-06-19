package amqp.springTest;

import java.util.concurrent.CountDownLatch;

public class Receiver {

	private CountDownLatch latch = new CountDownLatch(1);
	
	public void receiveMessage(String message){
		System.out.println("we're got message" +message);
		latch.countDown();
	}

	public CountDownLatch getLatch() {
		// TODO Auto-generated method stub
		return latch;
	}
}
