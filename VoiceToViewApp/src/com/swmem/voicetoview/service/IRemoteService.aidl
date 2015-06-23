package com.swmem.voicetoview.service;
 
import com.swmem.voicetoview.service.IRemoteServiceCallback;
 
interface IRemoteService {
    void registerCallback(IRemoteServiceCallback callback);
    void unregisterCallback(IRemoteServiceCallback callback);
    void getChunkList();
}