package com.kercer.kerkee.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zihong
 * This TaskQueue is used to queue up tasks that are supposed
 * to run sequentially, which helps avoid problems related to
 * thread-safety.
 */
public class KCTaskQueue extends Thread
{
    private BlockingQueue<Object> mQueue;

    public KCTaskQueue()
    {
        init("Queue");
    }
    
    public KCTaskQueue(String aQueueName)
    {
        init(aQueueName);
    }
    
    //init queue
    private void init(String aQueueName)
    {
        setName(aQueueName);
        mQueue = new LinkedBlockingQueue<Object>();
    }

    @Override
    public synchronized void start()
    {
        super.start();
    }

    public synchronized void stopTaskQueue()
    {
        // use 'Poison Pill Shutdown' to stop the task queue
        // add a non-Runnable object, which will be recognized as the command
        // by the thread to break the infinite loop
        mQueue.add(new Object());
    }

    public synchronized void scheduleTask(Runnable aTask)
    {
        mQueue.add(aTask);
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Object obj = mQueue.take();
                if (obj instanceof Runnable)
                    ((Runnable) obj).run();
                else
                    break;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
