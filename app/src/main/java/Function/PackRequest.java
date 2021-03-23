package Function;

import android.util.Log;

import com.example.parking.HeadProto;
import com.example.parking.LoginProto;
import com.google.protobuf.InvalidProtocolBufferException;

import java.security.acl.LastOwnerException;
import java.util.Map;

public class PackRequest {

    private static final String TAG = "PackRequest";




    public static byte[] packMsg(String head, byte[] body)  {


        int bodySize = body.length;


        HeadProto.Head headProto = HeadProto.Head.newBuilder().setHead(head).build();
        byte[] headInfo = headProto.toByteArray();

        byte[] pack = new byte[4+headInfo.length+bodySize];
        Log.i(TAG,"head size is "+headInfo.length);

        System.arraycopy(intToByteArray(headInfo.length),0,pack,0,4);
        System.arraycopy(headInfo,0,pack,4,headInfo.length);
        System.arraycopy(body,0,pack,4+headInfo.length,bodySize);

        Log.i(TAG,"packMsg is "+pack.toString());

        return pack;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        Log.i(TAG,"result is "+result.toString());
        return result;
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }
        return value;
    }
}
