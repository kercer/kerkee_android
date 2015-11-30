package com.kercer.kerkee.imagesetter;

import com.kercer.kercore.debug.KCLog;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author zihong
 *
 */
public class KCWebImageSetter extends Thread
{
    private static final long SHUTDOWN_TIMEOUT_IN_MS = 100;
    private final Queue<KCWebImageSetterTask> pendingTasks;
    private boolean isRunning;

    public KCWebImageSetter()
    {
        super("KCWebImageLoader");
        pendingTasks = new LinkedList<KCWebImageSetterTask>();
    }

    @Override
    public void run()
    {
        KCLog.i("Starting up task " + getName());
        KCWebImageSetterTask task;
        isRunning = true;
        while (isRunning)
        {
            synchronized (pendingTasks)
            {
                while (pendingTasks.isEmpty() && isRunning)
                {
                    try
                    {
                        pendingTasks.wait();
                    }
                    catch (InterruptedException e)
                    {
                        isRunning = false;
                        break;
                    }
                }

                try
                {
                    if (!canExecute(pendingTasks))
                        continue;
                    task = getNextTask(pendingTasks);
                }
                catch (Exception e)
                {
                    continue;
                }
            }

            try
            {
                if (task != null)
                {
                    task.executeTask();
                }
            }
            catch (Exception e)
            {
            }
        }

        KCLog.i("Shutting down task " + getName());
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
        isRunning = false;
    }

    public void addTask(KCWebImageSetterTask aTask)
    {
        synchronized (pendingTasks)
        {
            pendingTasks.add(aTask);
            pendingTasks.notifyAll();
//            KCLog.i("addTask succeeded:" + aTask.mUrl);
        }
    }

    private boolean canExecute(Queue<KCWebImageSetterTask> aTaskQueue)
    {
        KCWebImageSetterTask task = aTaskQueue.peek();
        return (task == null) ? false : task.canExecute();
    }

    private KCWebImageSetterTask getNextTask(Queue<KCWebImageSetterTask> aTaskQueue)
    {
        // Pop the first element from the pending request queue
        KCWebImageSetterTask task = aTaskQueue.poll();

        return task;
    }

    public void shutdown()
    {
        try
        {
            interrupt();
            join(SHUTDOWN_TIMEOUT_IN_MS);
        }
        catch (InterruptedException e)
        {
        }
    }

}
