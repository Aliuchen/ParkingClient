package com.example.parking.interfaces;

import java.util.concurrent.ExecutionException;

public interface ICommunication {

    boolean sendMessage(byte[] message);
}
