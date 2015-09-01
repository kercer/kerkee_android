package com.kercer.kerkee.bridge;

import java.util.HashMap;

/**
 * 
 * @author zihong
 *
 */
public class KCArgList
{
//    private List<String> mArgsKeys = new ArrayList<String>();
    private HashMap<String, KCArg> mArgs = new HashMap<String, KCArg>();
    
    
    public int size()
    {
        return mArgs.size();
    }
    
    public boolean addArg(KCArg aArg)
    {
        if (aArg == null) return false;
        String key = aArg.getArgName();
//        mArgsKeys.add(key);
        mArgs.put(key, aArg);
        return true;
    }
    
    public Object getArgValue(String aKey)
    {
        Object obj = null;
        if (aKey != null)
        {
            KCArg arg = mArgs.get(aKey);
            if (arg != null)
                obj = arg.getValue();
        }
        
        return obj;
    }
    
    public String getArgValueString(String aKey)
    {
    	Object value = getArgValue(aKey);
        return value == null ? null : value.toString();
    }
    
    public HashMap<String, KCArg> getArgs()
    {
        return mArgs;
    }
    
    @Override
    public String toString()
    {
        return mArgs.values().toString();
    }
}
