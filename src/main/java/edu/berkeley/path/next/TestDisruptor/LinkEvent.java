package edu.berkeley.path.next.TestDisruptor;

/**
 * Created by mauricemanning on 10/6/14.
 */
public class LinkEvent {
    private byte[] value;

    public void set(byte[] value)
    {
        this.value = value;
    }
    public byte[] get()
    {
        return this.value;
    }
}
