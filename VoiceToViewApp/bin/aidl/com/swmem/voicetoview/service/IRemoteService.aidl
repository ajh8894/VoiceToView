package com.swmem.voicetoview.service;

import com.swmem.voicetoview.service.IRemoteServiceCallback;
import com.swmem.voicetoview.data.Chunk;

interface IRemoteService {
    void registerCallback(IRemoteServiceCallback callback);
    void unregisterCallback(IRemoteServiceCallback callback);
    List<Chunk> getChunkList();
	void createHideView();
	void createAssistantView();
}