package pteidj.utils;

import pteidlib.PTEID_ADDR;
import pteidlib.PTEID_ID;
import pteidlib.PteidException;
import pteidlib.pteid;

/**
 * Created by Andre on 25/01/2016.
 */
public class Info {

    public PTEID_ID getIdinfo(){
        try
        {
            System.loadLibrary("pteidlibj");
        }
        catch (UnsatisfiedLinkError e)
        {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }

        PTEID_ID idinfo = null;
        try {
            idinfo = pteid.GetID();
            return idinfo;

        }catch(PteidException exception){
            exception.printStackTrace();
        }

        return idinfo;
    }

    public PTEID_ADDR getAddrinfo(){
        try
        {
            System.loadLibrary("pteidlibj");
        }
        catch (UnsatisfiedLinkError e)
        {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }

        try {
           return pteid.GetAddr();
        }catch(PteidException exception){
            exception.printStackTrace();
        }

        return null;
    }



    public String getFullName(){
        PTEID_ID id = getIdinfo();

        String fullname = id.firstname + " " + id.name;
        return fullname;
    }




}
