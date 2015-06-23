/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\swssm\\workspace\\VoiceToView\\VoiceToViewApp\\src\\com\\swmem\\voicetoview\\service\\IRemoteServiceCallback.aidl
 */
package com.swmem.voicetoview.service;
public interface IRemoteServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.swmem.voicetoview.service.IRemoteServiceCallback
{
private static final java.lang.String DESCRIPTOR = "com.swmem.voicetoview.service.IRemoteServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.swmem.voicetoview.service.IRemoteServiceCallback interface,
 * generating a proxy if needed.
 */
public static com.swmem.voicetoview.service.IRemoteServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.swmem.voicetoview.service.IRemoteServiceCallback))) {
return ((com.swmem.voicetoview.service.IRemoteServiceCallback)iin);
}
return new com.swmem.voicetoview.service.IRemoteServiceCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_messageCallback:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.messageCallback(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.swmem.voicetoview.service.IRemoteServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void messageCallback(int msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(msg);
mRemote.transact(Stub.TRANSACTION_messageCallback, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_messageCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void messageCallback(int msg) throws android.os.RemoteException;
}
