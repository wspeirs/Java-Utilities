package com.bittrust;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;

public class LockFreeArrayBlockingQueueSpeedTest {

	private LockFreeArrayBlockingQueue<Integer> lfq;
	private ArrayBlockingQueue<Integer> aq;
	private LinkedBlockingQueue<Integer> lq;
	
	private static final int QUEUE_SIZE = 1000000;
	private static final int QUEUE_TIMEOUT = 10;

	@Before
	public void setUp() throws Exception {
		lfq = new LockFreeArrayBlockingQueue<Integer>(QUEUE_SIZE, QUEUE_TIMEOUT);
		aq = new ArrayBlockingQueue<Integer>(QUEUE_SIZE);
		lq = new LinkedBlockingQueue<Integer>(QUEUE_SIZE);
	}
	
	private void fillQueue(BlockingQueue<Integer> bq) {
		for(int i=0; i < QUEUE_SIZE; ++i) {
			bq.offer(Integer.valueOf(i));
		}
	}
	
	@Test
	public void insertTest() {
		long start;
		long end;
		
		System.out.println("* INSERT TEST *");
		
		// test the lock-free array queue
		start = System.currentTimeMillis();
		fillQueue(lfq);
		end = System.currentTimeMillis();
		
		assertEquals(QUEUE_SIZE, lfq.size());
		
		System.out.println("LOCK FREE QUEUE: " + (end - start) + "ms");
		
		
		// test the array queue
		start = System.currentTimeMillis();
		fillQueue(aq);
		end = System.currentTimeMillis();
		
		assertEquals(QUEUE_SIZE, aq.size());
		
		System.out.println("    ARRAY QUEUE: " + (end - start) + "ms");
		
		
		// test the lock-free array queue
		start = System.currentTimeMillis();
		fillQueue(lq);
		end = System.currentTimeMillis();
		
		assertEquals(QUEUE_SIZE, lq.size());
		
		System.out.println("   LINKED QUEUE: " + (end - start) + "ms");
		
		System.out.println();
	}

	@Test
	public void removeTest() {
		long start;
		long end;
		
		System.out.println("* REMOVE TEST *");
		
		// test the lock-free array queue
		fillQueue(lfq);
		
		start = System.currentTimeMillis();
		for(int i=0; i < QUEUE_SIZE; ++i)
			lfq.poll();
		end = System.currentTimeMillis();
		
		assertEquals(0, lfq.size());
		
		System.out.println("LOCK FREE QUEUE: " + (end - start) + "ms");
		
		
		// test the array queue
		fillQueue(aq);

		start = System.currentTimeMillis();
		for(int i=0; i < QUEUE_SIZE; ++i)
			aq.poll();
		end = System.currentTimeMillis();
		
		assertEquals(0, aq.size());
		
		System.out.println("    ARRAY QUEUE: " + (end - start) + "ms");
		
		
		// test the lock-free array queue
		fillQueue(lq);

		start = System.currentTimeMillis();
		for(int i=0; i < QUEUE_SIZE; ++i)
			lq.poll();
		end = System.currentTimeMillis();
		
		assertEquals(0, lq.size());
		
		System.out.println("   LINKED QUEUE: " + (end - start) + "ms");
	}
}
