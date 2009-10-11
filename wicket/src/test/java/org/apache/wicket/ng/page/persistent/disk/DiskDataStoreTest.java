package org.apache.wicket.ng.page.persistent.disk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.apache.wicket.ng.page.persistent.AsynchronousDataStore;
import org.apache.wicket.ng.page.persistent.DataStore;
import org.apache.wicket.util.lang.Checks;

public class DiskDataStoreTest extends TestCase
{

	public DiskDataStoreTest()
	{
		
	}

	private static final Random random = new Random();
	private static final int FILE_SIZE_MIN = 1024 * 200;
	private static final int FILE_SIZE_MAX = 1024 * 300;
	private static final int MAX_SIZE_PER_SESSION = 1000000;
	private static final int FILE_CHANNEL_POOL_CAPACITY = 100;
	private static final int SESSION_COUNT = 50;
	private static final int FILES_COUNT = 1000;
	private static final int SLEEP_MAX = 10;
	private static final int THREAD_COUNT = 20;
	private static final int READ_MODULO = 100;
	
	private static class File
	{
		private final String sessionId;
		private final int id;
		
		private byte first;
		private byte last;
		private int length;
		
		public File(String sessionId, int id)
		{
			this.sessionId = sessionId;
			this.id = id;
		}
		
		public String getSessionId()
		{
			return sessionId;
		}
		
		public int getId()
		{
			return id;
		}
		
		public byte[] generateData()
		{
			length = FILE_SIZE_MIN + random.nextInt(FILE_SIZE_MAX - FILE_SIZE_MIN);
			byte data[] = new byte[length];
			random.nextBytes(data);
			first = data[0];
			last = data[data.length - 1];
			return data;
		}
		
		public boolean checkData(byte data[])
		{
			Checks.argumentNotNull(data, "data");
			if (data.length != length)
			{
				return false;
			}
			if (first != data[0])
			{
				return false;
			}
			if (last != data[data.length - 1])
			{
				return false;
			}
			return true;
		}
	}
		
	private Map<String, AtomicInteger> sessionCounter = new ConcurrentHashMap<String, AtomicInteger>();
	private ConcurrentLinkedQueue<File> filesToSave = new ConcurrentLinkedQueue<File>();
	private ConcurrentLinkedQueue<File> filesToRead1 = new ConcurrentLinkedQueue<File>();
	private ConcurrentLinkedQueue<File> filesToRead2 = new ConcurrentLinkedQueue<File>();
	
	private AtomicInteger read1Count = new AtomicInteger(0);
	private AtomicInteger read2Count = new AtomicInteger(0);
	private AtomicInteger saveCount = new AtomicInteger(0);
	
	private AtomicBoolean saveDone = new AtomicBoolean(false);
	private AtomicBoolean read1Done = new AtomicBoolean(false);
	private AtomicBoolean read2Done = new AtomicBoolean(false);
	
	private AtomicInteger failures = new AtomicInteger();
	
	private AtomicInteger bytesWritten = new AtomicInteger(0);
	private AtomicInteger bytesRead = new AtomicInteger(0);
	
	private AtomicInteger saveTime = new AtomicInteger(0);
	
	private String randomSessionId()
	{
		List<String> s = new ArrayList<String>(sessionCounter.keySet());
		return s.get(random.nextInt(s.size()));
	}
	
	private int nextSessionId(String sessionId)
	{
		AtomicInteger i = sessionCounter.get(sessionId);
		return i.incrementAndGet();
	}
	
	private void generateFiles()
	{
		for (int i = 0; i < SESSION_COUNT; ++i)
		{
			sessionCounter.put(UUID.randomUUID().toString(), new AtomicInteger(0));
		}
		for (int i = 0; i < FILES_COUNT; ++i)
		{
			String session = randomSessionId();
			File file = new File(session, nextSessionId(session));
			long now = System.nanoTime();
			filesToSave.add(file);
			long duration = System.nanoTime() - now;
			saveTime.addAndGet((int) duration);
		}
		
	}
	
	private DataStore dataStore;
	
	private class SaveRunnable implements Runnable
	{
		public void run()
		{
			File file;
			
			while ((file = filesToSave.poll()) != null || saveCount.get() < FILES_COUNT)
			{
				if (file != null)
				{					
					byte data[] = file.generateData();
					dataStore.storeData(file.getSessionId(), file.getId(), data);
					
					if (saveCount.get() % READ_MODULO == 0)
					{
						filesToRead1.add(file);
					}
					saveCount.incrementAndGet();
					bytesWritten.addAndGet(data.length);
				}
					
				try
				{
					Thread.sleep(random.nextInt(SLEEP_MAX));
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}						
			
			saveDone.set(true);			
		}		
	};
	
	private class Read1Runnable implements Runnable
	{
		public void run()
		{
			File file;
			while ((file = filesToRead1.poll()) != null || !saveDone.get())
			{
				if (file != null)
				{
					byte bytes[] = dataStore.getData(file.getSessionId(), file.getId());
					if (file.checkData(bytes) == false)
					{
						failures.incrementAndGet();
					}
					filesToRead2.add(file);	
					read1Count.incrementAndGet();
					bytesRead.addAndGet(bytes.length);
				}
								
				try
				{
					Thread.sleep(random.nextInt(SLEEP_MAX));
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}			
			
			read1Done.set(true);
		}		
	};
	
	
	private class Read2Runnable implements Runnable
	{
		public void run()
		{
			File file;
			while ((file = filesToRead2.poll()) != null || !read1Done.get())
			{
				if (file != null)
				{
					byte bytes[] = dataStore.getData(file.getSessionId(), file.getId());
					if (file.checkData(bytes) == false)
					{
						failures.incrementAndGet();
					}	
					read2Count.incrementAndGet();
					bytesRead.addAndGet(bytes.length);
				}
								
				try
				{
					Thread.sleep(random.nextInt(SLEEP_MAX));
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}							
			}
			
			read2Done.set(true);			
		}
	};
	
	private void doTestDataStore()
	{
		System.out.println("Starting...");
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < THREAD_COUNT; ++i)
		{
			new Thread(new SaveRunnable()).start();
		}
		
		for (int i = 0; i < THREAD_COUNT; ++i)
		{
			new Thread(new Read1Runnable()).start();
		}
		
		for (int i = 0; i < THREAD_COUNT; ++i)
		{
			new Thread(new Read2Runnable()).start();
		}
		
		while(!(read1Done.get() && read2Done.get() && saveDone.get()))
		{
			try
			{
				Thread.sleep(5);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		long duration = System.currentTimeMillis() - start;
		
		System.out.println("Took: " + duration + " ms");
		System.out.println("Save: " + saveCount.intValue() + " files, " + bytesWritten.get() +" bytes");
		System.out.println("Read: " + (read1Count.get() + read2Count.get()) + " files, " + bytesRead.get() + " bytes");

		System.out.println("Average save time (ns): " + (double) saveTime.get() / (double) saveCount.get());
		
		assertEquals(0, failures.get());
		
		for (String s : sessionCounter.keySet())
		{
			dataStore.removeData(s);
		}		
	}
	
	public void test1()
	{
		generateFiles();		
		
		dataStore = new DiskDataStore("app1", MAX_SIZE_PER_SESSION, FILE_CHANNEL_POOL_CAPACITY);
		dataStore = new AsynchronousDataStore(dataStore);
		
		doTestDataStore();
		
		dataStore.destroy();
	}


}
