package org.example.common.io.sound;

import java.io.Serializable;

public class RpcAudioFormat implements Serializable {

    private float sampleRate;

    private int sampleSizeInBits;

    private int channels;

    private boolean signed;

    private boolean bigEndian;

    public RpcAudioFormat(){}

    public RpcAudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian) {
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
        this.signed = signed;
        this.bigEndian = bigEndian;
    }

    public float sampleRate() {
        return sampleRate;
    }

    public int sampleSizeInBits() {
        return sampleSizeInBits;
    }

    public int channels() {
        return channels;
    }

    public boolean signed() {
        return signed;
    }

    public boolean bigEndian() {
        return bigEndian;
    }
}
