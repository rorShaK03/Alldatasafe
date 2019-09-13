package com.piperStd.alldatasafe.utils.Cryptographics;

import android.os.Build;
import android.os.Process;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class HighEntropyRandom
{

    private static final File URANDOM = new File("/dev/urandom");



    private static DataOutputStream getUrandomOutputStream()
    {
        try
        {
                return new DataOutputStream(new FileOutputStream(URANDOM));
        }
        catch(Exception e)
        {
            throw new SecurityException("Failed to open /dev/urandom");
        }
    }

    private static DataInputStream getUrandomInputStream()
    {
        try
        {
                return new DataInputStream(new FileInputStream(URANDOM));
        }
        catch(Exception e)
        {
            throw new SecurityException("Failed to open /dev/urandom");
        }
    }

    public static void genRandomFromUrandom(byte[] random)
    {
        try
        {
            getUrandomInputStream().readFully(random);
        }
        catch (Exception e)
        {
            throw new SecurityException("Couldn`t gen random from urandom");
        }
    }


    public static byte[] genSeed()
    {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(byteStream);
            dataStream.writeLong(System.currentTimeMillis());
            dataStream.writeLong(System.nanoTime());
            dataStream.writeInt(Process.myPid());
            dataStream.writeInt(Process.myUid());
            dataStream.write(getFingerprint());
            dataStream.close();
            return byteStream.toByteArray();
        }
        catch(Exception e)
        {
            showException(HighEntropyRandom.class, e.getMessage());
            return null;
        }

    }

    private static byte[] getFingerprint()
    {
        byte[] fingerprint = Build.FINGERPRINT.getBytes(StandardCharsets.UTF_8);
        if(fingerprint != null)
            return fingerprint;
        else
        {
            showException(HighEntropyRandom.class, "Couldn`t get device fingeprint");
            return null;
        }
    }

}
