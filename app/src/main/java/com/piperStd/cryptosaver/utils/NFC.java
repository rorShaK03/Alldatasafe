package com.piperStd.cryptosaver.utils;

import android.icu.text.AlphabeticIndex;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.piperStd.cryptosaver.utils.tools.showException;

public class NFC
{
    public static final byte TYPE_DATA = 0;
    public static final byte TYPE_KEY = 1;

    private Tag tag;
    private NdefMessage ndefMessage;


    public NFC(Bundle bundle)
    {
        tag = (Tag)bundle.get(NfcAdapter.EXTRA_TAG);
        ndefMessage = (NdefMessage)((Parcelable[])bundle.get(NfcAdapter.EXTRA_NDEF_MESSAGES))[0];

    }

    private void writeMessage(NdefMessage msg)
    {
        Ndef ndef = Ndef.get(tag);
            try
            {
                ndef.connect();
                ndef.writeNdefMessage(msg);
                ndef.close();
            }
            catch(Exception e)
            {
                showException(this, e.getMessage());
            }
    }

    public void writeTag(byte type, byte[] data)
    {
        NdefRecord record = null;
        switch(type)
        {
            case TYPE_DATA:
                record = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,
                        "com.piperstd.cryptosaver:data".getBytes(Charset.forName("US-ASCII")), new byte[0], data);
                break;
            case TYPE_KEY:
                record = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,
                        "com.piperstd.cryptosaver:data".getBytes(Charset.forName("US-ASCII")), new byte[0], data);
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
