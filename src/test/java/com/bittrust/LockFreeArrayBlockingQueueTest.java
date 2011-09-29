package com.bittrust;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LockFreeArrayBlockingQueueTest {
	
	private LockFreeArrayBlockingQueue<Integer> q;
	
	private static final int QUEUE_SIZE = 4;
	private static final int QUEUE_TIMEOUT = 10;

	@Before
	public void setUp() throws Exception {
		q = new LockFreeArrayBlockingQueue<Integer>(QUEUE_SIZE, QUEUE_TIMEOUT);
	}

	private void fillQueue() {
		for(int i=0; i < QUEUE_SIZE; ++i)
			assertTrue(q.offer(i));
	}
	
	@Test
	public void testPollEmptyQueue() {
		assertNull(q.poll());
	}

	@Test
	public void testPollFullQueue() {
		fillQueue();
		
		for(int i = QUEUE_SIZE; i > 0; --i) {
			Integer ret = q.poll();
			
			assertNotNull(ret);
			assertEquals((Integer)(QUEUE_SIZE - i), ret);
		}
		
		assertNull(q.poll());
	}
	
	@Test
	public void testPollOffer() {
		fillQueue();
		
		assertNotNull(q.poll());
		assertTrue(q.offer(0));
	}

	@Test
	public void testOfferEmptyQueue() {
		assertTrue(q.offer(0));
	}

	@Test
	public void testOfferFullQueue() {
		fillQueue();
		
		assertFalse(q.offer(0));
	}

}
