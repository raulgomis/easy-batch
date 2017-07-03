package org.easybatch.extensions.kafka;

import java.io.Serializable;

public interface ConnectionFactory<T> extends Serializable {

    /**
     * <p>Title: createConnection</p>
     * <p>Description: Returns a connection</p>
     *
     * @return Connection
     * @throws Exception
     */
    T createConnection() throws Exception;
}
