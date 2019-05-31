package com.piperStd.alldatasafe.utils;

import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PatternMatcher;

import java.nio.charset.StandardCharsets;

import static com.piperStd.alldatasafe.utils.tools.contains;
import static com.piperStd.alldatasafe.utils.tools.showException;

public class NFC
{
    public static final byte TYPE_DATA = 0;
    public static final byte TYPE_KEY = 1;

    private Tag tag;
    private NdefMessage ndefMessage = null;
    private boolean isNdefFormatable = false;
    private boolean isNdefFormat = false;

    public static String[] knownTech = { NfcA.class.getName(), NfcB.class.getName(),  NdefFormatable.class.getName(), MifareClassic.class.getName()};


    public NFC(Bundle bundle)
    {
        tag = (Tag)bundle.get(NfcAdapter.EXTRA_TAG);
        if(contains(tag.getTechList(), Ndef.class.getName()))
        {
            isNdefFormat = true;
            Parcelable[] parcelable = (Parcelable[]) bundle.get(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(parcelable != null)
            {
                ndefMessage = (NdefMessage)parcelable[0];
            }
        }
        else if(contains(tag.getTechList(), NdefFormatable.class.getName()))
        {
            isNdefFormatable = true;
            isNdefFormat = false;
        }

    }

    public static IntentFilter createNdefFilter(byte type)
    {
        IntentFilter NdefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try
        {
            NdefFilter.addDataScheme("vnd.android.nfc");
            NdefFilter.addDataAuthority("ext", null);
            switch(type)
            {
                case TYPE_DATA:
                    NdefFilter.addDataPath("/com.piperstd.alldatasafe:data", PatternMatcher.PATTERN_PREFIX);
                    break;
                case TYPE_KEY:
                    NdefFilter.addDataPath("/com.piperstd.alldatasafe:key", PatternMatcher.PATTERN_PREFIX);
                    break;
            }
        }
        catch(Exception e)
        {
            showException(NFC.class.getName(), e.getMessage());
        }
        return NdefFilter;
    }

    public static IntentFilter createTechFilter()
    {
        IntentFilter techFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        return techFilter;
    }

    private void writeMessage(NdefMessage msg)
    {
        if(isNdefFormat)
        {
            Ndef ndef = Ndef.get(tag);
            try
            {
                ndef.connect();
                ndef.writeNdefMessage(msg);
                ndef.close();
            }
            catch (Exception e)
            {
                showException(this, e.getMessage());
            }
        }
        else if(isNdefFormatable)
        {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            try
            {
                ndefFormatable.connect();
                ndefFormatable.format(msg);
                ndefFormatable.close();
            }
            catch (Exception e)
            {
                //Объект ошибки e почему-то возвращает null из getMessage()
                showException("NdefFormatable", "Couldn`t format to ndef");
            }
        }
    }

    public void writeTag(byte type, byte[] data)
    {
        NdefRecord record = null;
        switch(type)
        {
            // заменить UTF_8 на US_ASCII, если не работает
            case TYPE_DATA:
                record = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,
                        "com.piperstd.alldatasafe:data".getBytes(StandardCharsets.US_ASCII), new byte[0], data);
                break;
            case TYPE_KEY:
                record = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,
                        "com.piperstd.alldatasafe:key".getBytes(StandardCharsets.US_ASCII), new byte[0], data);
                break;
        }
        NdefRecord[] records = {record};
        NdefMessage message = new NdefMessage(records);
        writeMessage(message);
    }

    public byte[] readTag()
    {
        NdefRecord record;
        if(ndefMessage != null)
        {
            record = ndefMessage.getRecords()[0];
            return record.getPayload();
        }
        return null;
    }
}
