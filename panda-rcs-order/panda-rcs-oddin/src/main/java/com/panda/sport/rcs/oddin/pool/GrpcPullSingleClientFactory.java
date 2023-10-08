package com.panda.sport.rcs.oddin.pool;

import com.panda.sport.rcs.oddin.client.GrpcPullSingleClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

@Slf4j
public class GrpcPullSingleClientFactory extends BasePooledObjectFactory<GrpcPullSingleClient> {


    @Override
    public GrpcPullSingleClient create() throws Exception {
        return new GrpcPullSingleClient();
    }

    @Override
    public PooledObject<GrpcPullSingleClient> wrap(GrpcPullSingleClient client) {
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void destroyObject(PooledObject<GrpcPullSingleClient> p) throws Exception {
        log.info("==== GrpcClientFactory#destroyObject ====");
        p.getObject().requestStreamObserver.onCompleted();
        super.destroyObject(p);
    }

}
