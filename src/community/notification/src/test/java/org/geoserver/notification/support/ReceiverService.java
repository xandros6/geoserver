/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.notification.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReceiverService {

    private CountDownLatch latch = new CountDownLatch(1);
    
    private List<byte[]> messages;

    private int messageNumber;

    public ReceiverService(int number) {
        this.messageNumber = number;
        messages = new ArrayList<byte[]>(number);
    }
    
    public void manage(byte[] message){
        this.messages.add(message);
        if(this.messages.size() ==  this.messageNumber){
            latch.countDown();
        }
    }
    
    public List<byte[]> getMessages() throws InterruptedException{
        latch.await(2000,TimeUnit.MILLISECONDS);
        return this.messages;
    }
    

}
