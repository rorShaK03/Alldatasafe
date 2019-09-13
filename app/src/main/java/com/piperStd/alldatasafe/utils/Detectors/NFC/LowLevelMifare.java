package com.piperStd.alldatasafe.utils.Detectors.NFC;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;

public class LowLevelMifare
{

    Tag tag = null;
    MifareClassic classic = null;

    byte[] keyA;
    byte[] keyB;

    public static String[] knownTech = {MifareClassic.class.getName()};

    public LowLevelMifare(Intent intent)
    {
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        classic = MifareClassic.get(tag);
    }
    public LowLevelMifare(Intent intent, byte[] keyA)
    {
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        classic = MifareClassic.get(tag);
        this.keyA = keyA;
    }

    public LowLevelMifare(Intent intent, byte[] keyA, byte[] keyB)
    {
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        classic = MifareClassic.get(tag);
        this.keyA = keyA;
        this.keyB = keyB;
    }

}
