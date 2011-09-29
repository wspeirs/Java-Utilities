/**
 * @author William R. Speirs <bill.speirs@gmail.com>
 */
package com.bittrust;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wspeirs
 *
 */
public class LockFreeArrayBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {

	private static final long serialVersionUID = -6874611347291297986L;
	
	private final AtomicInteger read;
	private final AtomicInteger maxRead;
	private final AtomicInteger write;
	private final E[] items;
	private final long timeout;
	
	@SuppressWarnings("unchecked")
	public LockFreeArrayBlockingQueue(int capcity, long timeout) {
		this.items = (E[]) new Object[capcity + 1];
		this.read = new AtomicInteger(0);
		this.maxRead = new AtomicInteger(0);
		this.write = new AtomicInteger(0);
		this.timeout = timeout;
	}
	
	private boolean cmpAndInc(AtomicInteger integer, int expected) {
		return integer.compareAndSet(expected, (expected + 1) % items.length);
	}

	public E peek() {
		int read = this.read.get();
		
		return read == this.maxRead.get() ? null : items[read];
	}

	public E poll() {
		int read;
		int maxRead;
		E ret;
		
		do {
			// get the current values for read & maxRead
			read = this.read.get();
			maxRead = this.maxRead.get();
			
			// if read equals maxRead, then nothing in the queue to read
			if(read == maxRead)
				return null;
			
			// temporarily get this item as it's possibly the return value
			ret = items[read];
			
			// check to see that this.read is what we think it should be, and increment
		} while(! cmpAndInc(this.read, read));
		
		// by this point we have a valid value from the array
		return ret;
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		E ret = poll();
		
		if(ret == null) {
			Thread.sleep(unit.toMillis(timeout));
			ret = poll();
		}
		
		return ret;
	}

	public boolean offer(E e) {
		int read;
		int write;
		
		do {
			// get the current values for read & write
			read = this.read.get();
			write = this.write.get();
			
			// if we were to increase write and it equaled read, then the array is full
			if((write+1) % items.length == read)
				return false;
			
			// while write is NOT what we expect, increase it and try the whole thing again
		} while(! cmpAndInc(this.write, write));
		
		// at this point write has been "reserved" for us
		items[write % items.length] = e;
		
		// now we must increment the maxRead value, allowing any waiting threads to read
		// however, we only want to increment maxRead to our write value (what was "reserved" for us)
		while(! cmpAndInc(this.maxRead, write)) {
			Thread.yield();	// since we interrupted another thread, yield to them
		}
		
		return true;
	}

	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		boolean ret = offer(e);
		
		if(ret == false) {
			Thread.sleep(unit.toMillis(timeout));
			ret = offer(e);
		}
		
		return ret;
	}

	public E take() throws InterruptedException {
		return poll(timeout, TimeUnit.MILLISECONDS);
	}

	public void put(E e) throws InterruptedException {
		offer(e, timeout, TimeUnit.MILLISECONDS);
	}

	public int drainTo(Collection<? super E> c) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int drainTo(Collection<? super E> c, int maxElements) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int remainingCapacity() {
		return items.length - size();
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		int read = this.read.get();
		int maxRead = this.maxRead.get();
		
		return read <= maxRead ? maxRead - read : items.length - read + maxRead;
	}

}
