package com.t0ugh.server.callback;

import com.t0ugh.sdk.callback.Callback;
import com.t0ugh.sdk.proto.Proto;

import java.util.ArrayList;
import java.util.List;

public class CallbackTestImpl implements Callback {

    public List<Proto.Request> requestList = new ArrayList<>();
    public List<Proto.Response> responseList = new ArrayList<>();


    @Override
    public void callback(Proto.Request request, Proto.Response response) {
        requestList.add(request);
        responseList.add(response);
    }
}
