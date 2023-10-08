package com.panda.sport.rcs.oddin.pool;

import com.panda.sport.rcs.oddin.client.GrpcTicketClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

@Slf4j
public class GrpcTicketClientFactory extends BasePooledObjectFactory<GrpcTicketClient> {


    @Override
    public GrpcTicketClient create() throws Exception {
        return new GrpcTicketClient();
    }

    @Override
    public PooledObject<GrpcTicketClient> wrap(GrpcTicketClient client) {
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void destroyObject(PooledObject<GrpcTicketClient> p) throws Exception {
        log.info("==== GrpcClientFactory#destroyObject ====");
        p.getObject().orderObserver.onCompleted();
        super.destroyObject(p);
    }

}
